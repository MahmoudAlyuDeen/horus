package com.afterapps.horus.model;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.afterapps.horus.R;
import com.afterapps.horus.beans.HistoryEntry;
import com.afterapps.horus.beans.Stock;
import com.afterapps.horus.widget.StocksProvider;
import com.evernote.android.job.Job;

import org.greenrobot.eventbus.EventBus;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import needle.Needle;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;

/*
 * Created by mahmoudalyudeen on 4/22/17.
 */

public class StocksJob extends Job {

    public static final String TAG = "StocksJobTag";

    @NonNull
    @Override
    protected Result onRunJob(Params params) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Stock> stocks = realm.where(Stock.class).findAll();
        for (final Stock stock : stocks) {
            try {
                final yahoofinance.Stock yahooStock = YahooFinance.get(stock.getSymbol().toUpperCase(), true);
                if (isBadSymbol(yahooStock)) {
                    handleBadSymbol(realm, stock);
                    continue;
                }
                handleSuccess(realm, stock, yahooStock);
            } catch (IOException exception) {
                if (exception instanceof FileNotFoundException) {
                    handleBadSymbol(realm, stock);
                } else {
                    showOfflineMessage();
                    realm.close();
                    updateHomeScreenWidget();
                    EventBus.getDefault().post(new JobFinishedEvent());
                    return Result.RESCHEDULE;
                }
            }
        }
        realm.close();
        updateHomeScreenWidget();
        EventBus.getDefault().post(new JobFinishedEvent());
        return Result.SUCCESS;
    }

    private void updateHomeScreenWidget() {
        Context context = getContext().getApplicationContext();
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName stocksWidget = new ComponentName(context, StocksProvider.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(stocksWidget);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_stocks_list_view);
    }

    private boolean isBadSymbol(yahoofinance.Stock yahooStock) {
        return yahooStock == null ||
                yahooStock.getQuote() == null ||
                yahooStock.getQuote().getPrice() == null ||
                yahooStock.getQuote().getChangeInPercent() == null ||
                yahooStock.getQuote().getChange() == null;
    }

    private void showOfflineMessage() {
        Needle.onMainThread().execute(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), R.string.message_offline, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleSuccess(Realm realm, final Stock stock, final yahoofinance.Stock yahooStock) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                saveQuote(realm, stock, yahooStock);
                try {
                    saveHistory(yahooStock.getHistory(), stock.getSymbol(), realm);
                } catch (IOException ignored) {
                    //exception will never be called as history is side loaded with the stock
                }
            }
        });
    }

    private void handleBadSymbol(Realm realm, final Stock stock) {
        final String symbol = stock.getSymbol();
        Needle.onMainThread().execute(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(),
                        String.format(getContext().getString(R.string.message_stock_deleted_format),
                                symbol),
                        Toast.LENGTH_SHORT).show();
            }
        });
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                stock.deleteFromRealm();
            }
        });
    }

    private void saveQuote(Realm realm, Stock stock, yahoofinance.Stock yahooStock) {
        stock.setChangeAbs(yahooStock.getQuote().getChange().floatValue());
        stock.setChangePer(yahooStock.getQuote().getChangeInPercent().floatValue());
        stock.setPrice(yahooStock.getQuote().getPrice().floatValue());
        stock.setHandled();
        realm.copyToRealmOrUpdate(stock);
    }

    private void saveHistory(List<HistoricalQuote> history, String symbol, Realm realm) {
        for (HistoricalQuote historicalQuote : history) {
            HistoryEntry historyEntry = new HistoryEntry();
            historyEntry.setSymbol(symbol);
            historyEntry.setTimestamp(historicalQuote.getDate().getTimeInMillis());
            historyEntry.setClose(historicalQuote.getClose().floatValue());
            historyEntry.setPrimaryKey(historicalQuote.getDate() + symbol);
            realm.copyToRealmOrUpdate(historyEntry);
        }
    }

    public class JobFinishedEvent {
    }
}

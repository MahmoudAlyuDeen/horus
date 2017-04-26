package com.afterapps.horus.widget;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.afterapps.horus.Constants;
import com.afterapps.horus.R;
import com.afterapps.horus.beans.Stock;
import com.afterapps.horus.history.HistoryActivity;

import java.text.DecimalFormat;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/*
 * Created by mahmoudalyudeen on 4/25/17.
 */

public class StocksProvider implements RemoteViewsService.RemoteViewsFactory {

    private Context context;
    private List<Stock> mStocks;

    StocksProvider(StocksWidgetService stocksWidgetService, Intent intent) {
        context = stocksWidgetService;
    }

    @Override
    public void onCreate() {
        loadData();
    }

    private void loadData() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Stock> stocks = realm.where(Stock.class)
                .equalTo("handled", true)
                .findAll();
        mStocks = realm.copyFromRealm(stocks);
        realm.close();
    }

    @Override
    public void onDataSetChanged() {
        loadData();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return mStocks.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews itemRemoteView = new RemoteViews(context.getPackageName(),
                R.layout.item_stock_widget);
        Stock stock = mStocks.get(position);
        if (stock != null && stock.isValid()) {
            setStock(stock, itemRemoteView);
        }
        return itemRemoteView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private void setStock(Stock stock, RemoteViews itemRemoteView) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        boolean isChangePositive = stock.getChangeAbs() > 0;
        String priceChangePrefix = context.getString(isChangePositive ?
                R.string.prefix_plus : R.string.prefix_minus);
        String currencySign = context.getString(R.string.currency_sing_dollar);
        String absoluteChange = decimalFormat.format(stock.getChangeAbs());
        String percentageChange = decimalFormat.format(stock.getChangePer());
        String price = decimalFormat.format(stock.getPrice());
        itemRemoteView.setTextViewText(R.id.item_stock_symbol_text_view, stock.getSymbol());
        itemRemoteView.setTextViewText(R.id.item_stock_price_text_view,
                String.format(context.getString(R.string.stock_price_format), currencySign, price));
        itemRemoteView.setTextViewText(R.id.item_stock_change_abs_text_view,
                String.format(context.getString(R.string.absolute_change_format), priceChangePrefix, currencySign, absoluteChange));
        itemRemoteView.setTextViewText(R.id.item_stock_change_per_text_view,
                String.format(context.getString(R.string.percentage_change_format), priceChangePrefix, percentageChange));
        itemRemoteView.setImageViewResource(R.id.item_stock_direction_image_view, isChangePositive ? R.drawable.ic_up : R.drawable.ic_down);
        itemRemoteView.setOnClickFillInIntent(R.id.item_stock_parent, getHistoryIntent(stock));
    }

    private Intent getHistoryIntent(Stock stock) {
        Intent history = new Intent(context, HistoryActivity.class);
        history.putExtra(Constants.HISTORY_EXTRA_SYMBOL, stock.getSymbol());
        return history;
//        return PendingIntent.getActivity(
//                context,
//                0,
//                history,
//                PendingIntent.FLAG_UPDATE_CURRENT
//        );
    }
}

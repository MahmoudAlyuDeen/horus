package com.afterapps.horus.home;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afterapps.horus.BaseActivity;
import com.afterapps.horus.R;
import com.afterapps.horus.beans.Stock;
import com.afterapps.horus.model.StocksJob;
import com.afterapps.horus.widgets.EmptyStateRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity
        extends BaseActivity<MainView, MainPresenter>
        implements MainView, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.main_toolbar)
    Toolbar mMainToolbar;
    @BindView(R.id.main_recycler)
    EmptyStateRecyclerView mMainRecycler;
    @BindView(R.id.main_swipe_refresh)
    SwipeRefreshLayout mMainSwipeRefresh;
    @BindView(R.id.main_empty_state)
    TextView mMainEmptyState;

    private Realm realm;

    @NonNull
    @Override
    public MainPresenter createPresenter() {
        return new MainPresenter();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mMainToolbar);
        realm = Realm.getDefaultInstance();
        setupSwipeRefresh();
        if (savedInstanceState == null) {
            onRefresh();
        }
        EventBus.getDefault().register(this);
        displayStocks();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoadingFinished(StocksJob.JobFinishedEvent jobFinishedEvent) {
        isLoading = false;
        displayLoadingState();
    }

    private void displayStocks() {
        RealmResults<Stock> stocks = realm.where(Stock.class)
                .equalTo("handled", true)
                .findAll();
        StocksAdapter stocksAdapter = new StocksAdapter(stocks, this);
        mMainRecycler.setAdapter(stocksAdapter);
        mMainRecycler.setLayoutManager(new LinearLayoutManager(this));
        mMainRecycler.setEmptyView(mMainEmptyState);
    }

    private void setupSwipeRefresh() {
        mMainSwipeRefresh.setColorSchemeResources(R.color.colorAccent);
        mMainSwipeRefresh.setOnRefreshListener(this);
    }

    @Override
    protected void displayLoadingState() {
        mMainSwipeRefresh.setRefreshing(isLoading);
    }

    @Override
    protected void discardData() {

    }

    @Override
    public void onRefresh() {
        if (!isLoading) {
            isLoading = true;
            displayLoadingState();
            presenter.loadStocks();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_stock:
                new MaterialDialog.Builder(this)
                        .title(R.string.dialog_title_add_stock)
                        .content(R.string.dialog_content_add_stock)
                        .inputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                        .input(R.string.hint_symbol, R.string.empty_string, false,
                                new MaterialDialog.InputCallback() {
                                    @Override
                                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                        if (input != null && !input.toString().trim().isEmpty()) {
                                            handleNewSymbol(input.toString().trim().toUpperCase());
                                        }
                                    }
                                }).show();
                return true;
            default:
                return false;
        }
    }

    private void handleNewSymbol(final String symbol) {
        Stock stock = realm.where(Stock.class)
                .equalTo("symbol", symbol)
                .findFirst();
        if (stock != null) {
            return;
        }
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Stock stock = new Stock();
                stock.setSymbol(symbol);
                realm.copyToRealmOrUpdate(stock);
                onRefresh();
            }
        });
    }
}

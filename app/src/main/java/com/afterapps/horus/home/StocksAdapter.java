package com.afterapps.horus.home;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afterapps.horus.R;
import com.afterapps.horus.beans.Stock;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

/*
 * Created by mahmoudalyudeen on 4/22/17.
 */

class StocksAdapter extends RealmRecyclerViewAdapter<Stock, StocksAdapter.StockViewHolder> {

    private final Context context;

    StocksAdapter(@Nullable OrderedRealmCollection<Stock> data, Context context) {
        super(data, true);
        this.context = context;
    }

    @Override
    public StockViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_stock, parent, false);
        return new StockViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(StockViewHolder holder, int position) {
        if (getData() != null) {
            Stock stock = getData().get(position);
            if (stock != null && stock.isValid()) {
                holder.setStock(stock);
            }
        }
    }

    class StockViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.item_stock_symbol_text_view)
        TextView mItemStockSymbolTextView;
        @BindView(R.id.item_stock_price_text_view)
        TextView mItemStockPriceTextView;
        @BindView(R.id.item_stock_change_per_text_view)
        TextView mItemStockChangePerTextView;
        @BindView(R.id.item_stock_change_abs_text_view)
        TextView mItemStockChangeAbsTextView;
        @BindView(R.id.item_stock_parent)
        RelativeLayout mItemStockParent;

        StockViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mItemStockParent.setOnClickListener(this);
        }

        void setStock(Stock stock) {
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            mItemStockSymbolTextView.setText(stock.getSymbol());
            mItemStockPriceTextView.setText(decimalFormat.format(stock.getPrice()));
            mItemStockChangeAbsTextView.setText(decimalFormat.format(stock.getChangeAbs()));
            mItemStockChangePerTextView.setText(
                    String.format(context.getString(R.string.percentage_format),
                            decimalFormat.format(stock.getChangePer())));
        }

        @Override
        public void onClick(View v) {

        }
    }

}

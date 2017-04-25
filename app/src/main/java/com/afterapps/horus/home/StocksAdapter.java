package com.afterapps.horus.home;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afterapps.horus.Constants;
import com.afterapps.horus.R;
import com.afterapps.horus.beans.Stock;
import com.afterapps.horus.history.HistoryActivity;

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
        @BindView(R.id.item_stock_direction_image_view)
        ImageView mItemStockDirectionImageView;

        StockViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mItemStockParent.setOnClickListener(this);
        }

        void setStock(Stock stock) {
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            boolean isChangePositive = stock.getChangeAbs() > 0;
            String priceChangePrefix = context.getString(isChangePositive ?
                    R.string.prefix_plus : R.string.prefix_minus);
            String currencySign = context.getString(R.string.currency_sing_dollar);
            String absoluteChange = decimalFormat.format(stock.getChangeAbs());
            String percentageChange = decimalFormat.format(stock.getChangePer());
            String price = decimalFormat.format(stock.getPrice());
            mItemStockSymbolTextView.setText(stock.getSymbol());
            mItemStockPriceTextView.setText(
                    String.format(context.getString(R.string.stock_price_format), currencySign, price));
            mItemStockChangeAbsTextView.setText(
                    String.format(context.getString(R.string.absolute_change_format), priceChangePrefix, currencySign, absoluteChange));
            mItemStockChangePerTextView.setText(
                    String.format(context.getString(R.string.percentage_change_format), priceChangePrefix, percentageChange));
            mItemStockDirectionImageView.setImageResource(isChangePositive ? R.drawable.ic_up : R.drawable.ic_down);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.item_stock_parent:
                    if (getData() != null) {
                        Stock stock = getData().get(getLayoutPosition());
                        if (stock != null && stock.isValid()) {
                            Intent history = new Intent(context, HistoryActivity.class);
                            history.putExtra(Constants.HISTORY_EXTRA_SYMBOL, stock.getSymbol());
                            context.startActivity(history);
                        }
                    }
                    break;
            }
        }
    }

}

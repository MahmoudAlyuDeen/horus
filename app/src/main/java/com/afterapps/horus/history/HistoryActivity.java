package com.afterapps.horus.history;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;

import com.afterapps.horus.BaseActivity;
import com.afterapps.horus.Constants;
import com.afterapps.horus.R;
import com.afterapps.horus.beans.HistoryEntry;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

@SuppressWarnings({"WeakerAccess", "CanBeFinal"})
public class HistoryActivity
        extends BaseActivity<HistoryView, HistoryPresenter>
        implements HistoryView {

    @BindView(R.id.history_toolbar)
    Toolbar mHistoryToolbar;
    @BindView(R.id.history_bar_chart)
    BarChart mHistoryBarChart;
    private Realm realm;

    @NonNull
    @Override
    public HistoryPresenter createPresenter() {
        return new HistoryPresenter();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ButterKnife.bind(this);
        realm = Realm.getDefaultInstance();
        setSupportActionBar(mHistoryToolbar);
        String symbol = getIntent().getStringExtra(Constants.HISTORY_EXTRA_SYMBOL);
        if (symbol == null) {
            showLoadingError();
            finish();
            return;
        }
        setTitle(String.format(getString(R.string.history_title_format), symbol));
        displayHistory(symbol);
    }

    private void displayHistory(String symbol) {
        final RealmResults<HistoryEntry> historyEntries = realm.where(HistoryEntry.class)
                .equalTo("symbol", symbol)
                .findAllSorted("timestamp");
        List<BarEntry> barEntries = new ArrayList<>(0);
        for (float i = 0; i < historyEntries.size(); i++) {
            HistoryEntry historyEntry = historyEntries.get((int) i);
            BarEntry barEntry = new BarEntry(i, historyEntry.getClose());
            barEntries.add(barEntry);
        }
        BarDataSet barDataSet = new BarDataSet(barEntries, "");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        BarData data = new BarData(barDataSet);
        XAxis xAxis = mHistoryBarChart.getXAxis();
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return DateFormat.format("MMMM", historyEntries.get((int) value).getTimestamp()).toString();
            }

            @Override
            public int getDecimalDigits() {
                return 0;
            }
        });
        mHistoryBarChart.setData(data);
        Description description = new Description();
        description.setText("");
        mHistoryBarChart.setDescription(description);
        mHistoryBarChart.invalidate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    protected void displayLoadingState() {

    }

}

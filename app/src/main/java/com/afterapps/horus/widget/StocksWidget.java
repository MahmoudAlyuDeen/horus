package com.afterapps.horus.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.afterapps.horus.R;
import com.afterapps.horus.model.StocksJob;
import com.evernote.android.job.JobRequest;

/*
 * Created by mahmoudalyudeen on 4/25/17.
 */

public class StocksWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        scheduleUpdate();
        for (int appWidgetId : appWidgetIds) {
            updateStocksWidget(context, appWidgetManager, appWidgetId);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    private void scheduleUpdate() {
        new JobRequest.Builder(StocksJob.TAG)
                .setExecutionWindow(1, 1000)
                .setPersisted(true)
                .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                .setBackoffCriteria(5000, JobRequest.BackoffPolicy.LINEAR)
                .setUpdateCurrent(true)
                .build()
                .schedule();
    }

    private void updateStocksWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.stocks_widget);
        views.setRemoteAdapter(R.id.widget_stocks_list_view, new Intent(context, StocksWidgetService.class));
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}

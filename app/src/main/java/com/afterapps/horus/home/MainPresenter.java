package com.afterapps.horus.home;

import com.afterapps.horus.model.StocksJob;
import com.evernote.android.job.JobRequest;
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;

/*
 * Created by mahmoudalyudeen on 4/19/17.
 */

class MainPresenter extends MvpBasePresenter<MainView> {

    void loadStocks() {
        new JobRequest.Builder(StocksJob.TAG)
                .setExecutionWindow(1, 1000)
                .setPersisted(true)
                .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                .setBackoffCriteria(5000, JobRequest.BackoffPolicy.LINEAR)
                .setUpdateCurrent(true)
                .build()
                .schedule();
    }
}

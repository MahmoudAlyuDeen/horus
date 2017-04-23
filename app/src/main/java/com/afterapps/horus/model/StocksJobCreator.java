package com.afterapps.horus.model;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

/*
 * Created by mahmoudalyudeen on 4/22/17.
 */

public class StocksJobCreator implements JobCreator {

    @Override
    public Job create(String tag) {
        switch (tag) {
            case StocksJob.TAG:
                return new StocksJob();
            default:
                return null;
        }
    }

}

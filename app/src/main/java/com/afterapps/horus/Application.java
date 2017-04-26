package com.afterapps.horus;

/*
 * Created by mahmoud on 1/9/17.
 */


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.afterapps.horus.home.MainActivity;
import com.afterapps.horus.model.StocksJobCreator;
import com.evernote.android.job.JobManager;

import net.vrallev.android.cat.Cat;

import io.realm.Realm;

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        JobManager.create(this).addJobCreator(new StocksJobCreator());
        Realm.init(this);
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {
                Log.e("@@@@@", "uncaughtException: " + throwable.toString());
                Cat.e(throwable);
                Intent splash = new Intent(getApplicationContext(), MainActivity.class);
                splash.putExtra(Constants.CRASHED_FLAG, true);
                PendingIntent splashPending = PendingIntent.getActivity(getBaseContext(), 0, splash, 0);
                AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, splashPending);
                System.exit(2);
            }
        });
    }
}

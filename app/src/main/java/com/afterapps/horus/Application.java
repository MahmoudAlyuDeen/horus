package com.afterapps.horus;

/*
 * Created by mahmoud on 1/9/17.
 */


import com.afterapps.horus.model.StocksJobCreator;
import com.evernote.android.job.JobManager;

import io.realm.Realm;

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        JobManager.create(this).addJobCreator(new StocksJobCreator());
        Realm.init(this);

        //todo: enable un-comment out this before release
//        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
//            @Override
//            public void uncaughtException(Thread thread, Throwable throwable) {
//                Log.e("@@@@@", "uncaughtException: " + throwable.toString());
//                Cat.e(throwable);
//                Intent splash = new Intent(getApplicationContext(), SplashActivity.class);
//                splash.putExtra(SPLASH_CRASHED_FLAG, true);
//                PendingIntent splashPending = PendingIntent.getActivity(getBaseContext(), 0, splash, 0);
//                AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, splashPending);
//                System.exit(2);
//            }
//        });
    }
}

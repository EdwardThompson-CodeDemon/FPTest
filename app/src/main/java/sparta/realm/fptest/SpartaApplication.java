package sparta.realm.fptest;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;



import sparta.realm.Realm;
import sparta.realm.Services.DatabaseManager;
import sparta.realm.fptest.RealmDynamics.spartaDynamics;
import sparta.realm.spartautils.svars;
import sparta.realm.utils.AppConfig;


public class SpartaApplication extends Application {

    private static Context appContext;


    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
        AppConfig UIPA_APP = new AppConfig("http://ta.cs4africa.com:9090",
                /*"http://ta.cs4africa.com:2222/api/AppStore/LoadApp"*/null,
                "U.I.P.A." ,
                "MAIN CAMPUS",
                "/Authentication/Login/Submit",false

        );

        svars.DB_NAME="fp_dt_test";
svars.DB_PASS="764512";


        Realm.Initialize(this,new spartaDynamics(), BuildConfig.VERSION_NAME,BuildConfig.APPLICATION_ID,UIPA_APP);

//        DatabaseManager.database.execSQL("DELETE FROM member_data_table");




        svars.set_sync_interval_mins(this,20);



    }

    void restartApp()
    {
        Intent mStartActivity = new Intent(Realm.context, MainActivity.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(Realm.context, mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager)Realm.context.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 10000, mPendingIntent);
        System.exit(0);
    }




    public static boolean isServiceRunning(Context act,Class<?> serviceClass) {
//        Class<?> serviceClass=App_updates.class;

        ActivityManager manager;
        manager = (ActivityManager) act.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    protected Boolean isActivityRunning(Class activityClass)
    {
        ActivityManager activityManager = (ActivityManager) getBaseContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (ActivityManager.RunningTaskInfo task : tasks) {
            if (activityClass.getCanonicalName().equalsIgnoreCase(task.baseActivity.getClassName()))
                return true;
        }

        return false;
    }
    public static Context getAppContext() {
        return appContext;
    }
}

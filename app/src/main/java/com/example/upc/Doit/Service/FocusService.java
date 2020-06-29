package com.example.upc.Doit.Service;

import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.example.upc.Doit.Activity.ClockActivity;


import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class FocusService extends Service {

    boolean flag = true;// 用于停止线程
    private ArrayList<String> packageList = new ArrayList<>();

    private Timer timer;
    private TimerTask task = new TimerTask() {

        @Override
        public void run() {

            if(isPrevent()){
                Intent intentNewActivity = new Intent(FocusService.this, ClockActivity.class);
                intentNewActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentNewActivity);
            }

        }


    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getInstallApp();
        if (flag == true) {
            timer = new Timer();
            timer.schedule(task, 0, 100);
            flag = false;
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        timer.cancel();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    public void getInstallApp(){
        List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packages.size(); i++) {
            PackageInfo pi = packages.get(i);
            if ((pi.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                // 如果属于非系统程序，则添加到列表
                packageList.add(pi.packageName);

            }
        }
    }


    public String getLauncherPackageName(Context context) {
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        final ResolveInfo res = context.getPackageManager().resolveActivity(intent, 0);
        if (res.activityInfo == null) {
            // should not happen. A home is always installed, isn't it?
            return null;
        }
        if (res.activityInfo.packageName.equals("android")) {
            // 有多个桌面程序存在，且未指定默认项时；
            return null;
        } else {
            return res.activityInfo.packageName;
        }
    }

    public boolean isPrevent(){

        List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);
        ActivityManager mActivityManager;
        mActivityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);

        String packageName ;
        if (Build.VERSION.SDK_INT > 20) {
            UsageStatsManager usageStatsManager = (UsageStatsManager) getApplicationContext().getSystemService("usagestats");

            long ts = System.currentTimeMillis();
            List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST,0, ts);
            UsageStats recentStats = null;
            for (UsageStats usageStats : queryUsageStats) {
                if (recentStats == null || recentStats.getLastTimeUsed() < usageStats.getLastTimeUsed()) {
                    recentStats = usageStats;
                }
            }
            packageName = recentStats != null ? recentStats.getPackageName() : null;
        } else{

            packageName = mActivityManager.getRunningTasks(1).get(0).topActivity.getPackageName();
            //Log.i(TAG,packageName);
        }
        /*
        ComponentName topActivity = mActivityManager.getRunningTasks(1).get(0).topActivity;
        String packageName = topActivity.getPackageName();
        */
        Context context = getApplicationContext();
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        for (int i = 0; i < packageList.size(); i++) {
            if(packageName.equals(packageList.get(i)) && !packageName.equals("com.example.upc.todolist") ){
                Log.i("FocusService", "阻止"+ packageName);
                return true;
            }

        }
        return false;
    }
}

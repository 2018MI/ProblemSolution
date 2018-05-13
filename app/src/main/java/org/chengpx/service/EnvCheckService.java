package org.chengpx.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import org.chengpx.R;
import org.chengpx.domain.EnvBean;
import org.chengpx.domain.RoadBean;
import org.chengpx.util.SpUtils;
import org.chengpx.util.net.NetUtil;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * create at 2018/5/10 16:41 by chengpx
 */
public class EnvCheckService extends Service {

    private Timer mTimer;
    private EnvBean[] mEnvBeanArr = {
            new EnvBean("temperature", "温度", new int[]{0, 37}), new EnvBean("humidity", "湿度", new int[]{20, 80}),
            new EnvBean("LightIntensity", "光照强度", new int[]{1, 5000}), new EnvBean("co2", "co2", new int[]{350, 7000}),
            new EnvBean("pm2.5", "pm2.5", new int[]{0, 300}), new EnvBean("RoadStatus", "道路状态", new int[]{1, 5})
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        mTimer = new Timer();
        mTimer.schedule(new MyTimerTask(), 0, 1000 * 10);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTimer.cancel();
        mTimer = null;
        EventBus.getDefault().unregister(this);
    }

    public static void start(Context context) {
        context.startService(new Intent(context, EnvCheckService.class));
    }

    public static void stop(Context context) {
        context.stopService(new Intent(context, EnvCheckService.class));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getAllSense(Map<String, Double> map) {
        SpUtils spUtils = SpUtils.getInstance(this);
        for (int index = 0; index < mEnvBeanArr.length -1; index++) {
            EnvBean envBean = mEnvBeanArr[index];
            int anInt = spUtils.getInt(envBean.getSenseName(), -1);
            if (anInt == -1) {
                continue;
            }
            Integer val = map.get(envBean.getSenseName()).intValue();
            if (val > anInt) {
                sendNotification(envBean.getSenseDesc(), val, anInt, index);
            } else {
                clearNotification(index);
            }
        }
    }

    private void clearNotification(int flag) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.cancel(flag);
    }

    private void sendNotification(String senseDesc, Integer val, int yuzhi, int flag) {
        Notification notification = new NotificationCompat.Builder(this).setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(new SimpleDateFormat("yyyy年MM月dd日").format(Calendar.getInstance(Locale.CHINA).getTime())
                        + senseDesc + "值:" + val + "超出阈值范围:" + yuzhi)
                .setAutoCancel(true).build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.notify(flag, notification);
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getRoadStatus(RoadBean roadBean) {
        SpUtils spUtils = SpUtils.getInstance(this);
        EnvBean envBean = mEnvBeanArr[mEnvBeanArr.length - 1];
        int anInt = spUtils.getInt(envBean.getSenseName(), -1);
        if (anInt == -1) {
            return;
        }
        Integer val = roadBean.getStatus();
        if (val > anInt) {
            sendNotification(envBean.getSenseDesc(), val, anInt, mEnvBeanArr.length - 1);
        } else {
            clearNotification(mEnvBeanArr.length - 1);
        }
    }

    public static boolean isRunning(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        assert activityManager != null;
        List<ActivityManager.RunningServiceInfo> runningServiceInfoList = activityManager.getRunningServices(1000);
        for (ActivityManager.RunningServiceInfo runningServiceInfo : runningServiceInfoList) {
            if (EnvCheckService.class.getName().equals(runningServiceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            NetUtil.getNetUtil().addRequest("GetAllSense", null, Map.class);
            Map<String, Integer> values = new HashMap<>();
            values.put("RoadId", 1);
            NetUtil.getNetUtil().addRequest("GetRoadStatus", values, RoadBean.class);
        }
    }

}

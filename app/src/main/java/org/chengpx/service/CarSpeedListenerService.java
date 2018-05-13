package org.chengpx.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import org.chengpx.BaseService;
import org.chengpx.domain.CaroverspeedhistoryBean;
import org.chengpx.util.SpUtils;
import org.chengpx.util.db.CaroverspeedhistoryBeanDao;
import org.chengpx.util.net.NetUtil;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class CarSpeedListenerService extends BaseService {

    private static int sCar_speed_yuzhi;
    private Timer mTimer;
    private CaroverspeedhistoryBean[] mCaroverspeedhistoryBeanArr = {
            new CaroverspeedhistoryBean(1), new CaroverspeedhistoryBean(2), new CaroverspeedhistoryBean(3), new CaroverspeedhistoryBean(4)
    };
    private int mReqGetCarSpeedIndex;
    private CaroverspeedhistoryBeanDao mCaroverspeedhistoryBeanDao;
    private String mTag = getClass().getName();
    private AlertDialog mAlertDialog;

    @Override
    protected void init() {
        EventBus.getDefault().register(this);
        mCaroverspeedhistoryBeanDao = CaroverspeedhistoryBeanDao.getInstance(this);
        sCar_speed_yuzhi = SpUtils.getInstance(this).getInt("car_speed_yuzhi", 60);
        mTimer = new Timer();
        mTimer.schedule(new MyTimerTask(), 0, 1000 * 10);
    }

    @Override
    protected void onDie() {
        mTimer.cancel();
        mTimer = null;
        mReqGetCarSpeedIndex = 0;
        sCar_speed_yuzhi = 60;
        EventBus.getDefault().unregister(this);
    }

    public static void start(Context context) {
        context.startService(new Intent(context, CarSpeedListenerService.class));
    }

    public static void stop(Context context) {
        context.stopService(new Intent(context, CarSpeedListenerService.class));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void GetCarSpeed(CaroverspeedhistoryBean caroverspeedhistoryBean) {
        Integer carSpeed = caroverspeedhistoryBean.getCarSpeed();
        if (carSpeed > sCar_speed_yuzhi) {
            CaroverspeedhistoryBean localCaroverspeedhistoryBean = mCaroverspeedhistoryBeanArr[mReqGetCarSpeedIndex];
            localCaroverspeedhistoryBean.setCarSpeed(carSpeed);
            localCaroverspeedhistoryBean.setOverSpeedDateTime(new Date());
            int insert = mCaroverspeedhistoryBeanDao.insert(localCaroverspeedhistoryBean);
            Log.d(mTag, "CaroverspeedhistoryBeanDao insert; " + insert);
            showDialog(null);
        }
        mReqGetCarSpeedIndex++;
        if (mReqGetCarSpeedIndex < mCaroverspeedhistoryBeanArr.length) {
            Map<String, Integer> values = new HashMap<>();
            values.put("CarId", mCaroverspeedhistoryBeanArr[mReqGetCarSpeedIndex].getCarId());
            NetUtil.getNetUtil().addRequest("GetCarSpeed", values, CaroverspeedhistoryBean.class);
        } else {
            Intent intent = new Intent();
            intent.setAction("CarSpeedListenerFragmentBroadcastReceiver");
            sendBroadcast(intent);
        }
    }

    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            mReqGetCarSpeedIndex = 0;
            Map<String, Integer> values = new HashMap<>();
            values.put("CarId", mCaroverspeedhistoryBeanArr[mReqGetCarSpeedIndex].getCarId());
            NetUtil.getNetUtil().addRequest("GetCarSpeed", values, CaroverspeedhistoryBean.class);
        }
    }

    public static class CarSpeedListenerServiceBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            sCar_speed_yuzhi = SpUtils.getInstance(context).getInt("car_speed_yuzhi", 60);
        }
    }

}

package org.chengpx.service;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.chengpx.R;
import org.chengpx.base.BaseService;
import org.chengpx.domain.CarOverSpeedHistoryBean;
import org.chengpx.util.SpUtils;
import org.chengpx.util.db.CarOverSpeedHistoryDao;
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

    private CarOverSpeedHistoryBean[] mCarOverSpeedHistoryBeanArr = {
            new CarOverSpeedHistoryBean(1), new CarOverSpeedHistoryBean(2),
            new CarOverSpeedHistoryBean(3), new CarOverSpeedHistoryBean(4)
    };
    private Timer mTimer;
    private int mReqGetCarSpeedIndex;
    private SpUtils mSpUtils;
    private CarOverSpeedHistoryDao mCarOverSpeedHistoryDao;
    private String mTag = getClass().getName();

    @Override
    protected void init() {
        EventBus.getDefault().register(this);
        mSpUtils = SpUtils.getInstance(this);
        mCarOverSpeedHistoryDao = CarOverSpeedHistoryDao.getInstance(this);
        mTimer = new Timer();
        mTimer.schedule(new MyTimerTask(), 0, 1000 * 30);
    }

    @Override
    protected void onDie() {
        mTimer.cancel();
        mTimer = null;
        mReqGetCarSpeedIndex = 0;
        mSpUtils = null;
        mCarOverSpeedHistoryDao = null;
        EventBus.getDefault().unregister(this);
    }

    public static void stop(Context context) {
        context.stopService(new Intent(context, CarSpeedListenerService.class));
    }

    public static void start(Context context) {
        context.startService(new Intent(context, CarSpeedListenerService.class));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void GetCarSpeed(CarOverSpeedHistoryBean carOverSpeedHistoryBean) {
        int car_overspeed_yuzhi = mSpUtils.getInt("car_overspeed_yuzhi", 60);
        Integer carSpeed = carOverSpeedHistoryBean.getCarSpeed();
        if (carSpeed > car_overspeed_yuzhi) {
            CarOverSpeedHistoryBean localCarOverSpeedHistoryBean = mCarOverSpeedHistoryBeanArr[mReqGetCarSpeedIndex];
            localCarOverSpeedHistoryBean.setCarSpeed(carSpeed);
            localCarOverSpeedHistoryBean.setOverSpeedDateTime(new Date());
            int insert = mCarOverSpeedHistoryDao.insert(localCarOverSpeedHistoryBean);
            Log.d(mTag, "CarOverSpeedHistoryDao insert: " + insert);
            View view = showDialog(null);
            TextView basefragment_tv_dialogcontent = (TextView) view.findViewById(R.id.basefragment_tv_dialogcontent);
            basefragment_tv_dialogcontent.setText(localCarOverSpeedHistoryBean.getCarId() + "号小车超速,当前速度:" + carSpeed + "速度阈值:" + car_overspeed_yuzhi);
        }
        mReqGetCarSpeedIndex++;
        if (mReqGetCarSpeedIndex < mCarOverSpeedHistoryBeanArr.length) {
            Map<String, Integer> values = new HashMap<>();
            values.put("CarId", mCarOverSpeedHistoryBeanArr[mReqGetCarSpeedIndex].getCarId());
            NetUtil.getNetUtil().addRequest("GetCarSpeed.do", values, CarOverSpeedHistoryBean.class);
        } else {
            Intent intent = new Intent();
            intent.setAction("org.chengpx.fragment.CarSpeedListenerFragment.CarSpeedListenerFragmentBroadcastReceiver");
            sendBroadcast(intent);
        }
    }

    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            mReqGetCarSpeedIndex = 0;
            Map<String, Integer> values = new HashMap<>();
            values.put("CarId", mCarOverSpeedHistoryBeanArr[mReqGetCarSpeedIndex].getCarId());
            NetUtil.getNetUtil().addRequest("GetCarSpeed.do", values, CarOverSpeedHistoryBean.class);
        }
    }
}

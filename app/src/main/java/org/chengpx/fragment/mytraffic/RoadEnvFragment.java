package org.chengpx.fragment.mytraffic;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.chengpx.R;
import org.chengpx.base.BaseFragment;
import org.chengpx.domain.AllSenseBean;
import org.chengpx.util.net.NetUtil;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class RoadEnvFragment extends BaseFragment implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    private TextView roadenvTvPm25val;
    private TextView roadenvTvPm25tip;
    private SurfaceView roadenvSurfaceviewPm25;
    private TextView roadenvTvLightIntensityval;
    private ProgressBar roadenvProgressbarLightintensity;
    private String mTag = getClass().getName();
    private MediaPlayer mMediaPlayer;
    private boolean mIsPause;
    private Timer mTimer;
    private ProgressDialog mEnvLoadDialog;

    @Override
    protected void initListener() {

    }

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_roadenv, container, false);
        roadenvTvPm25val = (TextView) view.findViewById(R.id.roadenv_tv_pm25val);
        roadenvTvPm25tip = (TextView) view.findViewById(R.id.roadenv_tv_pm25tip);
        roadenvSurfaceviewPm25 = (SurfaceView) view.findViewById(R.id.roadenv_surfaceview_pm25);
        roadenvTvLightIntensityval = (TextView) view.findViewById(R.id.roadenv_tv_lightIntensityval);
        roadenvProgressbarLightintensity = (ProgressBar) view.findViewById(R.id.roadenv_progressbar_lightintensity);
        return view;
    }

    @Override
    protected void onDie() {

    }

    @Override
    protected void main() {
        SurfaceHolder surfaceHolder = roadenvSurfaceviewPm25.getHolder();
        surfaceHolder.addCallback(this);
        if (mIsPause && !mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
        }
    }

    @Override
    protected void initData() {
        EventBus.getDefault().register(this);
        mEnvLoadDialog = showLoadingDialog("环境数据加载", "");
        mTimer = new Timer();
        mTimer.schedule(new MyTimerTask(), 0, 3000);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void GetAllSense(AllSenseBean allSenseBean) {
        if (mEnvLoadDialog != null) {
            mEnvLoadDialog.dismiss();
            mEnvLoadDialog = null;
        }
        int pm25254 = allSenseBean.get_$Pm25171();
        roadenvTvPm25val.setText("PM2.5当前值:" + pm25254);
        int lightIntensity = allSenseBean.getLightIntensity();
        roadenvTvLightIntensityval.setText("光照强度当前值:" + lightIntensity);
        roadenvProgressbarLightintensity.setProgress(lightIntensity);
        if (pm25254 > 200) {
            roadenvSurfaceviewPm25.setVisibility(View.VISIBLE);
            notifyMsg("PM2.5大于200,不适合出行", 100);
            roadenvTvPm25tip.setText("友情提示:不适合出行");
        } else {
            roadenvSurfaceviewPm25.setVisibility(View.GONE);
            clearNotify(100);
            roadenvTvPm25tip.setText("友情提示:适合出行");
        }
        if (lightIntensity < 1100) {
            notifyMsg("光照较弱,出行请开灯", 101);
        } else if (lightIntensity > 3190) {
            notifyMsg("光照较强,出行请带墨镜", 101);
        } else {
            clearNotify(101);
        }
    }

    private void clearNotify(int flag) {
        NotificationManager notificationManager = (NotificationManager) mFragmentActivity.getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.cancel(flag);
    }

    private void notifyMsg(String msg, int flag) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mFragmentActivity);
        Notification notification = builder.setContentTitle(msg).setSmallIcon(R.mipmap.ic_launcher).setAutoCancel(true).build();
        NotificationManager notificationManager = (NotificationManager) mFragmentActivity.getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.notify(flag, notification);

    }

    @Override
    protected void onDims() {
        mTimer.cancel();
        mTimer = null;
        mMediaPlayer.pause();
        mIsPause = true;
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(mTag, "surfaceCreated");
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setDisplay(holder);
        try {
            mMediaPlayer.setDataSource(mFragmentActivity, Uri.parse("android.resource://" + mFragmentActivity.getPackageName()
                    + "/" + R.raw.pm));
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(mTag, "surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(mTag, "surfaceDestroyed");
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d(mTag, "onPrepared: ");
        mp.start();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d(mTag, "onCompletion");
        mp.start();
    }

    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            NetUtil.getNetUtil().addRequest("GetAllSense.do", null, AllSenseBean.class);
        }
    }

}

package org.chengpx;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.widget.Toast;

import org.chengpx.service.CarSpeedListenerService;

/**
 * create at 2018/5/11 20:53 by chengpx
 */
public class MyApp extends Application implements Thread.UncaughtExceptionHandler {

    private Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        //Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        e.printStackTrace();
        CarSpeedListenerService.stop(mContext);
        new Thread() {// 使用 Toast 来显示异常信息
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext, "服务器已崩溃请立即重启服务器!",
                        Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        Intent intent = new Intent(mContext, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());  // 结束进程之前可以把你程序的注销或者退出代码放在这段代码之前
    }

}

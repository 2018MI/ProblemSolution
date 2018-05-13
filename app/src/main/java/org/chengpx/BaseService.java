package org.chengpx;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public abstract class BaseService extends Service implements Runnable {

    private AlertDialog mCurrentDialog;

    @Nullable
    @Override
    public final IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public final void onCreate() {
        super.onCreate();
        init();
    }

    protected abstract void init();

    @Override
    public final void onDestroy() {
        super.onDestroy();
        onDie();
        if (mCurrentDialog != null && mCurrentDialog.isShowing()) {
            mCurrentDialog.dismiss();
        }
    }

    protected abstract void onDie();

    public AlertDialog showDialog(View view) {
        if (mCurrentDialog != null && mCurrentDialog.isShowing()) {
            mCurrentDialog.dismiss();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog alertDialog = builder.create();
        Window window = alertDialog.getWindow();
        assert window != null;
        window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alertDialog.show();
        if (view == null) {
            view = View.inflate(this, R.layout.dialog_loading, null);
        }
        window.setContentView(view);
        window.setGravity(Gravity.CENTER);
        mCurrentDialog = alertDialog;
        view.postDelayed(this, 2000);
        return alertDialog;
    }

    @Override
    public void run() {
        if (mCurrentDialog != null && mCurrentDialog.isShowing()) {
            mCurrentDialog.dismiss();
        }
    }

}

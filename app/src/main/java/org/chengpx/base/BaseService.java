package org.chengpx.base;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import org.chengpx.R;

public abstract class BaseService extends Service implements Runnable {

    protected AlertDialog mCurrentDialog;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
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
    }

    protected abstract void onDie();

    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public View showDialog(View view) {
        if (mCurrentDialog != null && mCurrentDialog.isShowing()) {
            mCurrentDialog.dismiss();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog alertDialog = builder.create();
        Window window = alertDialog.getWindow();
        assert window != null;
        window.setGravity(Gravity.CENTER);
        window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alertDialog.show();
        if (view == null) {
            view = View.inflate(this, R.layout.dialog_warn, null);
        }
        alertDialog.setContentView(view);
        mCurrentDialog = alertDialog;
        view.postDelayed(this, 2000);
        return view;
    }

    @Override
    public void run() {
        if (mCurrentDialog != null && mCurrentDialog.isShowing()) {
            mCurrentDialog.dismiss();
        }
    }
}

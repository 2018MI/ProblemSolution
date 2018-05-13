package org.chengpx;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

public abstract class BaseFragment extends Fragment implements Runnable {

    protected FragmentActivity mFragmentActivity;
    private AlertDialog mCurrentDialog;

    @Nullable
    @Override
    public final View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentActivity = getActivity();
        View view = initView(inflater, container, savedInstanceState);
        initListener();
        return view;
    }

    protected abstract void initListener();

    protected abstract View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    @Override
    public final void onDestroyView() {
        super.onDestroyView();
        onDie();
    }

    protected abstract void onDie();

    @Override
    public final void onResume() {
        super.onResume();
        initData();
        main();
    }

    protected abstract void main();

    protected abstract void initData();

    @Override
    public final void onPause() {
        super.onPause();
        onDims();
    }

    protected abstract void onDims();

    public void showToast(String msg) {
        Toast.makeText(mFragmentActivity, msg, Toast.LENGTH_SHORT).show();
    }

    public AlertDialog showDialog(View view) {
        if (mCurrentDialog != null && mCurrentDialog.isShowing()) {
            mCurrentDialog.dismiss();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(mFragmentActivity);
        AlertDialog alertDialog = builder.create();
        Window window = alertDialog.getWindow();
        assert window != null;
        //window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alertDialog.show();
        if (view == null) {
            view = View.inflate(mFragmentActivity, R.layout.dialog_loading, null);
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


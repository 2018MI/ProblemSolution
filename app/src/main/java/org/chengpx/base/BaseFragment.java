package org.chengpx.base;

import android.app.ProgressDialog;
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

public abstract class BaseFragment extends Fragment {

    protected FragmentActivity mFragmentActivity;
    private boolean mIsDestoryCallable;
    private boolean mIsInitCallable = true;

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
        if (mIsInitCallable) {
            initData();
            main();
            mIsInitCallable = false;
            mIsDestoryCallable = true;
        }
    }

    protected abstract void main();

    protected abstract void initData();

    @Override
    public final void onPause() {
        super.onPause();
        if (mIsDestoryCallable) {
            onDims();
            mIsDestoryCallable = false;
            mIsInitCallable = true;
        }
    }

    protected abstract void onDims();

    public void showToast(String msg) {
        Toast.makeText(mFragmentActivity, msg, Toast.LENGTH_SHORT).show();
    }

    public void destory() {
        if (mIsDestoryCallable) {
            onDims();
            mIsDestoryCallable = false;
            mIsInitCallable = true;
        }
    }

    public void init() {
        if (mIsInitCallable) {
            initData();
            main();
            mIsInitCallable = false;
            mIsDestoryCallable = true;
        }
    }

    protected ProgressDialog showLoadingDialog(String title, String msg) {
        ProgressDialog progressDialog = ProgressDialog.show(mFragmentActivity, title, msg);
        Window window = progressDialog.getWindow();
        assert window != null;
        window.setGravity(Gravity.CENTER);
        return progressDialog;
    }

}


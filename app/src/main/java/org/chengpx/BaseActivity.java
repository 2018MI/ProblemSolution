package org.chengpx;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;

/**
 * create at 2018/5/12 20:44 by chengpx
 */
public abstract class BaseActivity extends FragmentActivity {

    @Override
    protected final void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(initView(savedInstanceState));
        initListener();
    }

    protected abstract void initListener();

    protected abstract View initView(Bundle savedInstanceState);

    @Override
    protected final void onDestroy() {
        super.onDestroy();
        onDie();
    }

    protected abstract void onDie();

    @Override
    protected final void onResume() {
        super.onResume();
        initData();
        main();
    }

    protected abstract void main();

    protected abstract void initData();

    @Override
    protected final void onPause() {
        super.onPause();
        onDims();
    }

    protected abstract void onDims();

}

package org.chengpx.fragment.mymsg;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.chengpx.base.BaseFragment;

/**
 * create at 2018/5/19 18:52 by chengpx
 */
public class MsgAnalyFragment extends BaseFragment {
    @Override
    protected void initListener() {

    }

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TextView textView = new TextView(mFragmentActivity);
        textView.setText("消息分析");
        return textView;
    }

    @Override
    protected void onDie() {

    }

    @Override
    protected void main() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onDims() {

    }
}

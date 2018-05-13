package org.chengpx.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.chengpx.BaseFragment;
import org.chengpx.R;

/**
 * 地铁路线查看
 * <p>
 * create at 2018/5/12 15:43 by chengpx
 */
public class SubwayFragment extends BaseFragment {
    @Override
    protected void initListener() {

    }

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subway, container, false);
        return view;
    }

    @Override
    protected void onDie() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void main() {

    }

    @Override
    protected void onDims() {

    }
}

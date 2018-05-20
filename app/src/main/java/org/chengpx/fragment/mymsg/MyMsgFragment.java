package org.chengpx.fragment.mymsg;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.chengpx.R;
import org.chengpx.base.BaseFragment;

/**
 * create at 2018/5/19 18:37 by chengpx
 */
public class MyMsgFragment extends BaseFragment implements ViewPager.OnPageChangeListener, RadioGroup.OnCheckedChangeListener {

    private ViewPager mymsgMyviewpagerContent;
    private RadioGroup mymsgRadiogroupIndicators;
    private String[] mIndciatorArr = {
            "消息查询", "消息分析"
    };
    private BaseFragment[] mBaseFragmentArr = {
            new MsgQueryFragment(), new MsgAnalyFragment()
    };
    private MyAdapter mAdapter;

    @Override
    protected void initListener() {
        mymsgMyviewpagerContent.setOnPageChangeListener(this);
        mymsgRadiogroupIndicators.setOnCheckedChangeListener(this);
    }

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mymsg, container, false);
        mymsgMyviewpagerContent = (ViewPager) view.findViewById(R.id.mymsg_myviewpager_content);
        mymsgRadiogroupIndicators = (RadioGroup) view.findViewById(R.id.mymsg_radiogroup_indicators);
        RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(0, RadioGroup.LayoutParams.WRAP_CONTENT, 1);
        for (int index = 0; index < mIndciatorArr.length; index++) {
            RadioButton radioButton = new RadioButton(mFragmentActivity);
            radioButton.setId(mIndciatorArr[index].hashCode());
            radioButton.setGravity(Gravity.CENTER);
            radioButton.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
            radioButton.setText(mIndciatorArr[index]);
            mymsgRadiogroupIndicators.addView(radioButton, layoutParams);
        }
        return view;
    }

    @Override
    protected void onDie() {

    }

    @Override
    protected void main() {
        mAdapter = new MyAdapter(getChildFragmentManager());
        mymsgMyviewpagerContent.setAdapter(mAdapter);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onDims() {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for (int index = 0; index < mBaseFragmentArr.length; index++) {
            if (index == position) {
                mAdapter.getItem(position).init();
            } else {
                mAdapter.getItem(index).destory();
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        mymsgMyviewpagerContent.setCurrentItem(group.indexOfChild(group.findViewById(checkedId)));
    }

    private class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public BaseFragment getItem(int position) {
            return mBaseFragmentArr[position];
        }

        @Override
        public int getCount() {
            return mBaseFragmentArr.length;
        }
    }
}

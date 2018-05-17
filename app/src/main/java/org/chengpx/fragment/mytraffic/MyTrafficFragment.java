package org.chengpx.fragment.mytraffic;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.chengpx.R;
import org.chengpx.base.BaseFragment;


public class MyTrafficFragment extends BaseFragment implements RadioGroup.OnCheckedChangeListener, ViewPager.OnPageChangeListener {

    private ViewPager mMytrafficViewpagerContent;
    private RadioGroup mMytrafficRadiogroupIndcrute;
    private String[] mIncruteStrArr = {
            "我的路况", "道路环境"
    };
    private BaseFragment[] mFragmentArr = {
            new MyRoadStatusFragment(), new RoadEnvFragment()
    };
    private String mTag = getClass().getName();
    private MyPageAdapter mPageAdapter;

    @Override
    protected void initListener() {
        mMytrafficRadiogroupIndcrute.setOnCheckedChangeListener(this);
        mMytrafficViewpagerContent.setOnPageChangeListener(this);
    }

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mytraffic, container, false);
        mMytrafficViewpagerContent = (ViewPager) view.findViewById(R.id.mytraffic_viewpager_content);
        mMytrafficRadiogroupIndcrute = (RadioGroup) view.findViewById(R.id.mytraffic_radiogroup_indcrute);
        RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(0, RadioGroup.LayoutParams.WRAP_CONTENT, 1);
        for (int index = 0; index < mIncruteStrArr.length; index++) {
            RadioButton radioButton = new RadioButton(mFragmentActivity);
            radioButton.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));// 去除默认原点样式
            radioButton.setId(index);
            radioButton.setText(mIncruteStrArr[index]);
            radioButton.setGravity(Gravity.CENTER);
            mMytrafficRadiogroupIndcrute.addView(radioButton, layoutParams);
        }
        return view;
    }

    @Override
    protected void onDie() {

    }

    @Override
    protected void main() {
        mPageAdapter = new MyPageAdapter(getChildFragmentManager());
        mMytrafficViewpagerContent.setAdapter(mPageAdapter);
        mMytrafficViewpagerContent.setCurrentItem(0);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onDims() {
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        int pos = radioGroup.indexOfChild(radioGroup.findViewById(i));
        mMytrafficViewpagerContent.setCurrentItem(pos);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //Log.d(mTag, "postion = " + position + ", positionOffset = " + positionOffset + ", positionOffsetPixels = " + positionOffsetPixels);
    }

    @Override
    public void onPageSelected(int position) {
        Log.d(mTag, "postion = " + position);
        for (int index = 0; index < mPageAdapter.getCount(); index++) {
            if (position != index) {
                mPageAdapter.getItem(index).destory();
            } else {
                mPageAdapter.getItem(index).init();
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        //Log.d(mTag, "state = " + state);
    }

    private class MyPageAdapter extends FragmentPagerAdapter {

        MyPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public BaseFragment getItem(int position) {
            return mFragmentArr[position];
        }

        @Override
        public int getCount() {
            return mFragmentArr.length;
        }
    }

}

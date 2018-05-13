package org.chengpx.fragment;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.chengpx.BaseFragment;
import org.chengpx.R;
import org.chengpx.domain.AllSenseBean;
import org.chengpx.domain.WeatherBean;
import org.chengpx.util.net.NetUtil;
import org.chengpx.widget.MyGridView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

/**
 * create at 2018/5/4 14:47 by chengpx
 */
public class Test36Fragment extends BaseFragment implements View.OnClickListener {

    private Integer[] mDayArr = {
            0, 1, 2, 3, 4
    };
    private String[] mDateDescArr = {
            "今天", "明天", "后天"
    };
    private int[][] mWeatherRgbArr = {// 深蓝, 淡蓝, 浅灰
            new int[]{0XFF1181E0, 0XFF3698E7, 0XFF65B6EF}, new int[]{0XFF5AB6FA, 0XFF76C1F9, 0XFFA2D2F6},//分别为开始颜色，中间夜色，结束颜色
            new int[]{0XFF8AABCB, 0XFF9FBBD5, 0XFFBBD2E2}
    };
    private WeatherBean[] mHimbernateWeatherBeanArr = {
            new WeatherBean("小雪", R.drawable.xiaoxue, 2), new WeatherBean("中雪", R.drawable.zhongxue, 2),
            new WeatherBean("大雪", R.drawable.daxue, 2), new WeatherBean("暴雪", R.drawable.baoxue, 2),
            new WeatherBean("冻雨", R.drawable.dongyu, 1), new WeatherBean("雨夹雪", R.drawable.yujiaxue, 1),
            new WeatherBean("雷雨冰雹", R.drawable.leiyubingbao, 2), new WeatherBean("小雪转中雪", R.drawable.xiaoxuezhuanzhongxue, 2),
            new WeatherBean("中雪转大雪", R.drawable.zhongxuezhuandaxue, 2), new WeatherBean("大雪转暴雪", R.drawable.daxuezhuanbaoxue, 2)
    };
    private WeatherBean[] mCommonWeatherBeanArr = {
            new WeatherBean("晴朗", R.drawable.qing, 0), new WeatherBean("多云", R.drawable.duoyun, 0),
            new WeatherBean("阴", R.drawable.ying, 1), new WeatherBean("大雾", R.drawable.dawu, 1),
            new WeatherBean("雾霾", R.drawable.wumai, 1), new WeatherBean("小雨", R.drawable.xiaoyu, 2),
            new WeatherBean("中雨", R.drawable.zhongyu, 2), new WeatherBean("大雨", R.drawable.dayu, 2),
            new WeatherBean("大暴雨", R.drawable.dabaoyu, 2), new WeatherBean("雷电", R.drawable.leidian, 2),
            new WeatherBean("雷阵雨", R.drawable.leizhengyu, 2), new WeatherBean("晴转多云", R.drawable.qingzhuanduoyun, 0),
            new WeatherBean("小雨转中雨", R.drawable.xiaoyuzhuangzhongyu, 2), new WeatherBean("大暴雨转特大暴雨", R.drawable.dabaoyuzhuantedabaoyu, 2)

    };
    private ImageButton test36_imgbtn_flush;
    private ImageView test36_iv_nowweather;
    private TextView test36_tv_nowdate;
    private TextView test36_tv_placeandtemperature;
    private GridView test36_gridview_weatherdata;
    private MyAdapter mMyAdapter;
    private Random mRandom;
    private String mTag = "org.chengpx.test180501.fragment.Test36Fragment";
    private Integer mTodayTemperature;

    @Override
    protected void initListener() {
        test36_imgbtn_flush.setOnClickListener(this);
    }

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRandom = new Random();
        View view = inflater.inflate(R.layout.fragment_test36, container, false);
        test36_imgbtn_flush = (ImageButton) view.findViewById(R.id.test36_imgbtn_flush);
        test36_iv_nowweather = (ImageView) view.findViewById(R.id.test36_iv_nowweather);
        test36_tv_nowdate = (TextView) view.findViewById(R.id.test36_tv_nowdate);
        test36_tv_placeandtemperature = (TextView) view.findViewById(R.id.test36_tv_placeandtemperature);
        test36_gridview_weatherdata = (GridView) view.findViewById(R.id.test36_gridview_weatherdata);
        return view;
    }

    @Override
    protected void onDie() {
        mRandom = null;
    }

    @Override
    protected void main() {
        mMyAdapter = new MyAdapter();
        test36_gridview_weatherdata.setAdapter(mMyAdapter);
        Date date = Calendar.getInstance(Locale.CHINESE).getTime();
        test36_tv_nowdate.setText(String.format("%tY年%tm月%td日 %tA", date, date, date, date));
    }

    @Override
    protected void initData() {
        EventBus.getDefault().register(this);
        Map<String, String> reqValues = new HashMap<>();
        reqValues.put("SenseName", "temperature");
        NetUtil.getNetUtil().addRequest("GetSenseByName.do", reqValues, AllSenseBean.class);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void GetSenseByName(AllSenseBean allSenseBean) {
        mTodayTemperature = allSenseBean.getTemperature();
        if (mMyAdapter != null) {
            mMyAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDims() {
        mMyAdapter = null;
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.test36_imgbtn_flush:
                Map<String, String> reqValues = new HashMap<>();
                reqValues.put("SenseName", "temperature");
                NetUtil.getNetUtil().addRequest("GetSenseByName.do", reqValues, AllSenseBean.class);
                break;
        }
    }

    private static class ViewHolder {

        private final TextView test36_gridview_weatherdata_tv_date;
        private final ImageView test36_gridview_weatherdata_iv_weather;
        private final TextView test36_gridview_weatherdata_tv_weather;
        private final TextView test36_gridview_weatherdata_tv_temperature;
        private final LinearLayout test36_gridview_weatherdata_ll_content;

        private ViewHolder(View convertView) {
            test36_gridview_weatherdata_tv_date = (TextView) convertView.findViewById(R.id.test36_gridview_weatherdata_tv_date);
            test36_gridview_weatherdata_iv_weather = (ImageView) convertView.findViewById(R.id.test36_gridview_weatherdata_iv_weather);
            test36_gridview_weatherdata_tv_weather = (TextView) convertView.findViewById(R.id.test36_gridview_weatherdata_tv_weather);
            test36_gridview_weatherdata_tv_temperature = (TextView) convertView.findViewById(R.id.test36_gridview_weatherdata_tv_temperature);
            test36_gridview_weatherdata_ll_content = convertView.findViewById(R.id.test36_gridview_weatherdata_ll_content);
        }

        public static ViewHolder get(View convertView) {
            Object tag = convertView.getTag();
            if (tag == null) {
                tag = new ViewHolder(convertView);
                convertView.setTag(tag);
            }
            return (ViewHolder) tag;
        }

        public TextView getTest36_gridview_weatherdata_tv_date() {
            return test36_gridview_weatherdata_tv_date;
        }

        public ImageView getTest36_gridview_weatherdata_iv_weather() {
            return test36_gridview_weatherdata_iv_weather;
        }

        public TextView getTest36_gridview_weatherdata_tv_weather() {
            return test36_gridview_weatherdata_tv_weather;
        }

        public TextView getTest36_gridview_weatherdata_tv_temperature() {
            return test36_gridview_weatherdata_tv_temperature;
        }

        public LinearLayout getTest36_gridview_weatherdata_ll_content() {
            return test36_gridview_weatherdata_ll_content;
        }

    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (mTodayTemperature == null) {
                return 0;
            }
            return mDayArr.length;
        }

        @Override
        public Integer getItem(int position) {
            if (position == 0) {
                return mTodayTemperature;
            }
            // 0 - 37
            int maxLess = mTodayTemperature;
            int maxAdd = 37 - mTodayTemperature;
            if (mRandom.nextBoolean()) {
                return mTodayTemperature + Math.min(5, maxAdd);
            } else {
                return mTodayTemperature - Math.min(5, maxLess);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null || convertView.getTag() == null) {
                convertView = LayoutInflater.from(mFragmentActivity).inflate(R.layout.item_test36_gridview_weatherdata,
                        test36_gridview_weatherdata, false);
                convertView.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT, parent.getHeight()));
                viewHolder = ViewHolder.get(convertView);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            MyGridView myGridView = (MyGridView) parent;
            if (myGridView.isMeasure()) {
                return convertView;
            }
            Log.d(mTag, "postion = " + position);
            Integer item = getItem(position);
            viewHolder.getTest36_gridview_weatherdata_tv_temperature().setText(Math.max((item - 5), 0) + "/" + Math.min((item + 5), 40));
            Calendar calendar = Calendar.getInstance(Locale.CHINESE);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + position);
            if (position < 3) {
                viewHolder.getTest36_gridview_weatherdata_tv_date()
                        .setText(calendar.get(Calendar.DAY_OF_MONTH) + "日(" + mDateDescArr[position] + ")");
            } else {
                viewHolder.getTest36_gridview_weatherdata_tv_date()
                        .setText(calendar.get(Calendar.DAY_OF_MONTH) + "日(" + String.format("%tA", calendar.getTime()) + ")");
            }
            WeatherBean weatherBean;
            if (item < 10) {
                if (mRandom.nextBoolean()) {
                    weatherBean = mCommonWeatherBeanArr[mRandom.nextInt(mCommonWeatherBeanArr.length)];
                } else {
                    weatherBean = mHimbernateWeatherBeanArr[mRandom.nextInt(mHimbernateWeatherBeanArr.length)];
                }
            } else {
                weatherBean = mCommonWeatherBeanArr[mRandom.nextInt(mCommonWeatherBeanArr.length)];
            }
            viewHolder.getTest36_gridview_weatherdata_iv_weather().setImageResource(weatherBean.getResId());
            viewHolder.getTest36_gridview_weatherdata_tv_weather().setText(weatherBean.getName());
            GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, mWeatherRgbArr[weatherBean.getLevel()]);// 创建drawable
            gradientDrawable.setGradientType(GradientDrawable.RECTANGLE);
            gradientDrawable.setCornerRadius(10);
            gradientDrawable.setStroke(5, 0XFFFFFFFF);
            viewHolder.getTest36_gridview_weatherdata_ll_content().setBackgroundDrawable(gradientDrawable);
            return convertView;
        }

    }

}

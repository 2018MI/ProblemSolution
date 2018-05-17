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
import android.widget.TextView;

import org.chengpx.base.BaseFragment;
import org.chengpx.R;
import org.chengpx.domain.MyDayBean;
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


public class WeatherFragment extends BaseFragment implements View.OnClickListener {

    private ImageButton mWeatherIbtnFlush;
    private ImageView mWeatherIvTodayweather;
    private TextView mWeatherTvTodaydate;
    private TextView mWeatherTvTemperatureinfo;
    private GridView mWeatherMygridviewData;
    private WeatherBean[] mCommonWeatherBeanArr = {
            new WeatherBean("晴", R.drawable.qing, 0), new WeatherBean("多云", R.drawable.duoyun, 0),
            new WeatherBean("阴", R.drawable.ying, 1), new WeatherBean("小雨", R.drawable.xiaoyu, 0),
            new WeatherBean("中雨", R.drawable.zhongyu, 1), new WeatherBean("大雨", R.drawable.dayu, 2)
    };
    private WeatherBean[] mHibernateWeatherBeanArr = {
            new WeatherBean("小雪", R.drawable.xiaoxue, 0), new WeatherBean("中雪", R.drawable.zhongxue, 1),
            new WeatherBean("大雪", R.drawable.daxue, 2)
    };
    private int[][] mWeatherRgbArr = {// 深蓝, 淡蓝, 浅灰
            new int[]{0XFF1181E0, 0XFF3698E7, 0XFF65B6EF}, new int[]{0XFF5AB6FA, 0XFF76C1F9, 0XFFA2D2F6},//分别为开始颜色，中间夜色，结束颜色
            new int[]{0XFF8AABCB, 0XFF9FBBD5, 0XFFBBD2E2}
    };
    private MyDayBean[] mMyDayBeanArr = {
            new MyDayBean("今天"), new MyDayBean("明天"), new MyDayBean("后天"),
            new MyDayBean(), new MyDayBean()
    };
    private int mTemperature;
    private Random mRandom;
    private String mTag = getClass().getName();
    private BaseAdapter mAdapter;

    @Override
    protected void initListener() {
        mWeatherIbtnFlush.setOnClickListener(this);
    }

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_waather, container, false);
        mWeatherIbtnFlush = (ImageButton) view.findViewById(R.id.weather_ibtn_flush);
        mWeatherIvTodayweather = (ImageView) view.findViewById(R.id.weather_iv_todayweather);
        mWeatherTvTodaydate = (TextView) view.findViewById(R.id.weather_tv_todaydate);
        mWeatherTvTemperatureinfo = (TextView) view.findViewById(R.id.weather_tv_temperatureinfo);
        mWeatherMygridviewData = (GridView) view.findViewById(R.id.weather_mygridview_data);
        return view;
    }

    @Override
    protected void onDie() {

    }

    @Override
    protected void main() {
        Date date = Calendar.getInstance(Locale.CHINA).getTime();
        mWeatherTvTodaydate.setText(String.format("%tY年%tm月%td日 %tA", date, date, date, date));
        mAdapter = new MyAdapter();
        mWeatherMygridviewData.setAdapter(mAdapter);
    }

    @Override
    protected void initData() {
        EventBus.getDefault().register(this);
        mRandom = new Random();
        Map<String, String> values = new HashMap<>();
        values.put("SenseName", "temperature");
        NetUtil.getNetUtil().addRequest("GetSenseByName.do", values, WeatherBean.class);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void GetSenseByName(WeatherBean weatherBean) {
        mTemperature = weatherBean.getTemperature();
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDims() {
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.weather_ibtn_flush:
                Map<String, String> values = new HashMap<>();
                values.put("SenseName", "temperature");
                NetUtil.getNetUtil().addRequest("GetSenseByName.do", values, WeatherBean.class);
                break;
        }
    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mMyDayBeanArr.length;
        }

        @Override
        public MyDayBean getItem(int i) {
            int temperature = mTemperature;
            if (i != 0) {
                if (mRandom.nextBoolean()) {
                    int min = Math.min(5, 37 - mTemperature);
                    if (min != 0) {
                        temperature += mRandom.nextInt(min);
                    }
                } else {
                    int min = Math.min(5, mTemperature);
                    if (min != 0) {
                        temperature -= mRandom.nextInt(min);
                    }
                }
            }
            MyDayBean myDayBean = mMyDayBeanArr[i];
            WeatherBean weatherBean;
            if (temperature < 10) {
                if (mRandom.nextBoolean()) {
                    weatherBean = mCommonWeatherBeanArr[mRandom.nextInt(mCommonWeatherBeanArr.length)];
                } else {
                    weatherBean = mHibernateWeatherBeanArr[mRandom.nextInt(mHibernateWeatherBeanArr.length)];
                }
            } else {
                weatherBean = mCommonWeatherBeanArr[mRandom.nextInt(mCommonWeatherBeanArr.length)];
            }
            weatherBean.setRange(new Integer[]{Math.max(0, temperature - 5), Math.min(37, temperature + 5)});
            weatherBean.setTemperature(temperature);
            myDayBean.setWeatherBean(weatherBean);
            String desc = myDayBean.getDesc();
            if (desc == null) {
                Calendar calendar = Calendar.getInstance(Locale.CHINA);
                calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + i);
                myDayBean.setDesc(calendar.get(Calendar.DAY_OF_MONTH) + "日" + "(" + String.format("%tA", calendar.getTime()) + ")");
            }
            return myDayBean;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (view == null) {
                view = LayoutInflater.from(mFragmentActivity).inflate(R.layout.item_weather_mygridview_data,
                        mWeatherMygridviewData, false);
                viewHolder = new ViewHolder(view);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            view.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT, viewGroup.getHeight()));
            if (((MyGridView) viewGroup).isIsisMeasure()) {
                return view;
            }
            Log.d(mTag, "i = " + i);
            MyDayBean myDayBean = getItem(i);
            viewHolder.weather_tv_lvdayinfo.setText(myDayBean.getDesc());
            WeatherBean weatherBean = myDayBean.getWeatherBean();
            viewHolder.weather_iv_lvicon.setImageResource(weatherBean.getResId());
            viewHolder.weather_tv_lv_desc.setText(weatherBean.getDesc());
            viewHolder.weather_tv_lvtemperature.setText(weatherBean.getRange()[0] + "/" + weatherBean.getRange()[1] + "℃");
            GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, mWeatherRgbArr[weatherBean.getLevel()]);
            gradientDrawable.setGradientType(GradientDrawable.RECTANGLE);
            gradientDrawable.setCornerRadius(10);
            view.setBackgroundDrawable(gradientDrawable);
            if (i == 0) {
                mWeatherIvTodayweather.setImageResource(weatherBean.getResId());
                mWeatherTvTemperatureinfo.setText("南京" + weatherBean.getTemperature() + "度");
            }
            return view;
        }


    }

    private static class ViewHolder {
        public View rootView;
        public TextView weather_tv_lvdayinfo;
        public ImageView weather_iv_lvicon;
        public TextView weather_tv_lv_desc;
        public TextView weather_tv_lvtemperature;

        public ViewHolder(View rootView) {
            this.rootView = rootView;
            this.weather_tv_lvdayinfo = (TextView) rootView.findViewById(R.id.weather_tv_lvdayinfo);
            this.weather_iv_lvicon = (ImageView) rootView.findViewById(R.id.weather_iv_lvicon);
            this.weather_tv_lv_desc = (TextView) rootView.findViewById(R.id.weather_tv_lv_desc);
            this.weather_tv_lvtemperature = (TextView) rootView.findViewById(R.id.weather_tv_lvtemperature);
        }

    }

}

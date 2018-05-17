package org.chengpx.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.chengpx.base.BaseFragment;
import org.chengpx.R;
import org.chengpx.domain.CarOverSpeedHistoryBean;
import org.chengpx.domain.RuleBean;
import org.chengpx.util.SpUtils;
import org.chengpx.util.db.CarOverSpeedHistoryDao;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CarSpeedListenerFragment extends BaseFragment implements View.OnClickListener, Comparator<CarOverSpeedHistoryBean> {

    private static BaseAdapter sAdapter;

    private TextView mCarspeedlistenerTvShowyuzhi;
    private EditText mCarspeedlistenerEtYuzhi;
    private Button mCarspeedlistenerBtnSetyuzhi;
    private Spinner mCarspeedlistenerSpinnerRules;
    private Button mCarspeedlistenerBtnQuery;
    private ListView mCarspeedlistenerLvData;
    private static List<CarOverSpeedHistoryBean> sCarOverSpeedHistoryBeanList;
    private DateFormat mDateFormat;
    private RuleBean[] mRuleBeanArr = {
            new RuleBean("时间降序", "overSpeedDateTime", RuleBean.DESC),
            new RuleBean("时间升序", "overSpeedDateTime", RuleBean.ASC)
    };
    private String[] mRuleArr = {
            "时间降序", "时间升序"
    };
    private SpUtils mSpUtils;
    private String mTag = getClass().getName();

    @Override
    protected void initListener() {
        mCarspeedlistenerBtnSetyuzhi.setOnClickListener(this);
        mCarspeedlistenerBtnQuery.setOnClickListener(this);
    }

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_carspeedlistener, container, false);
        mCarspeedlistenerTvShowyuzhi = (TextView) view.findViewById(R.id.carspeedlistener_tv_showyuzhi);
        mCarspeedlistenerEtYuzhi = (EditText) view.findViewById(R.id.carspeedlistener_et_yuzhi);
        mCarspeedlistenerBtnSetyuzhi = (Button) view.findViewById(R.id.carspeedlistener_btn_setyuzhi);
        mCarspeedlistenerSpinnerRules = (Spinner) view.findViewById(R.id.carspeedlistener_spinner_rules);
        mCarspeedlistenerBtnQuery = (Button) view.findViewById(R.id.carspeedlistener_btn_query);
        mCarspeedlistenerLvData = (ListView) view.findViewById(R.id.carspeedlistener_lv_data);
        return view;
    }

    @Override
    protected void onDie() {
    }

    @Override
    protected void main() {
        mCarspeedlistenerTvShowyuzhi.setText("我的1-4号车车辆速度告警阈值:" + mSpUtils.getInt("car_overspeed_yuzhi", 60) + "km/h");
        mCarspeedlistenerSpinnerRules.setAdapter(new ArrayAdapter<String>(
                mFragmentActivity, android.R.layout.simple_spinner_item, mRuleArr
        ));
        sAdapter = new MyAdapter();
        mCarspeedlistenerLvData.setAdapter(sAdapter);
    }

    @Override
    protected void initData() {
        mSpUtils = SpUtils.getInstance(mFragmentActivity);
        mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sCarOverSpeedHistoryBeanList = CarOverSpeedHistoryDao.getInstance(mFragmentActivity).select();
    }

    @Override
    protected void onDims() {
        mDateFormat = null;
        sCarOverSpeedHistoryBeanList = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.carspeedlistener_btn_setyuzhi:
                setYuzhi();
                break;
            case R.id.carspeedlistener_btn_query:
                if (sCarOverSpeedHistoryBeanList != null) {
                    Collections.sort(sCarOverSpeedHistoryBeanList, this);
                }
                if (sAdapter != null) {
                    sAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

    private void setYuzhi() {
        String str = mCarspeedlistenerEtYuzhi.getText().toString();
        if (!str.matches("^[1-9]\\d{0,2}$")) {
            showToast("阈值非法");
            return;
        }
        int yuzhi = Integer.parseInt(str);
        if (yuzhi < 20 || yuzhi > 240) {
            showToast("阈值非法,阈值范围[20-240]");
            return;
        }
        mSpUtils.putInt("car_overspeed_yuzhi", yuzhi);
        mCarspeedlistenerTvShowyuzhi.setText("我的1-4号车车辆速度告警阈值:" + yuzhi + "km/h");
        showToast("ok");
    }

    @Override
    public int compare(CarOverSpeedHistoryBean carOverSpeedHistoryBean1, CarOverSpeedHistoryBean carOverSpeedHistoryBean2) {
        RuleBean ruleBean = mRuleBeanArr[mCarspeedlistenerSpinnerRules.getSelectedItemPosition()];
        try {
            Field declaredField = CarOverSpeedHistoryBean.class.getDeclaredField(ruleBean.getColumnField());
            declaredField.setAccessible(true);
            Comparable comparable1 = (Comparable) declaredField.get(carOverSpeedHistoryBean1);
            Comparable comparable2 = (Comparable) declaredField.get(carOverSpeedHistoryBean2);
            if (RuleBean.ASC.equals(ruleBean.getPriority())) {
                return comparable1.compareTo(comparable2);
            } else {
                return comparable2.compareTo(comparable1);
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static class CarSpeedListenerFragmentBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            sCarOverSpeedHistoryBeanList = CarOverSpeedHistoryDao.getInstance(context).select();
            if (sAdapter != null) {
                sAdapter.notifyDataSetChanged();
            }
        }
    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return sCarOverSpeedHistoryBeanList == null ? 0 : sCarOverSpeedHistoryBeanList.size();
        }

        @Override
        public CarOverSpeedHistoryBean getItem(int i) {
            return sCarOverSpeedHistoryBeanList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (view == null) {
                view = LayoutInflater.from(mFragmentActivity).inflate(R.layout.item_carspeedlistener_lv_data,
                        mCarspeedlistenerLvData, false);
                viewHolder = new ViewHolder(view);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            CarOverSpeedHistoryBean carOverSpeedHistoryBean = getItem(i);
            viewHolder.carspeedlistener_tv_num.setText(carOverSpeedHistoryBean.getId() + "");
            viewHolder.carspeedlistener_tv_carid.setText(carOverSpeedHistoryBean.getCarId() + "");
            viewHolder.carspeedlistener_tv_carspeed.setText(carOverSpeedHistoryBean.getCarSpeed() + "");
            viewHolder.carspeedlistener_tv_overSpeedDateTime.setText(mDateFormat.format(carOverSpeedHistoryBean.getOverSpeedDateTime()));
            return view;
        }


    }

    private static class ViewHolder {
        public View rootView;
        public TextView carspeedlistener_tv_num;
        public TextView carspeedlistener_tv_carid;
        public TextView carspeedlistener_tv_carspeed;
        public TextView carspeedlistener_tv_overSpeedDateTime;

        public ViewHolder(View rootView) {
            this.rootView = rootView;
            this.carspeedlistener_tv_num = (TextView) rootView.findViewById(R.id.carspeedlistener_tv_num);
            this.carspeedlistener_tv_carid = (TextView) rootView.findViewById(R.id.carspeedlistener_tv_carid);
            this.carspeedlistener_tv_carspeed = (TextView) rootView.findViewById(R.id.carspeedlistener_tv_carspeed);
            this.carspeedlistener_tv_overSpeedDateTime = (TextView) rootView.findViewById(R.id.carspeedlistener_tv_overSpeedDateTime);
        }

    }
}

package org.chengpx.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import org.chengpx.BaseFragment;
import org.chengpx.R;
import org.chengpx.domain.CaroverspeedhistoryBean;
import org.chengpx.domain.RuleBean;
import org.chengpx.service.CarSpeedListenerService;
import org.chengpx.util.SpUtils;
import org.chengpx.util.db.CaroverspeedhistoryBeanDao;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CarSpeedListenerFragment extends BaseFragment implements View.OnClickListener, Comparator<CaroverspeedhistoryBean> {

    private List<CaroverspeedhistoryBean> mCaroverspeedhistoryBeanList;
    private BaseAdapter mAdapter;
    private TextView mCarspeedlistenerTvShowyuzhi;
    private EditText mCarspeedlistenerEtYuzhi;
    private Button mCarspeedlistenerBtnSetyuzhi;
    private Spinner mCarspeedlistenerSpinnerRules;
    private Button mCarspeedlistenerBtnQuery;
    private ListView mCarspeedlistenerLvData;
    private SpUtils mSpUtils;
    private DateFormat mDateFormat;
    private String[] mRuleArr = {
            "时间降序", "时间升序"
    };
    private RuleBean[] mRuleBeanArr = {
            new RuleBean("时间降序", "overSpeedDateTime", RuleBean.DESC),
            new RuleBean("时间升序", "overSpeedDateTime", RuleBean.ASC)
    };
    private CarSpeedListenerService.CarSpeedListenerServiceBroadcastReceiver mCarSpeedListenerServiceBroadcastReceiver;

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
        mCarspeedlistenerTvShowyuzhi.setText("我的1-4号小车车辆速度警告阈值:" + mSpUtils.getInt("car_speed_yuzhi", 60) + "km/h");
        mCarspeedlistenerSpinnerRules.setAdapter(new ArrayAdapter<String>(
                mFragmentActivity, android.R.layout.simple_spinner_item, mRuleArr
        ));
        mAdapter = new MyAaapter();
        mCarspeedlistenerLvData.setAdapter(mAdapter);
    }

    @Override
    protected void initData() {
        mCarSpeedListenerServiceBroadcastReceiver = new CarSpeedListenerService().new CarSpeedListenerServiceBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CarSpeedListenerService.CarSpeedListenerServiceBroadcastReceiver.class.getName());
        mFragmentActivity.registerReceiver(mCarSpeedListenerServiceBroadcastReceiver, intentFilter);
        mSpUtils = SpUtils.getInstance(mFragmentActivity);
        mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    @Override
    protected void onDims() {
        mAdapter = null;
        mCaroverspeedhistoryBeanList = null;
        mFragmentActivity.unregisterReceiver(mCarSpeedListenerServiceBroadcastReceiver);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.carspeedlistener_btn_query:
                Collections.sort(mCaroverspeedhistoryBeanList, this);
                if (mAdapter != null) {
                    mAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.carspeedlistener_btn_setyuzhi:
                setYuzhi();
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
        if (20 > yuzhi || 240 < yuzhi) {
            showToast("阈值范围[20-240]");
            return;
        }
        mSpUtils.putInt("car_speed_yuzhi", yuzhi);
        mCarspeedlistenerTvShowyuzhi.setText("我的1-4号小车车辆速度警告阈值:" + yuzhi + "km/h");
        Intent intent = new Intent();
        intent.setAction(CarSpeedListenerService.CarSpeedListenerServiceBroadcastReceiver.class.getName());
        mFragmentActivity.sendBroadcast(intent);
    }

    @Override
    public int compare(CaroverspeedhistoryBean caroverspeedhistoryBean1, CaroverspeedhistoryBean caroverspeedhistoryBean2) {
        RuleBean ruleBean = mRuleBeanArr[mCarspeedlistenerSpinnerRules.getSelectedItemPosition()];
        try {
            Field declaredField = CaroverspeedhistoryBean.class.getDeclaredField(ruleBean.getColumnField());
            declaredField.setAccessible(true);
            Comparable comparable1 = (Comparable) declaredField.get(caroverspeedhistoryBean1);
            Comparable comparable2 = (Comparable) declaredField.get(caroverspeedhistoryBean2);
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

    public class CarSpeedListenerFragmentBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            mCaroverspeedhistoryBeanList = CaroverspeedhistoryBeanDao.getInstance(context).select();
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    private class MyAaapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mCaroverspeedhistoryBeanList == null ? 0 : mCaroverspeedhistoryBeanList.size();
        }

        @Override
        public CaroverspeedhistoryBean getItem(int i) {
            return mCaroverspeedhistoryBeanList.get(i);
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
            CaroverspeedhistoryBean caroverspeedhistoryBean = getItem(i);
            viewHolder.carspeedlistener_tv_lvnum.setText(caroverspeedhistoryBean.getId() + "");
            viewHolder.carspeedlistener_tv_lvcarid.setText(caroverspeedhistoryBean.getCarId() + "");
            viewHolder.carspeedlistener_tv_lvcarspeed.setText(caroverspeedhistoryBean.getCarSpeed() + "");
            viewHolder.carspeedlistener_tv_lvoverspeeddatetime.setText(mDateFormat.format(caroverspeedhistoryBean.getOverSpeedDateTime()));
            return view;
        }


    }

    private static class ViewHolder {
        public View rootView;
        public TextView carspeedlistener_tv_lvnum;
        public TextView carspeedlistener_tv_lvcarid;
        public TextView carspeedlistener_tv_lvcarspeed;
        public TextView carspeedlistener_tv_lvoverspeeddatetime;

        public ViewHolder(View rootView) {
            this.rootView = rootView;
            this.carspeedlistener_tv_lvnum = (TextView) rootView.findViewById(R.id.carspeedlistener_tv_lvnum);
            this.carspeedlistener_tv_lvcarid = (TextView) rootView.findViewById(R.id.carspeedlistener_tv_lvcarid);
            this.carspeedlistener_tv_lvcarspeed = (TextView) rootView.findViewById(R.id.carspeedlistener_tv_lvcarspeed);
            this.carspeedlistener_tv_lvoverspeeddatetime = (TextView) rootView.findViewById(R.id.carspeedlistener_tv_lvoverspeeddatetime);
        }

    }
}

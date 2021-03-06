package org.chengpx.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.chengpx.base.BaseFragment;
import org.chengpx.R;
import org.chengpx.domain.RuleBean;
import org.chengpx.domain.TrafficLightBean;
import org.chengpx.util.net.NetUtil;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * create at 2018/5/9 20:43 by chengpx
 */
public class TrafficLightManagerFragment extends BaseFragment implements AdapterView.OnItemSelectedListener, Comparator<TrafficLightBean> {

    private String mTag = getClass().getName();

    private ImageView trafficlightmanagerIvRedlight;
    private ImageView trafficlightmanagerIvYellowlight;
    private ImageView trafficlightmanagerIvGreenlight;
    private Spinner trafficlightmanagerSpinnerRules;
    private ListView trafficlightmanagerLvData;
    private int mReqGetTrafficLightConfigActionIndex;
    private Map<Integer, TrafficLightBean> mTrafficLightBeanMap;
    private MyAdapter mMyAdapter;
    private String[] mRuleArr = {
            "路口升序", "路口降序", "红灯升序", "红灯降序", "绿灯升序", "绿灯降序",
            "黄灯升序", "黄灯降序"
    };
    private RuleBean[] mRuleBeanArr = {
            new RuleBean("路口升序", "RoadId", RuleBean.ASC),
            new RuleBean("路口降序", "RoadId", RuleBean.DESC),
            new RuleBean("红灯升序", "RedTime", RuleBean.ASC),
            new RuleBean("红灯降序", "RedTime", RuleBean.DESC),
            new RuleBean("绿灯升序", "GreenTime", RuleBean.ASC),
            new RuleBean("绿灯降序", "GreenTime", RuleBean.DESC),
            new RuleBean("黄灯升序", "YellowTime", RuleBean.ASC),
            new RuleBean("黄灯降序", "YellowTime", RuleBean.DESC)
    };
    private TrafficLightBean[] mTrafficLightBeanArr = {
            new TrafficLightBean(1), new TrafficLightBean(2),
            new TrafficLightBean(3)
    };
    private Timer mTimer;
    private ProgressDialog mTrafficLightConfigDialog;
    private ProgressDialog mTraffilLightStatusDialog;

    protected void initListener() {
        trafficlightmanagerSpinnerRules.setOnItemSelectedListener(this);
    }

    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trafficlightmanager, container, false);
        trafficlightmanagerIvRedlight = (ImageView) view.findViewById(R.id.trafficlightmanager_iv_redlight);
        trafficlightmanagerIvYellowlight = (ImageView) view.findViewById(R.id.trafficlightmanager_iv_yellowlight);
        trafficlightmanagerIvGreenlight = (ImageView) view.findViewById(R.id.trafficlightmanager_iv_greenlight);
        trafficlightmanagerSpinnerRules = (Spinner) view.findViewById(R.id.trafficlightmanager_spinner_rules);
        trafficlightmanagerLvData = (ListView) view.findViewById(R.id.trafficlightmanager_lv_data);
        return view;
    }

    protected void onDie() {

    }

    protected void onDims() {
        mTimer.cancel();
        mTimer = null;
        mReqGetTrafficLightConfigActionIndex = 0;
        EventBus.getDefault().unregister(this);
    }

    protected void main() {
        mMyAdapter = new MyAdapter();
        trafficlightmanagerLvData.setAdapter(mMyAdapter);
        trafficlightmanagerSpinnerRules.setAdapter(new ArrayAdapter<String>(mFragmentActivity, android.R.layout.simple_spinner_item, mRuleArr));
    }

    protected void initData() {
        EventBus.getDefault().register(this);
        mTrafficLightBeanMap = new HashMap<>();
        Map<String, Integer> values = new HashMap<>();
        values.put("TrafficLightId", mTrafficLightBeanArr[mReqGetTrafficLightConfigActionIndex].getTrafficLightId());
        mTrafficLightConfigDialog = showLoadingDialog("红绿灯配置加载", "");
        NetUtil.getNetUtil().addRequest("GetTrafficLightConfigAction.do", values, TrafficLightBean.class);
        mTimer = new Timer();
        mTraffilLightStatusDialog = showLoadingDialog("红绿灯状态查询", "");
        mTimer.schedule(new MyTimerTask(), 0, 3000);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getTrafficLightConfigAction(TrafficLightBean trafficLightBean) {
        TrafficLightBean localTrafficLightBean = mTrafficLightBeanArr[mReqGetTrafficLightConfigActionIndex];
        localTrafficLightBean.setRedTime(trafficLightBean.getRedTime());
        localTrafficLightBean.setYellowTime(trafficLightBean.getYellowTime());
        localTrafficLightBean.setGreenTime(trafficLightBean.getGreenTime());
        mTrafficLightBeanMap.put(localTrafficLightBean.getTrafficLightId(), localTrafficLightBean);
        mReqGetTrafficLightConfigActionIndex++;
        if (mReqGetTrafficLightConfigActionIndex < mTrafficLightBeanArr.length) {
            Map<String, Integer> values = new HashMap<>();
            values.put("TrafficLightId", mTrafficLightBeanArr[mReqGetTrafficLightConfigActionIndex].getTrafficLightId());
            NetUtil.getNetUtil().addRequest("GetTrafficLightConfigAction.do", values, TrafficLightBean.class);
        } else {
            mTrafficLightConfigDialog.dismiss();
            mTrafficLightConfigDialog = null;
            if (mMyAdapter != null) {
                mMyAdapter.notifyDataSetChanged();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getTrafficLightNowStatus(Map<String, Object> map) {
        if (mTraffilLightStatusDialog != null) {
            mTraffilLightStatusDialog.dismiss();
            mTraffilLightStatusDialog = null;
        }
        switch ((String) map.get("Status")) {
            case "Red":
                trafficlightmanagerIvRedlight.setImageResource(R.drawable.shape_oval_red);
                trafficlightmanagerIvYellowlight.setImageResource(R.drawable.shape_oval_gray);
                trafficlightmanagerIvGreenlight.setImageResource(R.drawable.shape_oval_gray);
                break;
            case "Yellow":
                trafficlightmanagerIvRedlight.setImageResource(R.drawable.shape_oval_gray);
                trafficlightmanagerIvYellowlight.setImageResource(R.drawable.shape_oval_yellow);
                trafficlightmanagerIvGreenlight.setImageResource(R.drawable.shape_oval_gray);
                break;
            case "Green":
                trafficlightmanagerIvRedlight.setImageResource(R.drawable.shape_oval_gray);
                trafficlightmanagerIvYellowlight.setImageResource(R.drawable.shape_oval_gray);
                trafficlightmanagerIvGreenlight.setImageResource(R.drawable.shape_oval_green);
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Arrays.sort(mTrafficLightBeanArr, this);
        mMyAdapter.notifyDataSetChanged();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public int compare(TrafficLightBean o1, TrafficLightBean o2) {
        RuleBean ruleBean = mRuleBeanArr[trafficlightmanagerSpinnerRules.getSelectedItemPosition()];
        try {
            Field declaredField = TrafficLightBean.class.getDeclaredField(ruleBean.getColumnField());
            declaredField.setAccessible(true);
            Comparable val1 = (Comparable) declaredField.get(o1);
            Comparable val12 = (Comparable) declaredField.get(o2);
            if (RuleBean.ASC.equals(ruleBean.getPriority())) {
                return val1.compareTo(val12);
            } else if (RuleBean.DESC.equals(ruleBean.getPriority())) {
                return val12.compareTo(val1);
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return 0;
    }


    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mTrafficLightBeanMap == null ? 0 : mTrafficLightBeanMap.size();
        }

        @Override
        public TrafficLightBean getItem(int position) {
            return mTrafficLightBeanMap.get(mTrafficLightBeanArr[position].getTrafficLightId());
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mFragmentActivity).inflate(R.layout.item_trafficlightmanagerlvdata,
                        trafficlightmanagerLvData, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            TrafficLightBean trafficLightBean = getItem(position);
            viewHolder.trafficlightmanager_tv_lvRoadId.setText(trafficLightBean.getTrafficLightId() + "");
            viewHolder.trafficlightmanager_tv_lvredtime.setText(trafficLightBean.getRedTime() + "");
            viewHolder.trafficlightmanager_tv_lvyellowtime.setText(trafficLightBean.getYellowTime() + "");
            viewHolder.trafficlightmanager_tv_lvgreentime.setText(trafficLightBean.getGreenTime() + "");
            return convertView;
        }


    }

    private static class ViewHolder {
        public View rootView;
        public TextView trafficlightmanager_tv_lvRoadId;
        public TextView trafficlightmanager_tv_lvredtime;
        public TextView trafficlightmanager_tv_lvyellowtime;
        public TextView trafficlightmanager_tv_lvgreentime;

        public ViewHolder(View rootView) {
            this.rootView = rootView;
            this.trafficlightmanager_tv_lvRoadId = (TextView) rootView.findViewById(R.id.trafficlightmanager_tv_lvRoadId);
            this.trafficlightmanager_tv_lvredtime = (TextView) rootView.findViewById(R.id.trafficlightmanager_tv_lvredtime);
            this.trafficlightmanager_tv_lvyellowtime = (TextView) rootView.findViewById(R.id.trafficlightmanager_tv_lvyellowtime);
            this.trafficlightmanager_tv_lvgreentime = (TextView) rootView.findViewById(R.id.trafficlightmanager_tv_lvgreentime);
        }

    }

    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            Map<String, Integer> values = new HashMap<>();
            values.put("TrafficLightId", 1);
            NetUtil.getNetUtil().addRequest("GetTrafficLightNowStatus.do", values, Map.class);
        }
    }

}

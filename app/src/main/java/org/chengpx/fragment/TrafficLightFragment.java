package org.chengpx.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.chengpx.R;
import org.chengpx.base.BaseFragment;
import org.chengpx.domain.RuleBean;
import org.chengpx.domain.TrafficLightBean;
import org.chengpx.util.net.NetUtil;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TrafficLightFragment extends BaseFragment implements View.OnClickListener, Comparator<TrafficLightBean> {

    private Spinner mTrafficlightSpinnerRules;
    private Button mTrafficlightBtnQuery;
    private Button mTrafficlightBtnMulitset;
    private ListView mTrafficlightLvData;
    private RuleBean[] mRuleBeanArr = {
            new RuleBean("路口升序", "TrafficLightId", RuleBean.ASC),
            new RuleBean("路口降序", "TrafficLightId", RuleBean.DESC),
            new RuleBean("红灯升序", "RedTime", RuleBean.ASC),
            new RuleBean("红灯降序", "RedTime", RuleBean.DESC),
            new RuleBean("黄灯升序", "YellowTime", RuleBean.ASC),
            new RuleBean("黄灯降序", "YellowTime", RuleBean.DESC),
            new RuleBean("绿灯升序", "GreenTime", RuleBean.ASC),
            new RuleBean("绿灯降序", "GreenTime", RuleBean.DESC)
    };
    private String[] mRuleArr = {
            "路口升序", "路口降序", "红灯升序", "红灯降序",
            "黄灯升序", "黄灯降序", "绿灯升序", "绿灯降序"
    };
    private TrafficLightBean[] mTrafficLightBeanArr = {
            new TrafficLightBean(1), new TrafficLightBean(2),
            new TrafficLightBean(3), new TrafficLightBean(4),
            new TrafficLightBean(5)
    };
    private Map<Integer, TrafficLightBean> mTrafficLightBeanMap;
    private int mReqGetTrafficLightConfigActionIndex;
    private MyAdapter mAdapter;
    private AlertDialog mAlertDialog;
    private EditText mTrafficlightEtDialogRedtime;
    private EditText mTrafficlightEtDialogYellowtime;
    private EditText mTrafficlightEtDialogGreentime;
    private Button mTrafficlightBtnDialogconfirm;
    private Button mTrafficlightBtnDialogcancel;
    private List<Integer> mConfigTrafficLightIdList;
    private int mReqSetTrafficLightConfigIndex;
    private Map<String, Integer> mSetTrafficLightConfigValues;
    private ProgressDialog mProgressDialog;
    private ProgressDialog mTrafficLightConfigDialog;

    @Override
    protected void initListener() {
        mTrafficlightBtnQuery.setOnClickListener(this);
        mTrafficlightBtnMulitset.setOnClickListener(this);
    }

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trafficlight, container, false);
        mTrafficlightSpinnerRules = (Spinner) view.findViewById(R.id.trafficlight_spinner_rules);
        mTrafficlightBtnQuery = (Button) view.findViewById(R.id.trafficlight_btn_query);
        mTrafficlightBtnMulitset = (Button) view.findViewById(R.id.trafficlight_btn_mulitset);
        mTrafficlightLvData = (ListView) view.findViewById(R.id.trafficlight_lv_data);
        return view;
    }

    @Override
    protected void onDie() {

    }

    @Override
    protected void main() {
        mTrafficlightSpinnerRules.setAdapter(new ArrayAdapter<String>(mFragmentActivity, android.R.layout.simple_spinner_item, mRuleArr));
        mAdapter = new MyAdapter();
        mTrafficlightLvData.setAdapter(mAdapter);
    }

    @Override
    protected void initData() {
        EventBus.getDefault().register(this);
        mTrafficLightBeanMap = new HashMap<>();
        Map<String, Integer> values = new HashMap<>();
        values.put("TrafficLightId", mTrafficLightBeanArr[mReqGetTrafficLightConfigActionIndex].getTrafficLightId());
        NetUtil.getNetUtil().addRequest("GetTrafficLightConfigAction.do", values, TrafficLightBean.class);
        mTrafficLightConfigDialog = showLoadingDialog("红绿灯配置加载", "");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void GetTrafficLightConfigAction(TrafficLightBean trafficLightBean) {
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
            if (mTrafficLightConfigDialog != null) {
                mTrafficLightConfigDialog.dismiss();
                mTrafficLightConfigDialog = null;
            }
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onDims() {
        mReqGetTrafficLightConfigActionIndex = 0;
        mReqSetTrafficLightConfigIndex = 0;
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.trafficlight_btn_mulitset:
                List<Integer> intList = new ArrayList<>();
                for (int index = 0; index < mTrafficlightLvData.getChildCount(); index++) {
                    View childAtView = mTrafficlightLvData.getChildAt(index);
                    ViewHolder viewHolder = (ViewHolder) childAtView.getTag();
                    if (!viewHolder.trafficlight_checkbox_control.isChecked()) {
                        continue;
                    }
                    intList.add(Integer.parseInt(viewHolder.trafficlight_tv_roadid.getText().toString()));
                }
                if (intList.size() == 0) {
                    showToast("请至少选择一个");
                    return;
                }
                showConfigDialog(intList);
                break;
            case R.id.trafficlight_btn_query:
                Arrays.sort(mTrafficLightBeanArr, this);
                if (mAdapter != null) {
                    mAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.trafficlight_btn_set:
                showConfigDialog((List<Integer>) v.getTag());
                break;
            case R.id.trafficlight_btn_dialogcancel:
                mAlertDialog.dismiss();
                mAlertDialog = null;
                break;
            case R.id.trafficlight_btn_dialogconfirm:
                configTrafficlight((List<Integer>) v.getTag());
                break;
        }
    }

    private void configTrafficlight(List<Integer> intList) {
        String strRedTime = mTrafficlightEtDialogRedtime.getText().toString();
        String strYellowTime = mTrafficlightEtDialogYellowtime.getText().toString();
        String strGreenTime = mTrafficlightEtDialogGreentime.getText().toString();
        if (TextUtils.isEmpty(strRedTime) || TextUtils.isEmpty(strYellowTime) || TextUtils.isEmpty(strGreenTime)) {
            showToast("不可以为空");
            return;
        }
        if (!strRedTime.matches("^[1-9]\\d?$")) {
            showToast("红灯配置非法");
            return;
        }
        if (!strYellowTime.matches("^[1-9]\\d?$")) {
            showToast("黄灯配置非法");
            return;
        }
        if (!strGreenTime.matches("^[1-9]\\d?$")) {
            showToast("绿灯配置非法");
            return;
        }
        mConfigTrafficLightIdList = intList;
        mReqSetTrafficLightConfigIndex = 0;
        mSetTrafficLightConfigValues = new HashMap<>();
        mSetTrafficLightConfigValues.put("TrafficLightId", mConfigTrafficLightIdList.get(mReqSetTrafficLightConfigIndex));
        mSetTrafficLightConfigValues.put("RedTime", Integer.parseInt(strRedTime));
        mSetTrafficLightConfigValues.put("YellowTime", Integer.parseInt(strYellowTime));
        mSetTrafficLightConfigValues.put("GreenTime", Integer.parseInt(strGreenTime));
        NetUtil.getNetUtil().addRequest("SetTrafficLightConfig.do", mSetTrafficLightConfigValues, Map.class);
        mAlertDialog.dismiss();
        mAlertDialog = null;
        mProgressDialog = showLoadingDialog("红绿灯配置设置", "");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void SetTrafficLightConfig(Map<String, String> map) {
        mReqSetTrafficLightConfigIndex++;
        if (mReqSetTrafficLightConfigIndex < mConfigTrafficLightIdList.size()) {
            mSetTrafficLightConfigValues.put("TrafficLightId", mConfigTrafficLightIdList.get(mReqSetTrafficLightConfigIndex));
            NetUtil.getNetUtil().addRequest("SetTrafficLightConfig.do", mSetTrafficLightConfigValues, Map.class);
        } else {
            showToast("配置成功");
            mProgressDialog.dismiss();
            mProgressDialog = null;
            mReqGetTrafficLightConfigActionIndex = 0;
            Map<String, Integer> values = new HashMap<>();
            values.put("TrafficLightId", mTrafficLightBeanArr[mReqGetTrafficLightConfigActionIndex].getTrafficLightId());
            NetUtil.getNetUtil().addRequest("GetTrafficLightConfigAction.do", values, TrafficLightBean.class);
        }
    }

    private void showConfigDialog(List<Integer> intList) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mFragmentActivity);
        mAlertDialog = builder.create();
        View view = LayoutInflater.from(mFragmentActivity).inflate(R.layout.dialog_trafficlightconfig, null);
        mAlertDialog.setView(view);
        mAlertDialog.show();
        mTrafficlightEtDialogRedtime = (EditText) view.findViewById(R.id.trafficlight_et_dialog_redtime);
        mTrafficlightEtDialogYellowtime = (EditText) view.findViewById(R.id.trafficlight_et_dialog_yellowtime);
        mTrafficlightEtDialogGreentime = (EditText) view.findViewById(R.id.trafficlight_et_dialog_greentime);
        mTrafficlightBtnDialogconfirm = (Button) view.findViewById(R.id.trafficlight_btn_dialogconfirm);
        mTrafficlightBtnDialogcancel = (Button) view.findViewById(R.id.trafficlight_btn_dialogcancel);
        mTrafficlightBtnDialogconfirm.setOnClickListener(this);
        mTrafficlightBtnDialogcancel.setOnClickListener(this);
        mTrafficlightBtnDialogconfirm.setTag(intList);
    }

    @Override
    public int compare(TrafficLightBean trafficLightBean1, TrafficLightBean trafficLightBean2) {
        RuleBean ruleBean = mRuleBeanArr[mTrafficlightSpinnerRules.getSelectedItemPosition()];
        try {
            Field declaredField = TrafficLightBean.class.getDeclaredField(ruleBean.getColumnField());
            declaredField.setAccessible(true);
            Comparable comparable1  = (Comparable) declaredField.get(trafficLightBean1);
            Comparable comparable2 = (Comparable) declaredField.get(trafficLightBean2);
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

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mTrafficLightBeanMap.size();
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
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mFragmentActivity).inflate(R.layout.item_trafficlight_lv_data,
                        mTrafficlightLvData, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            TrafficLightBean trafficLightBean = getItem(position);
            viewHolder.trafficlight_tv_roadid.setText(trafficLightBean.getTrafficLightId() + "");
            viewHolder.trafficlight_tv_redtime.setText(trafficLightBean.getRedTime() + "");
            viewHolder.trafficlight_tv_yellowtime.setText(trafficLightBean.getYellowTime() + "");
            viewHolder.trafficlight_tv_greentime.setText(trafficLightBean.getGreenTime() + "");
            List<Integer> intList = new ArrayList<>();
            intList.add(trafficLightBean.getTrafficLightId());
            viewHolder.trafficlight_btn_set.setTag(intList);
            viewHolder.trafficlight_btn_set.setOnClickListener(TrafficLightFragment.this);
            return convertView;
        }


    }

    private static class ViewHolder {
        public View rootView;
        public TextView trafficlight_tv_roadid;
        public TextView trafficlight_tv_redtime;
        public TextView trafficlight_tv_yellowtime;
        public TextView trafficlight_tv_greentime;
        public CheckBox trafficlight_checkbox_control;
        public Button trafficlight_btn_set;

        public ViewHolder(View rootView) {
            this.rootView = rootView;
            this.trafficlight_tv_roadid = (TextView) rootView.findViewById(R.id.trafficlight_tv_roadid);
            this.trafficlight_tv_redtime = (TextView) rootView.findViewById(R.id.trafficlight_tv_redtime);
            this.trafficlight_tv_yellowtime = (TextView) rootView.findViewById(R.id.trafficlight_tv_yellowtime);
            this.trafficlight_tv_greentime = (TextView) rootView.findViewById(R.id.trafficlight_tv_greentime);
            this.trafficlight_checkbox_control = (CheckBox) rootView.findViewById(R.id.trafficlight_checkbox_control);
            this.trafficlight_btn_set = (Button) rootView.findViewById(R.id.trafficlight_btn_set);
        }

    }
}

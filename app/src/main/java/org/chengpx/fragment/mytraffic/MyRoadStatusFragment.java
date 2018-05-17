package org.chengpx.fragment.mytraffic;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.chengpx.R;
import org.chengpx.base.BaseFragment;
import org.chengpx.domain.MyRoadStatusBean;
import org.chengpx.domain.SetTrafficLightConfigBean;
import org.chengpx.domain.TrafficLightBean;
import org.chengpx.util.net.NetUtil;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


/**
 * 我的路況
 * create by chengpx
 */
public class MyRoadStatusFragment extends BaseFragment implements View.OnClickListener {

    private ListView mMyroadstatusLvContent;
    private MyAdapter mAdapter;
    private MyRoadStatusBean[] mMyRoadStatusBeanArr = {
            new MyRoadStatusBean(1, new TrafficLightBean(1)),
            new MyRoadStatusBean(2, new TrafficLightBean(2)),
            new MyRoadStatusBean(3, new TrafficLightBean(3)),
            new MyRoadStatusBean(4, new TrafficLightBean(4)),
            new MyRoadStatusBean(5, new TrafficLightBean(5))
    };
    private Map<Integer, MyRoadStatusBean> mMyRoadStatusBeanMap;
    private int mReqGetTrafficLightConfigActionIndex;
    private Timer mTimer;
    private int mReqGetTrafficLightNowStatusIndex;
    private int mReqGetRoadStatusIndex;
    private ImageButton myroadstatusIbtnCancel;
    private EditText myroadstatusEtRedtime;
    private EditText myroadstatusEtYellowtime;
    private EditText myroadstatusEtGreentime;
    private Button myroadstatusBtnConfirm;
    private Button myroadstatusBtnCancel;
    private AlertDialog mAlertDialog;
    private List<Integer> mConfigTrafficLightIdList;
    private int mReqSetTrafficLightConfigIndex;
    private Map<String, Integer> mConfigTrafficLightValues;

    @Override
    protected void initListener() {

    }

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myroadstatus, container, false);
        mMyroadstatusLvContent = (ListView) view.findViewById(R.id.myroadstatus_lv_content);
        return view;
    }

    @Override
    protected void onDie() {

    }

    @Override
    protected void main() {
        mAdapter = new MyAdapter();
        mMyroadstatusLvContent.setAdapter(mAdapter);
        mTimer = new Timer();
        mTimer.schedule(new MyTimerTask(), 0, 1000 * 3);
    }

    @Override
    protected void initData() {
        EventBus.getDefault().register(this);
        mMyRoadStatusBeanMap = new HashMap<>();
        Map<String, Integer> values = new HashMap<>();
        values.put("TrafficLightId", mMyRoadStatusBeanArr[mReqGetTrafficLightConfigActionIndex].getTrafficLightBean().getTrafficLightId());
        NetUtil.getNetUtil().addRequest("GetTrafficLightConfigAction.do", values, TrafficLightBean.class);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void GetTrafficLightConfigAction(TrafficLightBean trafficLightBean) {
        MyRoadStatusBean localMyRoadStatusBean = mMyRoadStatusBeanArr[mReqGetTrafficLightConfigActionIndex];
        TrafficLightBean localTrafficLightBean = localMyRoadStatusBean.getTrafficLightBean();
        localTrafficLightBean.setGreenTime(trafficLightBean.getGreenTime());
        localTrafficLightBean.setYellowTime(trafficLightBean.getYellowTime());
        localTrafficLightBean.setRedTime(trafficLightBean.getRedTime());
        mMyRoadStatusBeanMap.put(localMyRoadStatusBean.getRoadId(), localMyRoadStatusBean);
        mReqGetTrafficLightConfigActionIndex++;
        if (mReqGetTrafficLightConfigActionIndex < mMyRoadStatusBeanArr.length) {
            Map<String, Integer> values = new HashMap<>();
            values.put("TrafficLightId", mMyRoadStatusBeanArr[mReqGetTrafficLightConfigActionIndex].getTrafficLightBean().getTrafficLightId());
            NetUtil.getNetUtil().addRequest("GetTrafficLightConfigAction.do", values, TrafficLightBean.class);
        } else {
            mReqGetTrafficLightConfigActionIndex = 0;
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void GetTrafficLightNowStatus(Map<String, Object> map) {
        MyRoadStatusBean localMyRoadStatusBean = mMyRoadStatusBeanArr[mReqGetTrafficLightNowStatusIndex];
        TrafficLightBean localTrafficLightBean = localMyRoadStatusBean.getTrafficLightBean();
        String status = (String) map.get("Status");
        localTrafficLightBean.setTime(map.get("Time") + "");
        localTrafficLightBean.setStatus(status);
        mReqGetTrafficLightNowStatusIndex++;
        if (mReqGetTrafficLightNowStatusIndex < mMyRoadStatusBeanArr.length) {
            Map<String, Integer> values = new HashMap<>();
            values.put("TrafficLightId", mMyRoadStatusBeanArr[mReqGetTrafficLightNowStatusIndex].getTrafficLightBean().getTrafficLightId());
            NetUtil.getNetUtil().addRequest("GetTrafficLightNowStatus.do", values, Map.class);
        } else {
            mReqGetTrafficLightNowStatusIndex = 0;
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onDims() {
        mTimer.cancel();
        mTimer = null;
        mReqGetTrafficLightConfigActionIndex = 0;
        mReqGetTrafficLightNowStatusIndex = 0;
        mReqGetRoadStatusIndex = 0;
        mReqSetTrafficLightConfigIndex = 0;
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void GetRoadStatus(MyRoadStatusBean myRoadStatusBean) {
        MyRoadStatusBean localMyRoadStatusBean = mMyRoadStatusBeanArr[mReqGetRoadStatusIndex];
        if (myRoadStatusBean.getStatus() > 3) {
            notifyMsg(localMyRoadStatusBean.getRoadId(), myRoadStatusBean.getStatus());
        } else {
            clearMsg(localMyRoadStatusBean.getRoadId());
        }
        mReqGetRoadStatusIndex++;
        if (mReqGetRoadStatusIndex < mMyRoadStatusBeanArr.length) {
            Map<String, Integer> values = new HashMap<>();
            values.put("RoadId", mMyRoadStatusBeanArr[mReqGetRoadStatusIndex].getRoadId());
            NetUtil.getNetUtil().addRequest("GetRoadStatus.do", values, MyRoadStatusBean.class);
        }
    }

    private void clearMsg(Integer roadId) {
        NotificationManager notificationManager = (NotificationManager) mFragmentActivity.getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.cancel(roadId);
    }

    private void notifyMsg(Integer roadId, Integer status) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mFragmentActivity);
        Notification notification = builder.setAutoCancel(true).setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(roadId + "号道路处于拥挤堵塞状态,请选择合适路线").build();
        NotificationManager notificationManager = (NotificationManager) mFragmentActivity.getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.notify(roadId, notification);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.myroadstatus_btn_horcontrol:
            case R.id.myroadstatus_btn_vercontrol:
                showConfigDialog(v.getTag());
                break;
            case R.id.myroadstatus_btn_confirm:
                configTrafficLight(v.getTag());
                break;
            case R.id.myroadstatus_ibtn_cancel:
            case R.id.myroadstatus_btn_cancel:
                mAlertDialog.dismiss();
                mAlertDialog = null;
                break;
        }
    }

    private void configTrafficLight(Object tag) {
        String strRedTime = myroadstatusEtRedtime.getText().toString();
        String strYellowTime = myroadstatusEtYellowtime.getText().toString();
        String strGreenTime = myroadstatusEtGreentime.getText().toString();
        if (TextUtils.isEmpty(strGreenTime) || TextUtils.isEmpty(strYellowTime) || TextUtils.isEmpty(strRedTime)) {
            showToast("不可以为空");
            return;
        }
        if (!strGreenTime.matches("^[1-9]\\d?$")) {
            showToast("绿灯配置非法");
            return;
        }
        if (!strYellowTime.matches("^[1-9]\\d?$")) {
            showToast("黄灯配置非法");
            return;
        }
        if (!strRedTime.matches("^[1-9]\\d?$")) {
            showToast("红灯配置非法");
            return;
        }
        mConfigTrafficLightIdList = (List<Integer>) tag;
        mConfigTrafficLightValues = new HashMap<>();
        mConfigTrafficLightValues.put("TrafficLightId", mConfigTrafficLightIdList.get(mReqSetTrafficLightConfigIndex));
        mConfigTrafficLightValues.put("RedTime", Integer.parseInt(strRedTime));
        mConfigTrafficLightValues.put("YellowTime", Integer.parseInt(strYellowTime));
        mConfigTrafficLightValues.put("GreenTime", Integer.parseInt(strGreenTime));
        NetUtil.getNetUtil().addRequest("SetTrafficLightConfig.do", mConfigTrafficLightValues, SetTrafficLightConfigBean.class);
        mAlertDialog.dismiss();
        mAlertDialog = null;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void SetTrafficLightConfig(SetTrafficLightConfigBean setTrafficLightConfigBean) {
        mReqSetTrafficLightConfigIndex++;
        if (mReqSetTrafficLightConfigIndex < mConfigTrafficLightIdList.size()) {
            mConfigTrafficLightValues.put("TrafficLightId", mConfigTrafficLightIdList.get(mReqSetTrafficLightConfigIndex));
            NetUtil.getNetUtil().addRequest("SetTrafficLightConfig.do", mConfigTrafficLightValues, SetTrafficLightConfigBean.class);
        } else {
            showToast("配置成功");
            mConfigTrafficLightIdList = null;
            mConfigTrafficLightValues = null;
            mReqGetTrafficLightConfigActionIndex = 0;
            Map<String, Integer> values = new HashMap<>();
            values.put("TrafficLightId", mMyRoadStatusBeanArr[mReqGetTrafficLightConfigActionIndex].getTrafficLightBean().getTrafficLightId());
            NetUtil.getNetUtil().addRequest("GetTrafficLightConfigAction.do", values, TrafficLightBean.class);
        }
    }

    private void showConfigDialog(Object tag) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mFragmentActivity);
        mAlertDialog = builder.create();
        mAlertDialog.show();
        View view = LayoutInflater.from(mFragmentActivity).inflate(R.layout.dialog_configtrafficlight, null);
        mAlertDialog.setContentView(view);
        myroadstatusIbtnCancel = (ImageButton) view.findViewById(R.id.myroadstatus_ibtn_cancel);
        myroadstatusEtRedtime = (EditText) view.findViewById(R.id.myroadstatus_et_redtime);
        myroadstatusEtYellowtime = (EditText) view.findViewById(R.id.myroadstatus_et_yellowtime);
        myroadstatusEtGreentime = (EditText) view.findViewById(R.id.myroadstatus_et_greentime);
        myroadstatusBtnConfirm = (Button) view.findViewById(R.id.myroadstatus_btn_confirm);
        myroadstatusBtnCancel = (Button) view.findViewById(R.id.myroadstatus_btn_cancel);
        myroadstatusIbtnCancel.setOnClickListener(this);
        myroadstatusBtnCancel.setOnClickListener(this);
        myroadstatusBtnConfirm.setOnClickListener(this);
        myroadstatusBtnConfirm.setTag(tag);
    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mMyRoadStatusBeanMap.size();
        }

        @Override
        public MyRoadStatusBean getItem(int i) {
            MyRoadStatusBean myRoadStatusBean = mMyRoadStatusBeanMap.get(mMyRoadStatusBeanArr[i].getRoadId());
            TrafficLightBean trafficLightBean = myRoadStatusBean.getTrafficLightBean();
            String status = trafficLightBean.getStatus();
            if (status != null) {
                switch (status) {
                    case "Red":
                        trafficLightBean.setStatusDesc("红灯");
                        trafficLightBean.setStatusResId(R.drawable.shape_oval_red);
                        break;
                    case "Yellow":
                        trafficLightBean.setStatusDesc("黄灯");
                        trafficLightBean.setStatusResId(R.drawable.shape_oval_yellow);
                        break;
                    case "Green":
                        trafficLightBean.setStatusDesc("绿灯");
                        trafficLightBean.setStatusResId(R.drawable.shape_oval_green);
                        break;
                }
            }
            return myRoadStatusBean;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (view == null) {
                view = LayoutInflater.from(mFragmentActivity).inflate(R.layout.item_myroadstatus_lv_content,
                        mMyroadstatusLvContent, false);
                viewHolder = new ViewHolder(view);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            MyRoadStatusBean myRoadStatusBean = getItem(i);
            TrafficLightBean trafficLightBean = myRoadStatusBean.getTrafficLightBean();
            viewHolder.myroadstatus_tv_roadiv.setText(myRoadStatusBean.getRoadId() + "");
            viewHolder.myroadstatus_tv_redtime.setText("红灯" + trafficLightBean.getRedTime() + "秒");
            viewHolder.myroadstatus_tv_yellowtime.setText("黄灯" + trafficLightBean.getYellowTime() + "秒");
            viewHolder.myroadstatus_tv_greentime.setText("绿灯" + trafficLightBean.getGreenTime() + "秒");
            viewHolder.myroadstatus_tv_horstatus.setText("横向状态:" + trafficLightBean.getStatusDesc() + trafficLightBean.getTime() + "秒");
            String status = trafficLightBean.getStatus();
            viewHolder.myroadstatus_tv_verstatus.setText("纵向状态" + trafficLightBean.getStatusDesc() + trafficLightBean.getTime() + "秒");
            viewHolder.myroadstatus_iv_horstatus.setImageResource(trafficLightBean.getStatusResId());
            viewHolder.myroadstatus_iv_verstatus.setImageResource(trafficLightBean.getStatusResId());
            if (!"Green".equals(trafficLightBean.getStatus())) {
                viewHolder.myroadstatus_btn_horcontrol.setOnClickListener(MyRoadStatusFragment.this);
                viewHolder.myroadstatus_btn_vercontrol.setOnClickListener(MyRoadStatusFragment.this);
            } else {
                //showToast("绿灯周期内不可进行配置");
                viewHolder.myroadstatus_btn_horcontrol.setOnClickListener(null);
                viewHolder.myroadstatus_btn_vercontrol.setOnClickListener(null);
            }
            List<Integer> intList = new ArrayList<>();
            intList.add(trafficLightBean.getTrafficLightId());
            viewHolder.myroadstatus_btn_horcontrol.setTag(intList);
            viewHolder.myroadstatus_btn_vercontrol.setTag(intList);
            return view;
        }

    }

    private static class ViewHolder {
        public View rootView;
        public TextView myroadstatus_tv_roadiv;
        public TextView myroadstatus_tv_greentime;
        public TextView myroadstatus_tv_yellowtime;
        public TextView myroadstatus_tv_redtime;
        public TextView myroadstatus_tv_horstatus;
        public ImageView myroadstatus_iv_horstatus;
        public TextView myroadstatus_tv_verstatus;
        public ImageView myroadstatus_iv_verstatus;
        public Button myroadstatus_btn_horcontrol;
        public Button myroadstatus_btn_vercontrol;

        public ViewHolder(View rootView) {
            this.rootView = rootView;
            this.myroadstatus_tv_roadiv = (TextView) rootView.findViewById(R.id.myroadstatus_tv_roadiv);
            this.myroadstatus_tv_greentime = (TextView) rootView.findViewById(R.id.myroadstatus_tv_greentime);
            this.myroadstatus_tv_yellowtime = (TextView) rootView.findViewById(R.id.myroadstatus_tv_yellowtime);
            this.myroadstatus_tv_redtime = (TextView) rootView.findViewById(R.id.myroadstatus_tv_redtime);
            this.myroadstatus_tv_horstatus = (TextView) rootView.findViewById(R.id.myroadstatus_tv_horstatus);
            this.myroadstatus_iv_horstatus = (ImageView) rootView.findViewById(R.id.myroadstatus_iv_horstatus);
            this.myroadstatus_tv_verstatus = (TextView) rootView.findViewById(R.id.myroadstatus_tv_verstatus);
            this.myroadstatus_iv_verstatus = (ImageView) rootView.findViewById(R.id.myroadstatus_iv_verstatus);
            this.myroadstatus_btn_horcontrol = (Button) rootView.findViewById(R.id.myroadstatus_btn_horcontrol);
            this.myroadstatus_btn_vercontrol = (Button) rootView.findViewById(R.id.myroadstatus_btn_vercontrol);
        }

    }

    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            mReqGetTrafficLightNowStatusIndex = 0;
            mReqGetRoadStatusIndex = 0;
            Map<String, Integer> values = new HashMap<>();
            values.put("TrafficLightId", mMyRoadStatusBeanArr[mReqGetTrafficLightNowStatusIndex].getTrafficLightBean().getTrafficLightId());
            NetUtil.getNetUtil().addRequest("GetTrafficLightNowStatus.do", values, Map.class);
            values.put("RoadId", mMyRoadStatusBeanArr[mReqGetRoadStatusIndex].getRoadId());
            NetUtil.getNetUtil().addRequest("GetRoadStatus.do", values, MyRoadStatusBean.class);
        }
    }


}

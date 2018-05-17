package org.chengpx.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.chengpx.R;
import org.chengpx.base.BaseFragment;
import org.chengpx.domain.EnvBean;
import org.chengpx.domain.RoadBean;
import org.chengpx.domain.RoadStatusBean;
import org.chengpx.util.net.NetUtil;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 第13题编码实现路况查询模块
 * <p>
 * create at 2018/5/1 17:06 by chengpx
 */
public class RoadStatusQueryFragment extends BaseFragment implements View.OnClickListener {

    private TextView test13_tv_date;
    private Button test13_btn_flush;
    private ListView test13_lv_senselist;
    private ListView test13_lv_roadstatusexmaple;
    private RoadBean[] mRoadBeanArr = {
            new RoadBean(1), new RoadBean(2), new RoadBean(3),
            new RoadBean(4), new RoadBean(5), new RoadBean(6),
            new RoadBean(7)
    };
    private RoadStatusBean[] mRoadStatusBeanArr = {
            new RoadStatusBean(1, R.drawable.shape_rectangle_status1, "畅通"),
            new RoadStatusBean(2, R.drawable.shape_rectangle_status2, "缓行"),
            new RoadStatusBean(3, R.drawable.shape_rectangle_status3, "一般拥堵"),
            new RoadStatusBean(4, R.drawable.shape_rectangle_status4, "中度拥堵"),
            new RoadStatusBean(5, R.drawable.shape_rectangle_status5, "严重拥堵")
    };
    private EnvBean[] mEnvBeanArr = {
            new EnvBean("temperature", "温度", "℃"),
            new EnvBean("humidity", "相对湿度", "%"),
            new EnvBean("pm2.5", "PM2.5", "ug/m3")
    };
    private View[][] mRoadUiViewArr;
    private Map<String, EnvBean> mSenseDataMap;
    private SenseAdapter mSenseAdapter;
    private Timer mTimer;
    private int mRoadIdReqIndex;
    private Map<Integer, RoadStatusBean> mRoadStatusBeanMap;
    private ProgressDialog mSenseLoadDialog;
    private ProgressDialog mRoadStatusDialog;

    @Override
    protected void initListener() {
        test13_btn_flush.setOnClickListener(this);
    }

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_roadstatusquery, container, false);
        TextView test13_tv_hcksltop = (TextView) view.findViewById(R.id.test13_tv_hcksltop);
        TextView test13_tv_hckslleft = (TextView) view.findViewById(R.id.test13_tv_hckslleft);
        TextView test13_tv_hckslbottom = (TextView) view.findViewById(R.id.test13_tv_hckslbottom);
        TextView test13_tv_hcgsright = (TextView) view.findViewById(R.id.test13_tv_hcgsright);
        TextView test13_tv_xuexiaoroad = (TextView) view.findViewById(R.id.test13_tv_xuexiaoroad);
        TextView test13_tv_xinfuroad = (TextView) view.findViewById(R.id.test13_tv_xinfuroad);
        TextView test13_tv_lenovoroad = (TextView) view.findViewById(R.id.test13_tv_lenovoroad);
        TextView test13_tv_hospitalroad = (TextView) view.findViewById(R.id.test13_tv_hospitalroad);
        TextView test13_tv_parking = (TextView) view.findViewById(R.id.test13_tv_parking);
        test13_tv_date = (TextView) view.findViewById(R.id.test13_tv_date);
        test13_btn_flush = (Button) view.findViewById(R.id.test13_btn_flush);
        test13_lv_senselist = (ListView) view.findViewById(R.id.test13_lv_senselist);
        test13_lv_roadstatusexmaple = (ListView) view.findViewById(R.id.test13_lv_roadstatusexmaple);
        mRoadUiViewArr = new View[][]{
                {test13_tv_xuexiaoroad}, {test13_tv_lenovoroad}, {test13_tv_hospitalroad}, {test13_tv_xinfuroad},
                {test13_tv_hcksltop, test13_tv_hckslleft, test13_tv_hckslbottom}, {test13_tv_hcgsright}, {test13_tv_parking}
        };
        return view;
    }

    @Override
    protected void onDie() {
        mRoadUiViewArr = null;
    }

    @Override
    protected void main() {
        test13_lv_roadstatusexmaple.setAdapter(new RoadstatusexmapleAdapter());
        mSenseAdapter = new SenseAdapter();
        test13_lv_senselist.setAdapter(mSenseAdapter);
    }

    @Override
    protected void initData() {
        EventBus.getDefault().register(this);
        mRoadStatusBeanMap = new HashMap<>();
        for (RoadStatusBean roadStatusBean : mRoadStatusBeanArr) {
            mRoadStatusBeanMap.put(roadStatusBean.getValue(), roadStatusBean);
        }
        Date date = Calendar.getInstance(Locale.CHINA).getTime();
        test13_tv_date.setText(String.format(Locale.CHINA, "%tY-%tm-%td\n%tA", date, date, date, date));
        mSenseDataMap = new HashMap<>();
        NetUtil.getNetUtil().addRequest("GetAllSense.do", null, Map.class);
        mSenseLoadDialog = showLoadingDialog("传感器数据加载", "");
        mTimer = new Timer();
        mTimer.schedule(new GetRoadStatusTimerTask(), 0, 3000);
        mRoadStatusDialog = showLoadingDialog("道路状态加载", "");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getAllSense(Map<String, Double> map) {
        if (mSenseLoadDialog != null) {
            mSenseLoadDialog.dismiss();
            mSenseLoadDialog = null;
        }
        for (EnvBean envBean : mEnvBeanArr) {
            envBean.setVal(map.get(envBean.getSenseName()).intValue());
            mSenseDataMap.put(envBean.getSenseName(), envBean);
        }
        if (mSenseAdapter != null) {
            mSenseAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDims() {
        mSenseDataMap = null;
        mSenseAdapter = null;
        mTimer.cancel();
        mTimer = null;
        mRoadIdReqIndex = 0;
        EventBus.getDefault().unregister(this);
    }

    //点击按钮刷新传感器
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.test13_btn_flush:
                NetUtil.getNetUtil().addRequest("GetAllSense.do", null, Map.class);
                mSenseLoadDialog = showLoadingDialog("传感器数据加载", "");
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getRoadStatus(RoadBean roadBean) {
        View[] viewArr = mRoadUiViewArr[mRoadIdReqIndex];
        for (View view : viewArr) {
            view.setBackgroundResource(mRoadStatusBeanMap.get(roadBean.getStatus()).getResId());
        }
        mRoadIdReqIndex++;
        if (mRoadIdReqIndex < mRoadBeanArr.length) {
            Map<String, Integer> reqValues = new HashMap<>();
            reqValues.put("RoadId", mRoadBeanArr[mRoadIdReqIndex].getRoadId());
            NetUtil.getNetUtil().addRequest("GetRoadStatus.do", reqValues, RoadBean.class);
        } else {
            if (mRoadStatusDialog != null) {
                mRoadStatusDialog.dismiss();
                mRoadStatusDialog = null;
            }
        }
    }

    //定时器，定时刷新道路状态
    private class GetRoadStatusTimerTask extends TimerTask {

        @Override
        public void run() {
            mRoadIdReqIndex = 0;
            Map<String, Integer> reqValues = new HashMap<>();
            reqValues.put("RoadId", mRoadBeanArr[mRoadIdReqIndex].getRoadId());
            NetUtil.getNetUtil().addRequest("GetRoadStatus.do", reqValues, RoadBean.class);
        }

    }

    private class SenseAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mSenseDataMap == null ? 0 : mSenseDataMap.size();
        }

        @Override
        public EnvBean getItem(int position) {
            return mSenseDataMap.get(mEnvBeanArr[position].getSenseName());
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SenseViewHolder senseViewHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mFragmentActivity).inflate(R.layout.lv_test13_lv_senselist,
                        test13_lv_senselist, false);
                senseViewHolder = SenseViewHolder.get(convertView);
            } else {
                senseViewHolder = (SenseViewHolder) convertView.getTag();
            }
            EnvBean envBean = getItem(position);
            senseViewHolder.getTest13_tv_sensedesc().setText(envBean.getSenseDesc());
            senseViewHolder.getTest13_tv_senseval().setText(envBean.getVal() + envBean.getUnit());
            return convertView;
        }

    }

    private static class SenseViewHolder {

        private final TextView test13_tv_sensedesc;
        private final TextView test13_tv_senseval;

        private SenseViewHolder(View view) {
            test13_tv_sensedesc = view.findViewById(R.id.test13_tv_sensedesc);
            test13_tv_senseval = view.findViewById(R.id.test13_tv_senseval);
        }

        public static SenseViewHolder get(View view) {
            Object tag = view.getTag();
            if (tag == null) {
                tag = new SenseViewHolder(view);
                view.setTag(tag);
            }
            return (SenseViewHolder) tag;
        }

        public TextView getTest13_tv_sensedesc() {
            return test13_tv_sensedesc;
        }

        public TextView getTest13_tv_senseval() {
            return test13_tv_senseval;
        }

    }

    private class RoadstatusexmapleAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mRoadStatusBeanArr.length;
        }

        @Override
        public RoadStatusBean getItem(int position) {
            return mRoadStatusBeanArr[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            RoadstatusexmapleViewHolder roadstatusexmapleViewHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mFragmentActivity).inflate(R.layout.lv_test13_lv_roadstatusexmaple,
                        test13_lv_roadstatusexmaple, false);
                roadstatusexmapleViewHolder = RoadstatusexmapleViewHolder.get(convertView);
            } else {
                roadstatusexmapleViewHolder = (RoadstatusexmapleViewHolder) convertView.getTag();
            }
            RoadStatusBean roadStatusBean = getItem(position);
            roadstatusexmapleViewHolder.getTest13_iv_statusimg().setImageResource(roadStatusBean.getResId());
            roadstatusexmapleViewHolder.getTest13_tv_statusdesc().setText(roadStatusBean.getDesc());
            return convertView;
        }

    }

    private static class RoadstatusexmapleViewHolder {

        private final TextView test13_tv_statusdesc;
        private final ImageView test13_iv_statusimg;

        private RoadstatusexmapleViewHolder(View view) {
            test13_tv_statusdesc = view.findViewById(R.id.test13_tv_statusdesc);
            test13_iv_statusimg = view.findViewById(R.id.test13_iv_statusimg);
        }

        public static RoadstatusexmapleViewHolder get(View view) {
            Object tag = view.getTag();
            if (tag == null) {
                tag = new RoadstatusexmapleViewHolder(view);
                view.setTag(tag);
            }
            return (RoadstatusexmapleViewHolder) tag;
        }

        public TextView getTest13_tv_statusdesc() {
            return test13_tv_statusdesc;
        }

        public ImageView getTest13_iv_statusimg() {
            return test13_iv_statusimg;
        }

    }

}

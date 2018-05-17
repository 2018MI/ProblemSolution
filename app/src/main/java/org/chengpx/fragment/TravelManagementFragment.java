package org.chengpx.fragment;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import org.chengpx.R;
import org.chengpx.base.BaseFragment;
import org.chengpx.domain.CarBean;
import org.chengpx.domain.TrafficLightBean;
import org.chengpx.util.net.NetUtil;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class TravelManagementFragment extends BaseFragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private String mTag = getClass().getName();

    private TextView mTest1TvDate;
    private TextView mTest1TvEnablecariddesc;
    private TextView mTest1TvEnablecarids;
    private ListView mTest1LvData;
    private ImageView mTest1IvRedlight;
    private ImageView mTest1IvYellowlight;
    private ImageView mTest1IvGreenlight;
    private Calendar calendar;
    private String enableCarDesc;
    private List<Integer> enableCarIdList;
    private CarBean[] mCarBeanArr = {
            new CarBean(1), new CarBean(2), new CarBean(3)
    };
    private int mReqGetCarMoveIndex;
    private Map<Integer, CarBean> carBeanMap;
    private MyAdapter myAdapter;
    private Timer timer;
    private DatePickerDialog datePickerDialog;
    private ProgressDialog mCarActionLoadDialog;
    private ProgressDialog mTrafficStatusLoadDialog;
    private ProgressDialog mCarActionSetDialog;

    protected void initListener() {
        mTest1TvDate.setOnClickListener(this);
    }

    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_travelmanagement, container, false);
        mTest1TvDate = (TextView) view.findViewById(R.id.test1_tv_date);
        mTest1TvEnablecariddesc = (TextView) view.findViewById(R.id.test1_tv_enablecariddesc);
        mTest1TvEnablecarids = (TextView) view.findViewById(R.id.test1_tv_enablecarids);
        mTest1LvData = (ListView) view.findViewById(R.id.test1_lv_data);
        mTest1IvRedlight = (ImageView) view.findViewById(R.id.test1_iv_redlight);
        mTest1IvYellowlight = (ImageView) view.findViewById(R.id.test1_iv_yellowlight);
        mTest1IvGreenlight = (ImageView) view.findViewById(R.id.test1_iv_greenlight);
        return view;
    }

    protected void onDie() {

    }

    protected void main() {
        mTest1TvDate.setText(new SimpleDateFormat("yyyy年MM月dd日").format(calendar.getTime()));
        mTest1TvEnablecariddesc.setText(enableCarDesc);
        mTest1TvEnablecarids
                .setText(enableCarIdList.toString().replace("[", "").replace("]", ""));
        myAdapter = new MyAdapter();
        mTest1LvData.setAdapter(myAdapter);
    }

    protected void initData() {
        EventBus.getDefault().register(this);
        calendar = Calendar.getInstance(Locale.CHINA);
        carBeanMap = new HashMap<>();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        if (day % 2 == 0) {// 双号
            enableCarDesc = "双号出行车辆: ";
        } else {
            enableCarDesc = "单号出行车辆: ";
        }
        enableCarIdList = new ArrayList<>();
        for (int index = 0; index < mCarBeanArr.length; index++) {
            CarBean carBean = mCarBeanArr[index];
            if (day % 2 == carBean.getCarId() % 2) {
                enableCarIdList.add(carBean.getCarId());
                carBean.setEnable(true);
            } else {
                carBean.setEnable(false);
            }
        }
        Map<String, Integer> values = new HashMap<>();
        values.put("CarId", mCarBeanArr[mReqGetCarMoveIndex].getCarId());
        NetUtil.getNetUtil().addRequest("GetCarMove.do", values, CarBean.class);
        mCarActionLoadDialog = showLoadingDialog("小车状态查询", "");
        timer = new Timer();
        timer.schedule(new MyTimerTask(), 0, 3000);
        mTrafficStatusLoadDialog = showLoadingDialog("红绿灯状态查询", "");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getCarMove(CarBean carBean) {
        CarBean localCarBean = mCarBeanArr[mReqGetCarMoveIndex];
        localCarBean.setCarAction(carBean.getCarAction());
        carBeanMap.put(localCarBean.getCarId(), localCarBean);
        mReqGetCarMoveIndex++;
        if (mReqGetCarMoveIndex < mCarBeanArr.length) {
            Map<String, Integer> values = new HashMap<>();
            values.put("CarId", mCarBeanArr[mReqGetCarMoveIndex].getCarId());
            NetUtil.getNetUtil().addRequest("GetCarMove.do", values, CarBean.class);
        } else {
            if (mCarActionLoadDialog != null) {
                mCarActionLoadDialog.dismiss();
                mCarActionLoadDialog = null;
            }
            if (myAdapter != null) {
                myAdapter.notifyDataSetChanged();
            }
        }
    }

    protected void onDims() {
        EventBus.getDefault().unregister(this);
        timer.cancel();
        timer = null;
        mReqGetCarMoveIndex = 0;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (!compoundButton.isShown()) {
            return;
        }
        CarBean carBean = (CarBean) compoundButton.getTag();
        Map<String, Object> values = new HashMap<>();
        values.put("CarId", carBean.getCarId());
        if (b) {
            values.put("CarAction", "Start");
        } else {
            values.put("CarAction", "Stop");
        }
        NetUtil.getNetUtil().addRequest("SetCarMove", values, Map.class);
        mCarActionSetDialog = showLoadingDialog("小车动作设置", "");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setCarMove(Map<String, String> map) {
        if (mCarActionSetDialog != null) {
            mCarActionSetDialog.dismiss();
            mCarActionSetDialog = null;
        }
        mReqGetCarMoveIndex = 0;
        Map<String, Integer> values = new HashMap<>();
        values.put("CarId", mCarBeanArr[mReqGetCarMoveIndex].getCarId());
        NetUtil.getNetUtil().addRequest("GetCarMove.do", values, CarBean.class);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getTrafficLightNowStatus(TrafficLightBean trafficlightBean) {
        if (mTrafficStatusLoadDialog != null) {
            mTrafficStatusLoadDialog.dismiss();
            mTrafficStatusLoadDialog = null;
        }
        switch (trafficlightBean.getStatus()) {
            case "Red":
                mTest1IvRedlight.setImageResource(R.drawable.shape_oval_red);
                mTest1IvYellowlight.setImageResource(R.drawable.shape_oval_gray);
                mTest1IvGreenlight.setImageResource(R.drawable.shape_oval_gray);
                break;
            case "Yellow":
                mTest1IvRedlight.setImageResource(R.drawable.shape_oval_gray);
                mTest1IvYellowlight.setImageResource(R.drawable.shape_oval_yellow);
                mTest1IvGreenlight.setImageResource(R.drawable.shape_oval_gray);
                break;
            case "Green":
                mTest1IvRedlight.setImageResource(R.drawable.shape_oval_gray);
                mTest1IvYellowlight.setImageResource(R.drawable.shape_oval_gray);
                mTest1IvGreenlight.setImageResource(R.drawable.shape_oval_green);
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.test1_tv_date:
                showDialog();
                break;
        }
    }

    private void showDialog() {
        datePickerDialog = new DatePickerDialog(mFragmentActivity,
                this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        Log.d(mTag, "datePicker = " + datePicker.toString() + ", i = " + i + ", i1 = " + i1 + ", i2 = " + i2);
        calendar.set(Calendar.DAY_OF_MONTH, i2);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        if (day % 2 == 0) {// 双号
            enableCarDesc = "双号出行车辆: ";
        } else {
            enableCarDesc = "单号出行车辆: ";
        }
        enableCarIdList = new ArrayList<>();
        for (int index = 0; index < mCarBeanArr.length; index++) {
            CarBean carBean = mCarBeanArr[index];
            if (day % 2 == carBean.getCarId() % 2) {
                enableCarIdList.add(carBean.getCarId());
                carBean.setEnable(true);
            } else {
                carBean.setEnable(false);
            }
        }
        mTest1TvDate.setText(new SimpleDateFormat("yyyy年MM月dd日").format(calendar.getTime()));
        mTest1TvEnablecariddesc.setText(enableCarDesc);
        mTest1TvEnablecarids
                .setText(enableCarIdList.toString().replace("[", "").replace("]", ""));
        myAdapter.notifyDataSetChanged();
    }

    private static class ViewHolder {
        public View rootView;
        public TextView test1_tv_lvcarid;
        public Switch test1_switch_lvcontrol;

        public ViewHolder(View rootView) {
            this.rootView = rootView;
            this.test1_tv_lvcarid = (TextView) rootView.findViewById(R.id.test1_tv_lvcarid);
            this.test1_switch_lvcontrol = (Switch) rootView.findViewById(R.id.test1_switch_lvcontrol);
        }

    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return carBeanMap == null ? 0 : carBeanMap.size();
        }

        @Override
        public CarBean getItem(int i) {
            return carBeanMap.get(mCarBeanArr[i].getCarId());
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder = null;
            if (view == null) {
                view = LayoutInflater.from(mFragmentActivity).inflate(R.layout.item_test1_lv_data,
                        mTest1LvData, false);
                viewHolder = new ViewHolder(view);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            CarBean carBean = getItem(i);
            viewHolder.test1_tv_lvcarid.setText(carBean.getCarId() + "号");
            viewHolder.test1_switch_lvcontrol.setEnabled(carBean.getEnable());
            if ("Start".equals(carBean.getCarAction())) {
                viewHolder.test1_switch_lvcontrol.setChecked(true);
            } else if ("Stop".equals(carBean.getCarAction())) {
                viewHolder.test1_switch_lvcontrol.setChecked(false);
            }
            if (viewHolder.test1_switch_lvcontrol.isEnabled()) {
                viewHolder.test1_switch_lvcontrol.setOnCheckedChangeListener(TravelManagementFragment.this);
                viewHolder.test1_switch_lvcontrol.setTag(carBean);
            }
            return view;
        }


    }

    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            Map<String, Integer> values = new HashMap<>();
            values.put("TrafficLightId", 1);
            NetUtil.getNetUtil().addRequest("GetTrafficLightNowStatus.do", values,
                    TrafficLightBean.class);
        }
    }
}

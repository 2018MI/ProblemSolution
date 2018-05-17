package org.chengpx.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.chengpx.R;
import org.chengpx.base.BaseFragment;
import org.chengpx.domain.CarBalanceRechargeHistoryBean;
import org.chengpx.util.db.CarBalanceRechargeHistoryDao;
import org.chengpx.util.net.NetUtil;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CarManagerFragment extends BaseFragment implements View.OnClickListener {

    private Button mCarmanagerBtnMulitrecharge;
    private Button mCarmanagerBtnRechargehistory;
    private ListView mCarmanagerLvData;
    private CarBalanceRechargeHistoryBean[] mCarBalanceRechargeHistoryBeanArr = {
            new CarBalanceRechargeHistoryBean(1, "admin", R.drawable.ford), new CarBalanceRechargeHistoryBean(2, "admin", R.drawable.ford1),
            new CarBalanceRechargeHistoryBean(3, "admin", R.drawable.hongda), new CarBalanceRechargeHistoryBean(4, "admin", R.drawable.hongda1)
    };
    private int mReqGetCarAccountBalanceIndex;
    private Map<Integer, CarBalanceRechargeHistoryBean> mCarBalanceRechargeHistoryBeanMap;
    private MyAdapter mAdapter;
    private List<Integer> mRechargeCarIdList;
    private AlertDialog mAlertDialog;
    private TextView mCarmanagerTvDialogrechargecarids;
    private EditText mCarmanagerEtDialogmoney;
    private Button mCarmanagerBtnDialogrecharge;
    private Button mCarmanagerBtnDialogcancel;
    private int mReqSetCarAccountRechargeIndex;
    private Map<String, Integer> mRechargeValues;
    private CarBalanceRechargeHistoryDao mCarBalanceRechargeHistoryDao;
    private CarBalanceRechargeHistoryBean mRechargeBean;
    private String mTag = getClass().getName();
    private ProgressDialog mCarBalanceSetDialog;
    private ProgressDialog mCarBalanceQueryDialog;

    @Override
    protected void initListener() {
        mCarmanagerBtnMulitrecharge.setOnClickListener(this);
        mCarmanagerBtnRechargehistory.setOnClickListener(this);
    }

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_carmanager, container, false);
        mCarmanagerBtnMulitrecharge = (Button) view.findViewById(R.id.carmanager_btn_mulitrecharge);
        mCarmanagerBtnRechargehistory = (Button) view.findViewById(R.id.carmanager_btn_rechargehistory);
        mCarmanagerLvData = (ListView) view.findViewById(R.id.carmanager_lv_data);
        return view;
    }

    @Override
    protected void onDie() {

    }

    @Override
    protected void main() {
        mAdapter = new MyAdapter();
        mCarmanagerLvData.setAdapter(mAdapter);
    }

    @Override
    protected void initData() {
        EventBus.getDefault().register(this);
        mCarBalanceRechargeHistoryDao = CarBalanceRechargeHistoryDao.getInstance(mFragmentActivity);
        mCarBalanceRechargeHistoryBeanMap = new HashMap<>();
        Map<String, Integer> values = new HashMap<>();
        values.put("CarId", mCarBalanceRechargeHistoryBeanArr[mReqGetCarAccountBalanceIndex].getCarId());
        NetUtil.getNetUtil().addRequest("GetCarAccountBalance.do", values, CarBalanceRechargeHistoryBean.class);
        mCarBalanceQueryDialog = showLoadingDialog("小车余额查询", "");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void GetCarAccountBalance(CarBalanceRechargeHistoryBean carBalanceRechargeHistoryBean) {
        CarBalanceRechargeHistoryBean localCarBalanceRechargeHistoryBean = mCarBalanceRechargeHistoryBeanArr[mReqGetCarAccountBalanceIndex];
        localCarBalanceRechargeHistoryBean.setBalance(carBalanceRechargeHistoryBean.getBalance());
        mCarBalanceRechargeHistoryBeanMap.put(localCarBalanceRechargeHistoryBean.getCarId(), localCarBalanceRechargeHistoryBean);
        mReqGetCarAccountBalanceIndex++;
        if (mReqGetCarAccountBalanceIndex < mCarBalanceRechargeHistoryBeanArr.length) {
            Map<String, Integer> values = new HashMap<>();
            values.put("CarId", mCarBalanceRechargeHistoryBeanArr[mReqGetCarAccountBalanceIndex].getCarId());
            NetUtil.getNetUtil().addRequest("GetCarAccountBalance.do", values, CarBalanceRechargeHistoryBean.class);
        } else {
            if (mCarBalanceQueryDialog != null) {
                mCarBalanceQueryDialog.dismiss();
                mCarBalanceQueryDialog = null;
            }
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onDims() {
        mReqGetCarAccountBalanceIndex = 0;
        mReqSetCarAccountRechargeIndex = 0;
        mAdapter = null;
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.carmanager_btn_mulitrecharge:
                List<Integer> intList = new ArrayList<>();
                for (int index = 0; index < mCarmanagerLvData.getChildCount(); index++) {
                    View childAtView = mCarmanagerLvData.getChildAt(index);
                    ViewHolder viewHolder = (ViewHolder) childAtView.getTag();
                    if (!viewHolder.carmanager_checkbox_lv_select.isChecked()) {
                        continue;
                    }
                    intList.add(Integer.parseInt(viewHolder.carmanager_tv_lvcarid.getText().toString()));
                }
                if (intList.size() == 0) {
                    showToast("请至少选择一个");
                    return;
                }
                showRechargeDialog(intList);
                break;
            case R.id.carmanager_btn_rechargehistory:
                break;
            case R.id.carmanager_btn_lvrecharge:
                showRechargeDialog((List<Integer>) v.getTag());
                break;
            case R.id.carmanager_btn_dialogcancel:
                mAlertDialog.dismiss();
                mAlertDialog = null;
                break;
            case R.id.carmanager_btn_dialogrecharge:
                recharge((List<Integer>) v.getTag());
                break;
        }
    }

    private void recharge(List<Integer> intList) {
        String strMoney = mCarmanagerEtDialogmoney.getText().toString();
        if (TextUtils.isEmpty(strMoney)) {
            showToast("不可以为空");
            return;
        }
        if (!strMoney.matches("^[1-9]\\d{0,2}$")) {
            showToast("金额非法");
            return;
        }
        int money = Integer.parseInt(strMoney);
        if (money < 1 || money > 999) {
            showToast("金额非法");
            return;
        }
        mRechargeCarIdList = intList;
        mReqSetCarAccountRechargeIndex = 0;
        mRechargeValues = new HashMap<>();
        mRechargeValues.put("CarId", mRechargeCarIdList.get(mReqSetCarAccountRechargeIndex));
        mRechargeValues.put("Money", money);
        mRechargeBean = new CarBalanceRechargeHistoryBean(mRechargeCarIdList.get(mReqSetCarAccountRechargeIndex),
                money, new Date(), "admin");
        NetUtil.getNetUtil().addRequest("SetCarAccountRecharge.do", mRechargeValues, Map.class);
        mAlertDialog.dismiss();
        mAlertDialog = null;
        mCarBalanceSetDialog = showLoadingDialog("小车账户充值", "");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void SetCarAccountRecharge(Map<String, Integer> map) {
        int insert = mCarBalanceRechargeHistoryDao.insert(mRechargeBean);
        Log.d(mTag, "CarBalanceRechargeHistoryDao insert: " + insert);
        mReqSetCarAccountRechargeIndex++;
        if (mReqSetCarAccountRechargeIndex < mRechargeCarIdList.size()) {
            mRechargeValues.put("CarId", mRechargeCarIdList.get(mReqSetCarAccountRechargeIndex));
            mRechargeBean.setCarId(mRechargeCarIdList.get(mReqSetCarAccountRechargeIndex));
            mRechargeBean.setRechargeDateTime(new Date());
            NetUtil.getNetUtil().addRequest("SetCarAccountRecharge.do", mRechargeValues, Map.class);
        } else {
            mCarBalanceSetDialog.dismiss();
            mCarBalanceSetDialog = null;
            showToast("充值成功");
            mReqGetCarAccountBalanceIndex = 0;
            Map<String, Integer> values = new HashMap<>();
            values.put("CarId", mCarBalanceRechargeHistoryBeanArr[mReqGetCarAccountBalanceIndex].getCarId());
            NetUtil.getNetUtil().addRequest("GetCarAccountBalance.do", values, CarBalanceRechargeHistoryBean.class);
            mCarBalanceQueryDialog = showLoadingDialog("小车余额查询", "");
        }
    }

    private void showRechargeDialog(List<Integer> intList) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mFragmentActivity);
        mAlertDialog = builder.create();
        View view = LayoutInflater.from(mFragmentActivity).inflate(R.layout.dialog_carcharge, null);
        mAlertDialog.setView(view);
        mAlertDialog.show();
        mCarmanagerTvDialogrechargecarids = (TextView) view.findViewById(R.id.carmanager_tv_dialogrechargecarids);
        mCarmanagerEtDialogmoney = (EditText) view.findViewById(R.id.carmanager_et_dialogmoney);
        mCarmanagerBtnDialogrecharge = (Button) view.findViewById(R.id.carmanager_btn_dialogrecharge);
        mCarmanagerBtnDialogcancel = (Button) view.findViewById(R.id.carmanager_btn_dialogcancel);
        mCarmanagerTvDialogrechargecarids.setText(intList.toString());
        mCarmanagerBtnDialogrecharge.setOnClickListener(this);
        mCarmanagerBtnDialogcancel.setOnClickListener(this);
        mCarmanagerBtnDialogrecharge.setTag(intList);
    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mCarBalanceRechargeHistoryBeanMap.size();
        }

        @Override
        public CarBalanceRechargeHistoryBean getItem(int position) {
            return mCarBalanceRechargeHistoryBeanMap.get(mCarBalanceRechargeHistoryBeanArr[position].getCarId());
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mFragmentActivity).inflate(R.layout.item_carmanager_lv_data,
                        mCarmanagerLvData, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            CarBalanceRechargeHistoryBean carBalanceRechargeHistoryBean = getItem(position);
            viewHolder.carmanager_iv_lvcaricon.setImageResource(carBalanceRechargeHistoryBean.getResId());
            viewHolder.carmanager_tv_lvcarid.setText(carBalanceRechargeHistoryBean.getCarId() + "");
            viewHolder.carmanager_tv_lvuname.setText("车主: " + carBalanceRechargeHistoryBean.getUname());
            viewHolder.carmanager_tv_lvbalance.setText("余额: " + carBalanceRechargeHistoryBean.getBalance() + "元");
            if (carBalanceRechargeHistoryBean.getBalance() < 20) {
                viewHolder.mCarmanagerLlLvroot.setBackgroundColor(Color.parseColor("#ffffcc00"));
            } else {
                viewHolder.mCarmanagerLlLvroot.setBackgroundColor(Color.parseColor("#ffffffff"));
            }
            viewHolder.carmanager_btn_lvrecharge.setOnClickListener(CarManagerFragment.this);
            List<Integer> intList = new ArrayList<>();
            intList.add(carBalanceRechargeHistoryBean.getCarId());
            viewHolder.carmanager_btn_lvrecharge.setTag(intList);
            return convertView;
        }


    }

    private static class ViewHolder {
        public View rootView;
        public ImageView carmanager_iv_lvcaricon;
        public TextView carmanager_tv_lvcarid;
        public TextView carmanager_tv_lvuname;
        public TextView carmanager_tv_lvbalance;
        public CheckBox carmanager_checkbox_lv_select;
        public Button carmanager_btn_lvrecharge;
        public LinearLayout mCarmanagerLlLvroot;

        public ViewHolder(View rootView) {
            this.rootView = rootView;
            this.carmanager_iv_lvcaricon = (ImageView) rootView.findViewById(R.id.carmanager_iv_lvcaricon);
            this.carmanager_tv_lvcarid = (TextView) rootView.findViewById(R.id.carmanager_tv_lvcarid);
            this.carmanager_tv_lvuname = (TextView) rootView.findViewById(R.id.carmanager_tv_lvuname);
            this.carmanager_tv_lvbalance = (TextView) rootView.findViewById(R.id.carmanager_tv_lvbalance);
            this.carmanager_checkbox_lv_select = (CheckBox) rootView.findViewById(R.id.carmanager_checkbox_lv_select);
            this.carmanager_btn_lvrecharge = (Button) rootView.findViewById(R.id.carmanager_btn_lvrecharge);
            mCarmanagerLlLvroot = (LinearLayout) rootView.findViewById(R.id.carmanager_ll_lvroot);
        }

    }
}

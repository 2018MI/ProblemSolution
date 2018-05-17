package org.chengpx.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.chengpx.base.BaseFragment;
import org.chengpx.R;
import org.chengpx.domain.CarBean;
import org.chengpx.util.db.CarDao;
import org.chengpx.util.net.NetUtil;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class BalanceRechargeFragment extends BaseFragment implements View.OnClickListener {

    private String mTag = "org.chengpx.fragment.BalanceRechargeFragment";

    private Button balancerechargeBtnMulitpeRechage;
    private Button balancerechargeBtnRechargehistory;
    private ListView balancerechargeLvData;
    private MyAdapter myAdapter;
    private Map<Integer, CarBean> mCarBeanMap;
    private AlertDialog alertDialog;
    private TextView rechargedialogTvCarIds;
    private EditText rechargedialogEtMoney;
    private CarDao carDao;
    private CarBean[] mCarBeanArr = {new CarBean(1), new CarBean(2), new CarBean(3), new CarBean(4)};
    private String[] rechargeCarIdArr;
    private int mRechargeCarIdIndex;
    private int mReqGetCarAccountBalanceIndex;
    private int rechargeMoney;

    protected void initListener() {
        balancerechargeBtnMulitpeRechage.setOnClickListener(this);
        balancerechargeBtnRechargehistory.setOnClickListener(this);
    }

    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_balancerecharge, container, false);
        balancerechargeBtnMulitpeRechage = (Button) view.findViewById(R.id.balancerecharge_btn_mulitpeRechage);
        balancerechargeBtnRechargehistory = (Button) view.findViewById(R.id.balancerecharge_btn_rechargehistory);
        balancerechargeLvData = (ListView) view.findViewById(R.id.balancerecharge_lv_data);
        return view;
    }

    protected void onDie() {
        mFragmentActivity = null;
    }

    protected void main() {
        myAdapter = new MyAdapter();
        balancerechargeLvData.setAdapter(myAdapter);
    }

    protected void initData() {
        EventBus.getDefault().register(this);
        carDao = CarDao.getInstance(mFragmentActivity);
        mCarBeanMap = new HashMap<>();
        Map<String, Integer> values = new HashMap<>();
        values.put("CarId", mCarBeanArr[mReqGetCarAccountBalanceIndex].getCarId());
        NetUtil.getNetUtil().addRequest("GetCarAccountBalance", values, CarBean.class);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getCarAccountBalance(CarBean carBean) {
        CarBean localCarBean = mCarBeanArr[mReqGetCarAccountBalanceIndex];
        localCarBean.setBalance(carBean.getBalance());
        mCarBeanMap.put(localCarBean.getCarId(), localCarBean);
        mReqGetCarAccountBalanceIndex++;
        if (mReqGetCarAccountBalanceIndex < mCarBeanArr.length) {
            Map<String, Integer> values = new HashMap<>();
            values.put("CarId", mCarBeanArr[mReqGetCarAccountBalanceIndex].getCarId());
            NetUtil.getNetUtil().addRequest("GetCarAccountBalance", values, CarBean.class);
        } else {
            if (myAdapter != null) {
                myAdapter.notifyDataSetChanged();
            }
        }
    }

    protected void onDims() {
        carDao = null;
        mCarBeanMap = null;
        myAdapter = null;
        mRechargeCarIdIndex = 0;
        mReqGetCarAccountBalanceIndex = 0;
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.balancerecharge_btn_rechargehistory:
                FragmentTransaction fragmentTransaction = mFragmentActivity.getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_fl_content, new RechargeHistoryFragment());
                fragmentTransaction.commit();
                break;
            case R.id.balancerechargelv_tv_option:
                showDialog();
                CarBean carBean = (CarBean) view.getTag();
                rechargedialogTvCarIds.setText(carBean.getCarId() + "");
                break;
            case R.id.rechargedialog_btn_recharge:
                recharge();
                break;
            case R.id.rechargedialog_btn_cancel:
                alertDialog.dismiss();
                alertDialog = null;
                break;
            case R.id.balancerecharge_btn_mulitpeRechage:
                showDialog();
                int count = balancerechargeLvData.getCount();
                for (int index = 0; index < count; index++) {
                    View childAtView = balancerechargeLvData.getChildAt(index);
                    ViewHolder viewHolder = (ViewHolder) childAtView.getTag();
                    if (!viewHolder.getBalancerechargelvTvCheck().isChecked()) {
                        continue;
                    }
                    if (TextUtils.isEmpty(rechargedialogTvCarIds.getText().toString())) {
                        rechargedialogTvCarIds.setText(viewHolder.getBalancerechargelvTvCarId().getText().toString());
                    } else {
                        rechargedialogTvCarIds.setText(rechargedialogTvCarIds.getText() + "," + viewHolder.getBalancerechargelvTvCarId().getText().toString());
                    }
                }
                if (TextUtils.isEmpty(rechargedialogTvCarIds.getText())) {
                    Toast.makeText(mFragmentActivity, "请至少选择一个", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                    alertDialog = null;
                }
                break;
        }
    }

    private void recharge() {
        String strMoney = rechargedialogEtMoney.getText().toString();
        if (!strMoney.matches("^\\d{1,3}$")) {
            Toast.makeText(mFragmentActivity, "金额非法", Toast.LENGTH_SHORT).show();
            return;
        }
        int money = Integer.parseInt(strMoney);
        if (money < 1 || money > 999) {
            Toast.makeText(mFragmentActivity, "金额非法", Toast.LENGTH_SHORT).show();
            return;
        }
        String strCarId = rechargedialogTvCarIds.getText().toString();
        rechargeCarIdArr = strCarId.split(",");
        Map<String, Integer> values = new HashMap<>();
        values.put("CarId", Integer.parseInt(rechargeCarIdArr[mRechargeCarIdIndex]));
        values.put("Money", money);
        rechargeMoney = money;
        NetUtil.getNetUtil().addRequest("SetCarAccountRecharge", values, Map.class);
        alertDialog.dismiss();
        alertDialog = null;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setCarAccountRecharge(Map<String, String> map) {
        CarBean carBean = new CarBean();
        carBean.setCarId(Integer.parseInt(rechargeCarIdArr[mRechargeCarIdIndex]));
        carBean.setMoney(rechargeMoney);
        carBean.setRechargeTime(new Date());
        carBean.setRechargeUName("admin");
        int insert = carDao.insert(carBean);
        Log.d(mTag, "CarDao insert: " + insert);
        mRechargeCarIdIndex++;
        if (mRechargeCarIdIndex < rechargeCarIdArr.length) {
            Map<String, Integer> values = new HashMap<>();
            values.put("CarId", Integer.parseInt(rechargeCarIdArr[mRechargeCarIdIndex]));
            values.put("Money", Integer.parseInt(rechargedialogEtMoney.getText().toString()));
            NetUtil.getNetUtil().addRequest("SetCarAccountRecharge", values, Map.class);
        } else {
            Toast.makeText(mFragmentActivity, "ok", Toast.LENGTH_SHORT).show();
            onDims();
            initData();
            main();
        }
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mFragmentActivity);
        View view = LayoutInflater.from(mFragmentActivity).inflate(R.layout.dialog_recharge, null);
        rechargedialogTvCarIds = (TextView) view.findViewById(R.id.rechargedialog_tv_CarIds);
        rechargedialogEtMoney = (EditText) view.findViewById(R.id.rechargedialog_et_money);
        Button rechargedialogBtnRecharge = (Button) view.findViewById(R.id.rechargedialog_btn_recharge);
        Button rechargedialogBtnCancel = (Button) view.findViewById(R.id.rechargedialog_btn_cancel);
        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.show();
        rechargedialogBtnRecharge.setOnClickListener(this);
        rechargedialogBtnCancel.setOnClickListener(this);
    }


    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mCarBeanMap.size();
        }

        @Override
        public CarBean getItem(int i) {
            return mCarBeanMap.get(mCarBeanArr[i].getCarId());
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder = null;
            if (view == null) {
                view = LayoutInflater.from(mFragmentActivity).inflate(R.layout.item_balancerechargelvdata,
                        balancerechargeLvData, false);
                viewHolder = ViewHolder.get(view);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            CarBean carBean = getItem(i);
            viewHolder.getBalancerechargelvTvCarId().setText(carBean.getCarId() + "");
            viewHolder.getBalancerechargelvTvBalance().setText(carBean.getBalance() + "元");
            Button balancerechargelvTvOption = viewHolder.getBalancerechargelvTvOption();
            balancerechargelvTvOption.setTag(carBean);
            balancerechargelvTvOption.setOnClickListener(BalanceRechargeFragment.this);
            return view;
        }
    }

    private static class ViewHolder {

        TextView balancerechargelvTvCarId;
        TextView balancerechargelvTvBalance;
        CheckBox balancerechargelvTvCheck;
        Button balancerechargelvTvOption;


        public ViewHolder(View view) {
            balancerechargelvTvCarId = (TextView) view.findViewById(R.id.balancerechargelv_tv_CarId);
            balancerechargelvTvBalance = (TextView) view.findViewById(R.id.balancerechargelv_tv_Balance);
            balancerechargelvTvCheck = (CheckBox) view.findViewById(R.id.balancerechargelv_tv_check);
            balancerechargelvTvOption = (Button) view.findViewById(R.id.balancerechargelv_tv_option);
        }

        public static ViewHolder get(View view) {
            Object tag = view.getTag();
            if (tag == null) {
                tag = new ViewHolder(view);
                view.setTag(tag);
            }
            return (ViewHolder) tag;
        }

        public TextView getBalancerechargelvTvCarId() {
            return balancerechargelvTvCarId;
        }

        public TextView getBalancerechargelvTvBalance() {
            return balancerechargelvTvBalance;
        }

        public CheckBox getBalancerechargelvTvCheck() {
            return balancerechargelvTvCheck;
        }

        public Button getBalancerechargelvTvOption() {
            return balancerechargelvTvOption;
        }
    }

}

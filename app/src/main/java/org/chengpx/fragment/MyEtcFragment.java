package org.chengpx.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

public class MyEtcFragment extends Fragment implements View.OnClickListener {

    private TextView etc_tv_carBalance;
    private Spinner etc_spinner_carIds;
    private EditText etc_et_CarRechargeMoney;
    private Button etc_btn_search;
    private Button etc_btn_rechage;
    private Integer[] mCarIdArr = {
            1, 2, 3
    };
    private FragmentActivity fragmentActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentActivity = getActivity();
        View view = initView(inflater, container, savedInstanceState);
        initListener();
        return view;
    }

    private void initListener() {
        etc_btn_search.setOnClickListener(this);
        etc_btn_rechage.setOnClickListener(this);
    }

    private View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_etc, container, false);
        etc_tv_carBalance = (TextView) view.findViewById(R.id.etc_tv_carBalance);
        etc_spinner_carIds = (Spinner) view.findViewById(R.id.etc_spinner_carIds);
        etc_et_CarRechargeMoney = (EditText) view.findViewById(R.id.etc_et_CarRechargeMoney);
        etc_btn_search = (Button) view.findViewById(R.id.etc_btn_search);
        etc_btn_rechage = (Button) view.findViewById(R.id.etc_btn_rechage);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        initData();
        main();
    }

    private void main() {
        etc_spinner_carIds.setAdapter(new ArrayAdapter<Integer>(
                fragmentActivity, android.R.layout.simple_spinner_dropdown_item, mCarIdArr
        ));
    }

    private void initData() {
        Map<String, Integer> values = new HashMap<>();
        values.put("CarId", 1);
        NetUtil.getNetUtil().addRequest("GetCarAccountBalance.do",
                values, CarBean.class);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getCarAccountBalance(CarBean carBean) {
        etc_tv_carBalance.setText(carBean.getBalance() + " 元");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setCarAccountRecharge(Map map) {
        Toast.makeText(fragmentActivity, "充值成功", Toast.LENGTH_SHORT).show();
        Map<String, Integer> values = new HashMap<>();
        values.put("CarId", mCarIdArr[etc_spinner_carIds.getSelectedItemPosition()]);
        NetUtil.getNetUtil().addRequest("GetCarAccountBalance.do",
                values, CarBean.class);
        CarBean carBean = new CarBean();
        carBean.setRechargeUName("admin");
        carBean.setCarId(mCarIdArr[etc_spinner_carIds.getSelectedItemPosition()]);
        carBean.setMoney(Integer.parseInt(etc_et_CarRechargeMoney.getText().toString()));
        carBean.setRechargeTime(new Date());
        CarDao.getInstance(fragmentActivity).insert(carBean);
    }


    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
        onDims();
    }

    private void onDims() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.etc_btn_search:
                Map<String, Integer> values = new HashMap<>();
                values.put("CarId", mCarIdArr[etc_spinner_carIds.getSelectedItemPosition()]);
                NetUtil.getNetUtil().addRequest("GetCarAccountBalance.do",
                        values, CarBean.class);
                break;
            case R.id.etc_btn_rechage:
                recharge();
                break;
        }
    }

    private void recharge() {
        String rechargeMoney = etc_et_CarRechargeMoney.getText().toString();
        if (TextUtils.isEmpty(rechargeMoney)) {
            Toast.makeText(fragmentActivity, "不可以为空", Toast.LENGTH_SHORT).show();
            return;
        }
        int money = 0;
        if (!rechargeMoney.matches("^\\d{1,3}$")) {
            Toast.makeText(fragmentActivity, "金额非法", Toast.LENGTH_SHORT).show();
            return;
        }
        money = Integer.parseInt(rechargeMoney);
        if (money < 1 || money > 999) {
            Toast.makeText(fragmentActivity, "范围为 1- 999", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, Integer> values = new HashMap<>();
        values.put("CarId", mCarIdArr[etc_spinner_carIds.getSelectedItemPosition()]);// Money
        values.put("Money", money);
        NetUtil.getNetUtil().addRequest("SetCarAccountRecharge.do", values, Map.class);
    }

}

package org.chengpx.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import org.chengpx.base.BaseFragment;
import org.chengpx.R;
import org.chengpx.domain.EnvBean;
import org.chengpx.service.EnvCheckService;
import org.chengpx.util.SpUtils;

import java.util.Arrays;

/**
 * 第七题阈值设置功能
 * create at 2018/4/29 10:28 by chengpx
 */
public class ThresholdSettingFragment extends BaseFragment implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {

    private String mTag = getClass().getName();

    private Switch thresholdsetting_switch_isautocallpolice;
    private EditText thresholdsetting_et_temperature;
    private EditText thresholdsetting_et_humidity;
    private EditText thresholdsetting_et_LightIntensity;
    private EditText thresholdsetting_et_co2;
    private EditText thresholdsetting_et_pm25;
    private EditText thresholdsetting_et_RoadStatus;
    private Button thresholdsetting_btn_save;
    private EditText[] mEditTextArr;
    private EnvBean[] mEnvBeanArr = {
            new EnvBean("temperature", "温度", new int[]{0, 37}), new EnvBean("humidity", "湿度", new int[]{20, 80}),
            new EnvBean("LightIntensity", "光照强度", new int[]{1, 5000}), new EnvBean("co2", "co2", new int[]{350, 7000}),
            new EnvBean("pm2.5", "pm2.5", new int[]{0, 300}), new EnvBean("RoadStatus", "道路状态", new int[]{1, 5})
    };

    @Override
    protected void initListener() {
        thresholdsetting_btn_save.setOnClickListener(this);
        thresholdsetting_switch_isautocallpolice.setOnCheckedChangeListener(this);
    }

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_thresholdsetting, container, false);
        thresholdsetting_switch_isautocallpolice = (Switch) view.findViewById(R.id.thresholdsetting_switch_isautocallpolice);
        thresholdsetting_et_temperature = (EditText) view.findViewById(R.id.thresholdsetting_et_temperature);
        thresholdsetting_et_humidity = (EditText) view.findViewById(R.id.thresholdsetting_et_humidity);
        thresholdsetting_et_LightIntensity = (EditText) view.findViewById(R.id.thresholdsetting_et_LightIntensity);
        thresholdsetting_et_co2 = (EditText) view.findViewById(R.id.thresholdsetting_et_co2);
        thresholdsetting_et_pm25 = (EditText) view.findViewById(R.id.thresholdsetting_et_pm25);
        thresholdsetting_et_RoadStatus = (EditText) view.findViewById(R.id.thresholdsetting_et_RoadStatus);
        thresholdsetting_btn_save = (Button) view.findViewById(R.id.thresholdsetting_btn_save);
        mEditTextArr = new EditText[]{
                thresholdsetting_et_temperature, thresholdsetting_et_humidity,
                thresholdsetting_et_LightIntensity, thresholdsetting_et_co2,
                thresholdsetting_et_pm25, thresholdsetting_et_RoadStatus,
        };
        return view;
    }

    @Override
    protected void onDie() {

    }

    @Override
    protected void main() {
        boolean isautocallpolice = EnvCheckService.isRunning(mFragmentActivity);
        SpUtils spUtils = SpUtils.getInstance(mFragmentActivity);
        thresholdsetting_switch_isautocallpolice.setChecked(isautocallpolice);
        for (int index = 0; index < mEditTextArr.length; index++) {
            int val = spUtils.getInt(mEnvBeanArr[index].getSenseName(), -1);
            EditText editText = mEditTextArr[index];
            editText.setEnabled(!isautocallpolice);
            if (val != -1) {
                editText.setText(val + "");
            }
        }
    }

    @Override
    protected void initData() {
    }

    @Override
    protected void onDims() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.thresholdsetting_btn_save:
                save();
                break;
        }
    }

    private void save() {
        if (thresholdsetting_switch_isautocallpolice.isChecked()) {
            Toast.makeText(mFragmentActivity, "请先关闭自动报警", Toast.LENGTH_SHORT).show();
            return;
        }
        int count = 0;
        for (int index = 0; index < mEditTextArr.length; index++) {
            String str = mEditTextArr[index].getText().toString();
            if (TextUtils.isEmpty(str)) {
                SpUtils.getInstance(mFragmentActivity).remove(mEnvBeanArr[index].getSenseName());
                continue;
            }
            if (!str.matches("^[1-9]\\d*$")) {
                Toast.makeText(mFragmentActivity, mEnvBeanArr[index].getSenseDesc() + " 阈值非法", Toast.LENGTH_SHORT).show();
                return;
            }
            int val = Integer.parseInt(str);
            int[] range = mEnvBeanArr[index].getRange();
            if (range[0] > val || val > range[1]) {
                Toast.makeText(mFragmentActivity, mEnvBeanArr[index].getSenseDesc() + " 阈值非法, 阈值范围: " + Arrays.toString(range), Toast.LENGTH_SHORT).show();
                return;
            }
            SpUtils.getInstance(mFragmentActivity).putInt(mEnvBeanArr[index].getSenseName(), val);
            count++;
        }
        if (count != 0) {
            Toast.makeText(mFragmentActivity, "ok", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(mTag, "buttonView = " + buttonView + ", isChecked = " + isChecked);
        if (!buttonView.isShown()) {
            return;
        }
        for (EditText editText : mEditTextArr) {
            editText.setEnabled(!isChecked);
        }
        if (isChecked) {
            EnvCheckService.start(mFragmentActivity);
        } else {
            EnvCheckService.stop(mFragmentActivity);
        }
    }

}

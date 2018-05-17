package org.chengpx;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.chengpx.base.BaseActivity;
import org.chengpx.util.SpUtils;

import java.util.Locale;

/**
 * create at 2018/5/12 20:44 by chengpx
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private Button loginBtnLanguage;
    private Button loginBtnLogin;
    private AlertDialog mAlertDialog;
    private ImageView loginIvDialogBack;
    private Button loginBtnSave;
    private RadioGroup loginRadiogroupDialoglanguage;
    private RadioButton loginRadiobtnDialogchina;
    private RadioButton loginRadiobtnDialogenglish;
    private Locale[] mLocaleArr = {
            Locale.CHINA, Locale.US
    };
    private View mDialogRootView;

    @Override
    protected void initListener() {
        loginBtnLanguage.setOnClickListener(this);
        loginBtnLogin.setOnClickListener(this);
    }

    @Override
    protected View initView(Bundle savedInstanceState) {
        View view = LayoutInflater.from(this).inflate(R.layout.activity_login, null);
        loginBtnLanguage = (Button) view.findViewById(R.id.login_btn_language);
        loginBtnLogin = (Button) view.findViewById(R.id.login_btn_login);
        return view;
    }

    @Override
    protected void onDie() {

    }

    @Override
    protected void main() {

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
            case R.id.login_btn_language:
                showSettingLanguageDialog();
                break;
            case R.id.login_btn_login:
                Intent it = new Intent(this, MainActivity.class);
                startActivity(it);
                break;
            case R.id.login_iv_dialog_back:
                mAlertDialog.dismiss();
                mAlertDialog = null;
                break;
            case R.id.login_btn_save:
                applyLanguageSetting();
                break;
        }

    }

    private void applyLanguageSetting() {
        mAlertDialog.dismiss();
        mAlertDialog = null;
        Resources resources = getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        // 应用用户选择语言
        int radiobtnCheckIndex = loginRadiogroupDialoglanguage.indexOfChild(mDialogRootView.findViewById(loginRadiogroupDialoglanguage.getCheckedRadioButtonId()));
        config.locale = mLocaleArr[radiobtnCheckIndex];
        resources.updateConfiguration(config, dm);
        SpUtils.getInstance(this).putInt("localarrindex", radiobtnCheckIndex);
        Intent it = new Intent(this, LoginActivity.class); //MainActivity 是你想要重启的 activity
        it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(it);
    }

    private void showSettingLanguageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        mDialogRootView = LayoutInflater.from(this).inflate(R.layout.dialog_languagesetting, null);
        builder.setView(mDialogRootView);
        mAlertDialog = builder.create();
        mAlertDialog.show();
        loginIvDialogBack = (ImageView) mDialogRootView.findViewById(R.id.login_iv_dialog_back);
        loginBtnSave = (Button) mDialogRootView.findViewById(R.id.login_btn_save);
        loginRadiogroupDialoglanguage = (RadioGroup) mDialogRootView.findViewById(R.id.login_radiogroup_dialoglanguage);
        loginRadiobtnDialogchina = (RadioButton) mDialogRootView.findViewById(R.id.login_radiobtn_dialogchina);
        loginRadiobtnDialogenglish = (RadioButton) mDialogRootView.findViewById(R.id.login_radiobtn_dialogenglish);
        loginIvDialogBack.setOnClickListener(this);
        loginBtnSave.setOnClickListener(this);
        loginRadiogroupDialoglanguage.check(loginRadiogroupDialoglanguage.getChildAt(SpUtils.getInstance(this).getInt("localarrindex", 0)).getId());
    }

}

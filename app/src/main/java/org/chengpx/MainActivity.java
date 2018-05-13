package org.chengpx;

import android.os.Bundle;
import android.view.WindowManager;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

import org.chengpx.service.CarSpeedListenerService;

public class MainActivity extends SlidingFragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onDie();
    }

    private void onDie() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        CarSpeedListenerService.start(this);
        initData();
        main();
    }

    private void main() {
    }

    private void initData() {
    }

    @Override
    protected void onPause() {
        super.onPause();
        onDims();
        CarSpeedListenerService.stop(this);
    }

    private void onDims() {
    }

    private void initView() {
        SlidingMenu slidingMenu = getSlidingMenu();
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        assert windowManager != null;
        slidingMenu.setBehindOffset((int) ((1.0f * 3 / 4) * windowManager.getDefaultDisplay().getWidth()));
        setBehindContentView(R.layout.slidingmenu_main);
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_fl_menu, new MainMenuFragment());
        fragmentTransaction.commit();
    }

    private void initListener() {
    }

}

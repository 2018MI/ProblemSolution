package org.chengpx;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.chengpx.fragment.BalanceRechargeFragment;
import org.chengpx.fragment.CarSpeedListenerFragment;
import org.chengpx.fragment.EnvFragment;
import org.chengpx.fragment.MyEtcFragment;
import org.chengpx.fragment.RoadStatusQueryFragment;
import org.chengpx.fragment.SubwayFragment;
import org.chengpx.fragment.ThresholdSettingFragment;
import org.chengpx.fragment.TrafficLightManagerFragment;
import org.chengpx.fragment.TravelManagementFragment;
import org.chengpx.fragment.WeatherFragment;
import org.chengpx.fragment.mytraffic.MyTrafficFragment;

/**
 * create at 2018/5/9 14:45 by chengpx
 */
public class MainMenuFragment extends Fragment implements AdapterView.OnItemClickListener {

    private FragmentActivity mFragmentActivity;
    private ListView mainmenuLvItems;
    private String[] mItemStrArr = {
            "道路环境", "etc账户充值", "红绿灯管理", "etc管理", "小车单双号管制", "阈值设置", "路况查询",
            "天气信息", "地铁路线查看", "小车车速监控", "我的交通"
    };
    private Fragment[] mFragmentArr = {
            new EnvFragment(), new BalanceRechargeFragment(), new TrafficLightManagerFragment(),
            new MyEtcFragment(), new TravelManagementFragment(), new ThresholdSettingFragment(),
            new RoadStatusQueryFragment(), new WeatherFragment(), new SubwayFragment(), new CarSpeedListenerFragment(),
            new MyTrafficFragment()
    };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentActivity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = initView(inflater, container, savedInstanceState);
        initListener();
        return view;
    }

    private View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mainmenu, container, false);
        mainmenuLvItems = (ListView) view.findViewById(R.id.mainmenu_lv_items);
        return view;
    }

    private void initListener() {
        mainmenuLvItems.setOnItemClickListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        onDie();
    }

    private void onDie() {
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
        main();
    }

    private void main() {
        mainmenuLvItems.setAdapter(new ArrayAdapter<String>(mFragmentActivity, android.R.layout.simple_expandable_list_item_1,
                mItemStrArr));
    }

    private void initData() {
    }

    @Override
    public void onPause() {
        super.onPause();
        onDims();
    }

    private void onDims() {
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        assert getFragmentManager() != null;
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_fl_content, mFragmentArr[position]);
        fragmentTransaction.commit();
    }

}

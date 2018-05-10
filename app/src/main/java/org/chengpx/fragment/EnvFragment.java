package org.chengpx.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import org.chengpx.BaseFragment;
import org.chengpx.R;
import org.chengpx.domain.EnvBean;
import org.chengpx.domain.RoadBean;
import org.chengpx.util.db.EnvDao;
import org.chengpx.util.net.NetUtil;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class EnvFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    private String mTag = "org.chengpx.fragment.EnvFragment";

    private GridView env_gridview_content;
    private Timer timer;
    private Map<String, EnvBean> mDataMap;
    private MyAdapter myAdapter;
    private EnvDao envDao;
    private int mInsertCount;
    private EnvBean[] mEnvBeanArr = {
            new EnvBean("temperature", "温度", new int[]{0, 37}),
            new EnvBean("humidity", "湿度", new int[]{20, 80}),
            new EnvBean("LightIntensity", "光照", new int[]{1, 5000}),
            new EnvBean("co2", "CO2", new int[]{300, 7000}),
            new EnvBean("pm2.5", "PM2.5", new int[]{0, 300}),
            new EnvBean("RoadStatus", "道路状态", new int[]{1, 5})
    };

    protected void initListener() {
        env_gridview_content.setOnItemClickListener(this);
    }

    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_env, container, false);
        env_gridview_content = (GridView) view.findViewById(R.id.env_gridview_content);
        return view;
    }

    protected void onDie() {
    }

    protected void main() {
        myAdapter = new MyAdapter();
        env_gridview_content.setAdapter(myAdapter);
    }

    protected void initData() {
        EventBus.getDefault().register(this);
        mDataMap = new HashMap<>();
        envDao = EnvDao.getInstance(mFragmentActivity);
        timer = new Timer();
        timer.schedule(new MyTimerTask(), 0, 3000);
    }

    protected void onDims() {
        timer.cancel();
        timer = null;
        mInsertCount = 0;
        myAdapter = null;
        mDataMap = null;
        envDao = null;
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getAllSense(Map<String, Double> map) {
        List<EnvBean> envBeanList = new ArrayList<>();
        for (int index = 0; index < mEnvBeanArr.length - 1; index++) {
            EnvBean envBean = mEnvBeanArr[index];
            Double aDouble = map.get(envBean.getSenseName());
            envBean.setVal(aDouble.intValue());
            mDataMap.put(envBean.getSenseName(), envBean);
            envBeanList.add(envBean);
        }
        if (myAdapter != null) {
            myAdapter.notifyDataSetChanged();
        }
        for (EnvBean envBean : envBeanList) {
            envBean.setDateTime(new Date());
            int insert = envDao.insert(envBean);
            Log.d(mTag, "EnvDao insert: " + insert);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getRoadStatus(RoadBean roadBean) {
        EnvBean envBean = mEnvBeanArr[mEnvBeanArr.length - 1];
        envBean.setDateTime(new Date());
        envBean.setVal(roadBean.getStatus());
        mDataMap.put("RoadStatus", envBean);
        if (myAdapter != null) {
            myAdapter.notifyDataSetChanged();
        }
        int insert = envDao.insert(envBean);
        Log.d(mTag, "EnvDao insert: " + insert);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
    }

    private class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            if (mInsertCount > 1000 * 60 / 3000) {
                int delete = envDao.delete();
                Log.d(mTag, "EnvDao delete: " + delete);
                mInsertCount = 0;
            }
            mInsertCount++;
            Map<String, Integer> values = new HashMap<>();
            values.put("RoadId", 1);
            NetUtil.getNetUtil().addRequest("GetAllSense.do", null, Map.class);
            NetUtil.getNetUtil().addRequest("GetRoadStatus.do", values, RoadBean.class);
        }

    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mDataMap == null ? 0 : mDataMap.size();
        }

        @Override
        public EnvBean getItem(int i) {
            return mDataMap.get(mEnvBeanArr[i].getSenseName());
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder = null;
            if (view == null) {
                view = LayoutInflater.from(mFragmentActivity).inflate(R.layout.item_env_gridview_content,
                        env_gridview_content, false);
                viewHolder = ViewHolder.get(view);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            EnvBean item = getItem(i);
            int val = item.getVal();
            viewHolder.getEnv_tv_senseData().setText(val + "");
            viewHolder.getEnv_tv_senseDesc().setText(item.getSenseDesc());
            int[] range = item.getRange();
            float f = (range[0] + range[1]) * 1.0f * 0.6f;
            if (val > f) {// 超标
                viewHolder.getEnv_rl_itemcontent().setBackgroundResource(R.drawable.shape_rectangle_red);
            } else {
                viewHolder.getEnv_rl_itemcontent().setBackgroundResource(R.drawable.shape_rectangle_green);
            }
            return view;
        }
    }

    private static class ViewHolder {

        private final TextView env_tv_senseDesc;
        private final TextView env_tv_senseData;
        private final View env_rl_itemcontent;

        private ViewHolder(View view) {
            env_tv_senseDesc = (TextView) view.findViewById(R.id.env_tv_senseDesc);
            env_tv_senseData = (TextView) view.findViewById(R.id.env_tv_senseData);
            env_rl_itemcontent = view.findViewById(R.id.env_rl_itemcontent);
        }

        public static ViewHolder get(View view) {
            Object tag = view.getTag();
            if (tag == null) {
                tag = new ViewHolder(view);
                view.setTag(tag);
            }
            return (ViewHolder) tag;
        }

        public TextView getEnv_tv_senseDesc() {
            return env_tv_senseDesc;
        }

        public TextView getEnv_tv_senseData() {
            return env_tv_senseData;
        }

        public View getEnv_rl_itemcontent() {
            return env_rl_itemcontent;
        }

    }

}

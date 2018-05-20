package org.chengpx.fragment.mymsg;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.chengpx.R;
import org.chengpx.base.BaseFragment;
import org.chengpx.domain.EnvBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * create at 2018/5/19 18:51 by chengpx
 */
public class MsgQueryFragment extends BaseFragment implements AdapterView.OnItemSelectedListener {

    private Spinner msgquerySpinnerWarntype;
    private ListView msgqueryLvData;
    private String[] mWarnTypeArr = {
            "全部", "[温度]警告", "[湿度]警告", "[光照强度]警告", "[co2]警告", "[pm2.5]警告", "[道路状态]警告"
    };
    private String mTag = getClass().getName();

    private static Map<String, List<EnvBean>> sListMap;
    private static MyAdapter sAdapter;

    @Override
    protected void initListener() {
        msgquerySpinnerWarntype.setOnItemSelectedListener(this);
    }

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_msgquery, container, false);
        msgquerySpinnerWarntype = (Spinner) view.findViewById(R.id.msgquery_spinner_warntype);
        msgqueryLvData = (ListView) view.findViewById(R.id.msgquery_lv_data);
        return view;
    }

    @Override
    protected void onDie() {

    }

    @Override
    protected void main() {
        msgquerySpinnerWarntype.setAdapter(new ArrayAdapter<String>(
                mFragmentActivity, android.R.layout.simple_spinner_item, mWarnTypeArr
        ));
        sAdapter = new MyAdapter();
        msgqueryLvData.setAdapter(sAdapter);
    }

    @Override
    protected void initData() {
        if (sListMap == null) {
            sListMap = new HashMap<>();
        }
    }

    @Override
    protected void onDims() {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (sAdapter != null) {
            sAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public static class MsgQueryFragmentBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getBundleExtra("warndata");
            int yuzhi = bundle.getInt("yuzhi");
            if (yuzhi == -1) {
                return;
            }
            String warnType = bundle.getString("warnType");
            if (sListMap == null) {
                sListMap = new HashMap<>();
            }
            List<EnvBean> envBeanList = sListMap.get(warnType);
            if (envBeanList == null) {
                envBeanList = new ArrayList<>();
            }
            envBeanList.add(new EnvBean(bundle.getInt("val"), warnType, yuzhi));
            sListMap.put(warnType, envBeanList);
            if (sAdapter != null) {
                sAdapter.notifyDataSetChanged();
            }
        }
    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            if (sListMap == null) {
                return 0;
            }
            int selectedItemPosition = msgquerySpinnerWarntype.getSelectedItemPosition();
            if (selectedItemPosition < 0) {
                selectedItemPosition = 0;
            } else if (selectedItemPosition > mWarnTypeArr.length - 1) {
                selectedItemPosition = mWarnTypeArr.length - 1;
            }
            if (selectedItemPosition != 0) {
                List<EnvBean> envBeanList = sListMap.get(mWarnTypeArr[selectedItemPosition]);
                return envBeanList == null ? 0 : envBeanList.size();
            }
            int count = 0;
            for (int index = 1; index < mWarnTypeArr.length; index++) {
                List<EnvBean> envBeanList = sListMap.get(mWarnTypeArr[index]);
                if (envBeanList == null) {
                    continue;
                }
                count += envBeanList.size();
            }
            return count;
        }

        @Override
        public EnvBean getItem(int position) {
            int selectedItemPosition = msgquerySpinnerWarntype.getSelectedItemPosition();
            if (selectedItemPosition < 0) {
                selectedItemPosition = 0;
            } else if (selectedItemPosition > mWarnTypeArr.length - 1) {
                selectedItemPosition = mWarnTypeArr.length - 1;
            }
            if (selectedItemPosition != 0) {
                List<EnvBean> envBeanList = sListMap.get(mWarnTypeArr[selectedItemPosition]);
                return envBeanList.get(position);
            }
            int count = 0;
            for (int index = 1; index < mWarnTypeArr.length; index++) {
                List<EnvBean> envBeanList = sListMap.get(mWarnTypeArr[index]);
                if (envBeanList == null) {
                    continue;
                }
                count += envBeanList.size();
                if (count > position) {
                    int pos = position - (count - envBeanList.size());
                    return envBeanList.get(pos);
                }
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mFragmentActivity).inflate(R.layout.item_msgquery_lv_data,
                        msgqueryLvData, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            EnvBean envBean = getItem(position);
            viewHolder.msgquery_tv_lvnum.setText(position + "");
            viewHolder.msgquery_tv_lvwarntype.setText(envBean.getWarnType());
            viewHolder.msgquery_tv_lvval.setText(envBean.getVal() + "");
            viewHolder.msgquery_tv_lvyuzhi.setText(envBean.getYuzhi() + "");
            return convertView;
        }


    }

    private static class ViewHolder {
        public View rootView;
        public TextView msgquery_tv_lvnum;
        public TextView msgquery_tv_lvwarntype;
        public TextView msgquery_tv_lvyuzhi;
        public TextView msgquery_tv_lvval;

        public ViewHolder(View rootView) {
            this.rootView = rootView;
            this.msgquery_tv_lvnum = (TextView) rootView.findViewById(R.id.msgquery_tv_lvnum);
            this.msgquery_tv_lvwarntype = (TextView) rootView.findViewById(R.id.msgquery_tv_lvwarntype);
            this.msgquery_tv_lvyuzhi = (TextView) rootView.findViewById(R.id.msgquery_tv_lvyuzhi);
            this.msgquery_tv_lvval = (TextView) rootView.findViewById(R.id.msgquery_tv_lvval);
        }

    }

}

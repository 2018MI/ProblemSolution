package org.chengpx.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.chengpx.base.BaseFragment;
import org.chengpx.R;
import org.chengpx.domain.CarBean;
import org.chengpx.domain.RuleBean;
import org.chengpx.util.db.CarDao;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * create at 2018/5/9 17:27 by chengpx
 */
public class RechargeHistoryFragment extends BaseFragment implements View.OnClickListener, Comparator<CarBean> {

    private Spinner rechargehistorySpinnerRulues;
    private Button rechargeBtnSearch;
    private ListView rechargehistoryLvData;
    private RuleBean[] mRuleBeanArr = {
            new RuleBean("时间降序", "rechargeTime", RuleBean.DESC),
            new RuleBean("时间升序", "rechargeTime", RuleBean.ASC)
    };
    private String[] mRuleArr = {
            "时间降序", "时间升序"
    };
    private List<CarBean> mCarBeanList;
    private MyAdapter mMyAdapter;
    private DateFormat mDateFormat;

    protected void initListener() {
        rechargeBtnSearch.setOnClickListener(this);
    }

    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rechargehistory, container, false);
        rechargehistorySpinnerRulues = (Spinner) view.findViewById(R.id.rechargehistory_spinner_rulues);
        rechargeBtnSearch = (Button) view.findViewById(R.id.recharge_btn_search);
        rechargehistoryLvData = (ListView) view.findViewById(R.id.rechargehistory_lv_data);
        return view;
    }

    protected void onDie() {

    }

    protected void main() {
        rechargehistorySpinnerRulues.setAdapter(new ArrayAdapter<String>(mFragmentActivity,
                android.R.layout.simple_spinner_item, mRuleArr));
        mMyAdapter = new MyAdapter();
        rechargehistoryLvData.setAdapter(mMyAdapter);
    }

    protected void initData() {
        mDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        mCarBeanList = CarDao.getInstance(mFragmentActivity).select();
        if (mCarBeanList == null || mCarBeanList.size() == 0) {
            Toast.makeText(mFragmentActivity, "暂无历史记录", Toast.LENGTH_SHORT).show();
        } else {
            rechargehistorySpinnerRulues.setSelection(0);
            Collections.sort(mCarBeanList, this);
        }
        if (mMyAdapter != null) {
            mMyAdapter.notifyDataSetChanged();
        }
    }

    protected void onDims() {
        mDateFormat = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.recharge_btn_search:
                if (mCarBeanList != null && mCarBeanList.size() > 0) {
                    Collections.sort(mCarBeanList, this);
                    mMyAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(mFragmentActivity, "暂无历史记录", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public int compare(CarBean carBean1, CarBean carBean2) {
        RuleBean ruleBean = mRuleBeanArr[rechargehistorySpinnerRulues.getSelectedItemPosition()];
        Integer priority = ruleBean.getPriority();
        try {
            Field field = CarBean.class.getDeclaredField(ruleBean.getColumnField());
            field.setAccessible(true);
            Comparable val1 = (Comparable) field.get(carBean1);
            Comparable val2 = (Comparable) field.get(carBean2);
            if (RuleBean.ASC.equals(priority)) {
                return val1.compareTo(val2);
            } else if (RuleBean.DESC.equals(priority)) {
                return val2.compareTo(val1);
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mCarBeanList == null ? 0 : mCarBeanList.size();
        }

        @Override
        public CarBean getItem(int i) {
            return mCarBeanList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder = null;
            if (view == null) {
                view = LayoutInflater.from(mFragmentActivity).inflate(R.layout.item_rechartgehistory, rechargehistoryLvData, false);
                viewHolder = new ViewHolder(view);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            CarBean carBean = getItem(i);
            viewHolder.rechargehistory_tv_lvitemrownum.setText(i + "");
            viewHolder.rechargehistory_tv_lvitemcarid.setText(carBean.getCarId() + "");
            viewHolder.rechargehistory_tv_lvitemrechargemoney.setText(carBean.getMoney() + "");
            viewHolder.rechargehistory_tv_lvitemuname.setText(carBean.getRechargeUName());
            viewHolder.rechargehistory_tv_lvitemrechargedate.setText(mDateFormat.format(carBean.getRechargeTime()));
            return view;
        }

    }

    private static class ViewHolder {
        public View rootView;
        public TextView rechargehistory_tv_lvitemrownum;
        public TextView rechargehistory_tv_lvitemcarid;
        public TextView rechargehistory_tv_lvitemrechargemoney;
        public TextView rechargehistory_tv_lvitemuname;
        public TextView rechargehistory_tv_lvitemrechargedate;

        public ViewHolder(View rootView) {
            this.rootView = rootView;
            this.rechargehistory_tv_lvitemrownum = (TextView) rootView.findViewById(R.id.rechargehistory_tv_lvitemrownum);
            this.rechargehistory_tv_lvitemcarid = (TextView) rootView.findViewById(R.id.rechargehistory_tv_lvitemcarid);
            this.rechargehistory_tv_lvitemrechargemoney = (TextView) rootView.findViewById(R.id.rechargehistory_tv_lvitemrechargemoney);
            this.rechargehistory_tv_lvitemuname = (TextView) rootView.findViewById(R.id.rechargehistory_tv_lvitemuname);
            this.rechargehistory_tv_lvitemrechargedate = (TextView) rootView.findViewById(R.id.rechargehistory_tv_lvitemrechargedate);
        }

    }

}

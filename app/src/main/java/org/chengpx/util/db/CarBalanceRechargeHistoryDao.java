package org.chengpx.util.db;


import android.content.Context;

import com.j256.ormlite.dao.Dao;

import org.chengpx.domain.CarBalanceRechargeHistoryBean;

import java.sql.SQLException;
import java.util.List;

public class CarBalanceRechargeHistoryDao {
    private static CarBalanceRechargeHistoryDao sCarBalanceRechargeHistoryDao;
    private Dao<CarBalanceRechargeHistoryBean, ?> mDao;

    private CarBalanceRechargeHistoryDao(Context context) {
        try {
            mDao = DbHelper.getInstance(context).getDao(CarBalanceRechargeHistoryBean.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static CarBalanceRechargeHistoryDao getInstance(Context context) {
        if (sCarBalanceRechargeHistoryDao == null) {
            synchronized (CarBalanceRechargeHistoryDao.class) {
                if (sCarBalanceRechargeHistoryDao == null) {
                    sCarBalanceRechargeHistoryDao = new CarBalanceRechargeHistoryDao(context);
                }
            }
        }
        return sCarBalanceRechargeHistoryDao;
    }

    public int insert(CarBalanceRechargeHistoryBean carBalanceRechargeHistoryBean) {
        try {
            return mDao.create(carBalanceRechargeHistoryBean);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<CarBalanceRechargeHistoryBean> select() {
        try {
            return mDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}

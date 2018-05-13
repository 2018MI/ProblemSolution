package org.chengpx.util.db;

import android.content.Context;

import com.j256.ormlite.dao.Dao;

import org.chengpx.domain.CaroverspeedhistoryBean;

import java.sql.SQLException;
import java.util.List;

public class CaroverspeedhistoryBeanDao {
    private static CaroverspeedhistoryBeanDao sCaroverspeedhistoryBeanDao;
    private Dao<CaroverspeedhistoryBean, ?> mDao;

    private CaroverspeedhistoryBeanDao(Context context) {
        try {
            mDao = DbHelper.getInstance(context).getDao(CaroverspeedhistoryBean.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static CaroverspeedhistoryBeanDao getInstance(Context context) {
        if (sCaroverspeedhistoryBeanDao == null) {
            synchronized (CaroverspeedhistoryBeanDao.class) {
                if (sCaroverspeedhistoryBeanDao == null) {
                    sCaroverspeedhistoryBeanDao = new CaroverspeedhistoryBeanDao(context);
                }
            }
        }
        return sCaroverspeedhistoryBeanDao;
    }

    public List<CaroverspeedhistoryBean> select() {
        try {
            return mDao.queryBuilder().orderBy("overSpeedDateTime", false).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int insert(CaroverspeedhistoryBean caroverspeedhistoryBean) {
        try {
            return mDao.create(caroverspeedhistoryBean);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}

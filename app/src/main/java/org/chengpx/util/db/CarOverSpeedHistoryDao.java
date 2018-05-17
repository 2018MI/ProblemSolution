package org.chengpx.util.db;


import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import org.chengpx.domain.CarOverSpeedHistoryBean;

import java.sql.SQLException;
import java.util.List;

public class CarOverSpeedHistoryDao {
    private static CarOverSpeedHistoryDao sCarOverSpeedHistoryDao;
    private Dao<CarOverSpeedHistoryBean, ?> mDao;

    private CarOverSpeedHistoryDao(Context context) {
        try {
            mDao = DbHelper.getInstance(context).getDao(CarOverSpeedHistoryBean.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static CarOverSpeedHistoryDao getInstance(Context context) {
        if (sCarOverSpeedHistoryDao == null) {
            synchronized (CarOverSpeedHistoryDao.class) {
                if (sCarOverSpeedHistoryDao == null) {
                    sCarOverSpeedHistoryDao = new CarOverSpeedHistoryDao(context);
                }
            }
        }
        return sCarOverSpeedHistoryDao;
    }

    public int insert(CarOverSpeedHistoryBean carOverSpeedHistoryBean) {
        try {
            return mDao.create(carOverSpeedHistoryBean);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<CarOverSpeedHistoryBean> select() {
        try {
            return mDao.queryBuilder().orderBy("overSpeedDateTime", false).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public long selectCouuntByCarId(int CarId) {
        try {
            QueryBuilder<CarOverSpeedHistoryBean, ?> queryBuilder = mDao.queryBuilder();
            Where<CarOverSpeedHistoryBean, ?> where = queryBuilder.where().eq("CarId", CarId);
            return where.countOf();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}

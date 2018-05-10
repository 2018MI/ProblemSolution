package org.chengpx.util.db;

import android.content.Context;

import com.j256.ormlite.dao.Dao;

import org.chengpx.domain.CarBean;

import java.sql.SQLException;
import java.util.List;

public class CarDao {

    private static CarDao sCarDao;
    private Dao<CarBean, ?> dao;

    private CarDao(Context context) {
        DbHelper dbHelper = DbHelper.getInstance(context);
        try {
            dao = dbHelper.getDao(CarBean.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static CarDao getInstance(Context context) {
        if (sCarDao == null) {
            synchronized (CarDao.class) {
                if (sCarDao == null) {
                    sCarDao = new CarDao(context);
                }
            }
        }
        return sCarDao;
    }

    public int insert(CarBean carBean) {
        try {
            return dao.create(carBean);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<CarBean> select() {
        try {
            return dao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}

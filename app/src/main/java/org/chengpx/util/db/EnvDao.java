package org.chengpx.util.db;

import android.content.Context;

import com.j256.ormlite.dao.Dao;

import org.chengpx.domain.EnvBean;

import java.sql.SQLException;

public class EnvDao {

    private static EnvDao sEnvDao;
    private Dao<EnvBean, ?> mEnvBeanDao;

    public static EnvDao getInstance(Context context) {
        if (sEnvDao == null) {
            synchronized (EnvDao.class) {
                if (sEnvDao == null) {
                    sEnvDao = new EnvDao(context);
                }
            }
        }
        return sEnvDao;
    }

    private EnvDao(Context context) {
        try {
            mEnvBeanDao = DbHelper.getInstance(context).getDao(EnvBean.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int insert(EnvBean envBean) {
        try {
            return mEnvBeanDao.create(envBean);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int delete() {
        try {
            return mEnvBeanDao.deleteBuilder().delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

}

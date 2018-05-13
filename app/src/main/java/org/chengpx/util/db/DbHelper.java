package org.chengpx.util.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import org.chengpx.domain.CarBean;
import org.chengpx.domain.CaroverspeedhistoryBean;
import org.chengpx.domain.EnvBean;

import java.sql.SQLException;

public class DbHelper extends OrmLiteSqliteOpenHelper {

    private static  DbHelper sDbHelper;

    public static DbHelper getInstance(Context context) {
        if (sDbHelper == null) {
            synchronized (DbHelper.class) {
                if (sDbHelper == null) {
                    sDbHelper = new DbHelper(context, "app.db", null, 1);
                }
            }
        }
        return sDbHelper;
    }

    private DbHelper(Context context, String databaseName, SQLiteDatabase.CursorFactory factory, int databaseVersion) {
        super(context, databaseName, factory, databaseVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, EnvBean.class);
            TableUtils.createTable(connectionSource, CarBean.class);
            TableUtils.createTable(connectionSource, CaroverspeedhistoryBean.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i1) {
    }

}

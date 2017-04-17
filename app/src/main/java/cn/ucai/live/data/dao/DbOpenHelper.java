package cn.ucai.live.data.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import cn.ucai.live.LiveApplication;
import cn.ucai.live.utils.L;

/**
 * Created by w on 2017/4/14.
 */

public class DbOpenHelper extends SQLiteOpenHelper {
    private static final String TAG = "DbOpenHelper";
    private static final int DATABASE_VERSION = 1;
    private static DbOpenHelper instance;

    private static final String GIFT_TABLE_CREATE = "CREATE TABLE "
            + GiftDao.GIFT_TABLE_NAME + " ("
            + GiftDao.GIFT_COLUMN_NAME + " TEXT, "
            + GiftDao.GIFT_COLUMN_URL + " TEXT, "
            + GiftDao.GIFT_COLUMN_PRICE + " INTEGER, "
            + GiftDao.GIFT_COLUMN_ID + " INTEGER PRIMARY KEY);";

    private DbOpenHelper(Context context) {
        super(context, getUserDatabaseName(), null, DATABASE_VERSION);
    }

    public static DbOpenHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DbOpenHelper(context.getApplicationContext());
        }
        return instance;
    }

    private static String getUserDatabaseName() {
        return LiveApplication.getInstance().getPackageName() + "_demo.db";
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(GIFT_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void closeDB() {
        if (instance != null) {
            try {
                SQLiteDatabase db = instance.getWritableDatabase();
                db.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            instance = null;
        }
    }
}

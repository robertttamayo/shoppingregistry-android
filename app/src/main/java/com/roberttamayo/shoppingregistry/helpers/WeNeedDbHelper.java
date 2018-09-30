package com.roberttamayo.shoppingregistry.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.roberttamayo.shoppingregistry.helpers.WeNeedDbSchema.UserTable;

public class WeNeedDbHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "weneed.db";

    public WeNeedDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + UserTable.NAME + "(" +
            " _id integer primary key autoincrement, " +
            UserTable.Cols.USERNAME + ", " +
            UserTable.Cols.NICKNAME + ", " +
            UserTable.Cols.PASSWORD + ", " +
            UserTable.Cols.ACCOUNT_ID + ", " +
            UserTable.Cols.FIREBASE_TOKEN + ", " +
            UserTable.Cols.IS_SELF +
            ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}

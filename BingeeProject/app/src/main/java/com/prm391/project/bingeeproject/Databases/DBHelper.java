package com.prm391.project.bingeeproject.Databases;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.prm391.project.bingeeproject.Model.Order;

import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static final String CREATE_TABLE_ORDERDETAIL =
            "CREATE TABLE OrderDetail ("
                    + "id integer not null primary key autoincrement,"
                    + "productId text,"
                    + "productName text,"
                    + "quantity integer,"
                    + "price real"
                    + ")";

    public DBHelper(Activity activity, String dbName, int version) {
        super(activity, dbName, null, version);
    }

    private final String DROP_TABLE_ORDERDETAIL = "DROP TABLE OrderDetail";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_ORDERDETAIL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL(DROP_TABLE_ORDERDETAIL);
            onCreate(db);
        }
    }

}

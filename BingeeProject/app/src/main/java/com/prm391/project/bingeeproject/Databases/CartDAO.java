package com.prm391.project.bingeeproject.Databases;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;

import com.github.yaa110.db.RestorableSQLiteDatabase;
import com.google.android.material.snackbar.Snackbar;
import com.prm391.project.bingeeproject.Fragment.ProductDetailFragment;
import com.prm391.project.bingeeproject.Model.Order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CartDAO {
    private static final String DELETE_ORDERDETAIL = "DELETE FROM OrderDetail";
    private static final String INSERT_ORDERDETAIL =
            "INSERT INTO OrderDetail VALUES (?,?,?,?)";
    private static final String SELECT_TABLE_ORDERDETAIL =
            "SELECT productId, productName, quantity, price  FROM OrderDetail";
    private Context context;
    private DBHelper dbHelper;

    public CartDAO(Context context) {
        this.context = context;
        dbHelper = new DBHelper(context, "orderdetail.db", 1);
    }

    public List<Order> getCarts() {
        List<Order> orders = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(SELECT_TABLE_ORDERDETAIL, null);
        while (cursor.moveToNext()) {
            Order order = new Order();
            order.setmProductId(cursor.getString(cursor.getColumnIndex("productId")));
            order.setmProductName(cursor.getString(cursor.getColumnIndex("productName")));
            order.setmQuantity(cursor.getInt(cursor.getColumnIndex("quantity")));
            order.setmPrice(cursor.getDouble(cursor.getColumnIndex("price")));
            orders.add(order);
        }
        return orders;
    }

    public void addToCart(Order order, View view) {
        List<Order> carts = getCarts();
        for (Order item : carts) {
            if (item.getmProductId().equals(order.getmProductId())) {
                updateItemFromCart(order, view);
                return;
            }
        }
        insertNewItemToCart(order, view);
    }

    private void updateItemFromCart(Order order, View view) {
        HashMap<String, String> orderDetailID = new HashMap<>();
        orderDetailID.put("OrderDetail", "id");
        final RestorableSQLiteDatabase db = RestorableSQLiteDatabase.getInstance(dbHelper, orderDetailID);
        ContentValues values = new ContentValues();
        values.put("productId", order.getmProductId());
        values.put("productName", order.getmProductName());
        values.put("quantity", order.getmQuantity());
        values.put("price", order.getmPrice());
        long check = db.update("OrderDetail", values, "productId" + " = ?", new String[]{String.valueOf(order.getmProductId())}, "UpdateFromCartTag");
        if (check > 0) {
            Snackbar.make(view, "Update from cart is successful", Snackbar.LENGTH_SHORT)
                    .setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // restore update by tag
                                    db.restore("UpdateFromCartTag");
                                }
                            }
                    )
                    .show();
        }

    }

    private void insertNewItemToCart(Order order, View view) {
        //SQLiteDatabase db = dbHelper.getReadableDatabase();
// db.execSQL(INSERT_ORDERDETAIL,new String[]{order.getmProductId(),order.getmProductName(),order.getmQuantity(),order.getmPrice(),order.getmDiscount()});
        HashMap<String, String> orderDetailID = new HashMap<>();
        orderDetailID.put("OrderDetail", "id");
        final RestorableSQLiteDatabase db = RestorableSQLiteDatabase.getInstance(dbHelper, orderDetailID);
        ContentValues values = new ContentValues();
        values.put("productId", order.getmProductId());
        values.put("productName", order.getmProductName());
        values.put("quantity", order.getmQuantity());
        values.put("price", order.getmPrice());
        long check = db.insert("OrderDetail", null, values, "AddToCartTag");
        if (check != -1) {
            Snackbar.make(view, "Add to cart is successful", Snackbar.LENGTH_SHORT)
                    .setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // restore insert by tag
                                    db.restore("AddToCartTag");
                                }
                            }
                    )
                    .show();
        }
    }

    public void cleanCart() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL(DELETE_ORDERDETAIL);
    }


}

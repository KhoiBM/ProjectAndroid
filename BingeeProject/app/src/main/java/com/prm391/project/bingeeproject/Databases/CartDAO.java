package com.prm391.project.bingeeproject.Databases;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.github.yaa110.db.RestorableSQLiteDatabase;
import com.google.android.material.snackbar.Snackbar;
import com.prm391.project.bingeeproject.Common.NavigationHost;
import com.prm391.project.bingeeproject.Fragment.CartFragment;
import com.prm391.project.bingeeproject.Fragment.CheckoutFragment;
import com.prm391.project.bingeeproject.Model.Order;
import com.prm391.project.bingeeproject.Utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CartDAO<C extends Fragment>{
    private static final String DELETE_ORDERDETAIL = "DELETE FROM OrderDetail";
    private static final String INSERT_ORDERDETAIL =
            "INSERT INTO OrderDetail VALUES (?,?,?,?)";
    private static final String SELECT_TABLE_ORDERDETAIL =
            "SELECT productId, productName, quantity, price  FROM OrderDetail";
    private Activity activity;
    private @NonNull DBHelper dbHelper;

    public CartDAO(Activity activity) {
        this.activity = activity;
        dbHelper = new DBHelper(activity, "orderdetail.db", 1);
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

    public void addToCart(Order order, View view, FragmentActivity activity, String type) {
        List<Order> carts = getCarts();
        for (Order item : carts) {
            if (item.getmProductId().equals(order.getmProductId())) {
                updateItemFromCart(item, order, view, activity, type);
                return;
            }
        }
        insertNewItemToCart(order, view, activity, type);
    }

    private void updateItemFromCart(Order item, Order order, View view, FragmentActivity activity, String type) {
        HashMap<String, String> orderDetailID = new HashMap<>();
        orderDetailID.put("OrderDetail", "id");
        final RestorableSQLiteDatabase db = RestorableSQLiteDatabase.getInstance(dbHelper, orderDetailID);

        ContentValues values = new ContentValues();
        values.put("productId", order.getmProductId());
        values.put("productName", order.getmProductName());
        values.put("quantity", item.getmQuantity() + order.getmQuantity());
        values.put("price", order.getmPrice());

        long check = db.update("OrderDetail", values, "productId" + " = ?", new String[]{String.valueOf(order.getmProductId())}, "UpdateFromCartTag");
        if (check > 0) {
            createSnackbarShowSuccessAndUndo(view, activity, db, "Add to cart is successful", "UpdateFromCartTag", type);
        }
    }

    private void insertNewItemToCart(Order order, View view, FragmentActivity activity, String type) {
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
            createSnackbarShowSuccessAndUndo(view, activity, db, "Add to cart is successful", "AddToCartTag", type);
        }
    }

    private void createSnackbarShowSuccessAndUndo(final View view, FragmentActivity activity, final RestorableSQLiteDatabase db, String message, final String undoTag, final String type) {
//        Snackbar.Callback callBackForBuyNow = snackbarCallBackOnDismissedForBuyNow(activity);
        Snackbar.Callback callBackForReLoadCart = snackbarCallBackOnDismissedForReLoadCart(view);
        showSnackbarWithUndoAction(view,activity,message,db,undoTag,type,callBackForReLoadCart);
    }

    private void showSnackbarWithUndoAction(final View view, FragmentActivity activity, String message, final RestorableSQLiteDatabase db, final String undoTag, final String type,Snackbar.Callback callBackForReLoadCart){
       if(type.equals("buyNow")){
           Bundle bundle = new Bundle();
           ((NavigationHost) activity).navigateTo(new CartFragment(), bundle,true);
       }else {
           Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
                   .setAction("Undo", new View.OnClickListener() {
                               @Override
                               public void onClick(View v) {
                                   // restore by tag
                                   db.restore(undoTag);
                                   if (type.equals("deleteItemCart") | type.equals("cleanCart")) {
                                       ((CartFragment) findFragmentByView(view)).reLoadViewCart();
                                   }
                               }
                           }
                   )
                   .addCallback(type.equals("cleanCart") || type.equals("deleteItemCart") ? callBackForReLoadCart : null)
                   .show();
       }
    }

    private Snackbar.Callback snackbarCallBackOnDismissedForReLoadCart(final View view) {
        return new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                super.onDismissed(snackbar, event);
                ((CartFragment)findFragmentByView(view)).loadViewCart();
            }

        };
    }

    public void cleanCart(View snackbarLayout, String type) {
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//        db.execSQL(DELETE_ORDERDETAIL);
        HashMap<String, String> orderDetailID = new HashMap<>();
        orderDetailID.put("OrderDetail", "id");
        final RestorableSQLiteDatabase db = RestorableSQLiteDatabase.getInstance(dbHelper, orderDetailID);

        int check = db.delete("OrderDetail", null, null,
                "CleanCartTag"
        );

        if (check > 0) {
            ((CartFragment)findFragmentByView(snackbarLayout)).reLoadViewCart();
            createSnackbarShowSuccessAndUndo(snackbarLayout, null, db, "You cleaned all cart", "CleanCartTag", type);
        }
    }

    public void delete(final String productId, final String productName, final View snackbarLayout, View viewDelete, final String type) {
        Animation anim = AnimationUtils.loadAnimation(viewDelete.getContext(),
                android.R.anim.slide_out_right);
        anim.setDuration(500);
        viewDelete.startAnimation(anim);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                HashMap<String, String> orderDetailID = new HashMap<>();
                orderDetailID.put("OrderDetail", "id");
                final RestorableSQLiteDatabase db = RestorableSQLiteDatabase.getInstance(dbHelper, orderDetailID);

                int check = db.delete("OrderDetail",
                        "productId" + " = ?",
                        new String[]{String.valueOf(productId)},
                        "DeleteTag"
                );

                if (check > 0) {
                    ((CartFragment)findFragmentByView(snackbarLayout)).reLoadViewCart();
                    createSnackbarShowSuccessAndUndo(snackbarLayout, null, db, "You removed " + productName + " from cart", "DeleteTag", type);
                }
            }

        }, anim.getDuration());

    }

    public void updateQuantity(View snackbarLayout, View viewDelete, String productId, String productName, int quantity,C c) {

        HashMap<String, String> orderDetailID = new HashMap<>();
        orderDetailID.put("OrderDetail", "id");
        final RestorableSQLiteDatabase db = RestorableSQLiteDatabase.getInstance(dbHelper, orderDetailID);

        ContentValues values = new ContentValues();
        values.put("quantity", String.valueOf(quantity));

        if (quantity == 0) {
            delete(productId,productName, snackbarLayout, viewDelete,"deleteItemCart");
        } else {
            long check = db.update("OrderDetail", values, "productId" + " = ?", new String[]{String.valueOf(productId)}, "UpdateQuantityFromCartTag");
            if (check > 0) {
                if(CartFragment.class.isInstance(c)){
////                    ((CartFragment) c).reloadWhenChangeQuantityDifferentZero();

//                    Utils.showSnackbarWithNoAction(snackbarLayout,"isCartFragment");
                    ((CartFragment)findFragmentByView(snackbarLayout)).reloadWhenChangeQuantityDifferentZero();
                }else if(c instanceof CheckoutFragment){
//                    Utils.showSnackbarWithNoAction(snackbarLayout,"isCheckoutFragment");
                    ((CheckoutFragment)findFragmentByView(snackbarLayout)).reloadWhenChangeQuantityDifferentZero();
                }
            }
        }
    }
    private Fragment findFragmentByView(View view){
        FragmentManager manager = ((AppCompatActivity) activity).getSupportFragmentManager();
        return FragmentManager.findFragment(view);
    }
    public boolean cleanCartWhenFinishCheckout(View snackbarLayout, String s) {
        HashMap<String, String> orderDetailID = new HashMap<>();
        orderDetailID.put("OrderDetail", "id");
        final RestorableSQLiteDatabase db = RestorableSQLiteDatabase.getInstance(dbHelper, orderDetailID);

        int check = db.delete("OrderDetail", null, null,
                ""
        );

        if (check > 0) {
            return  true;
        }
        return  false;
    }
}

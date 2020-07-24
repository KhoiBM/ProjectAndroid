package com.prm391.project.bingeeproject.Utils;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.prm391.project.bingeeproject.Common.NavigationHost;
import com.prm391.project.bingeeproject.Fragment.CartFragment;
import com.prm391.project.bingeeproject.Fragment.HomeFragment;
import com.prm391.project.bingeeproject.Fragment.MyAccountFragment;
import com.prm391.project.bingeeproject.Fragment.ProductListFragment;
import com.prm391.project.bingeeproject.Fragment.ProfileFragment;
import com.prm391.project.bingeeproject.R;

public class HandleNavMenu {


    public static void setupButton(View.OnClickListener onClickListener, View view, Button button, int id) {
        button = view.findViewById(id);
        button.setOnClickListener(onClickListener);
    }


    public static void commonNavigationMenuForCategory(View v, FragmentActivity activity) {
        int id = v.getId();
        switch (id) {
            case R.id.nav_btn_go_home:
                Bundle nav_btn_go_home_bundle = new Bundle();
                ((NavigationHost) activity).navigateTo(new HomeFragment(), nav_btn_go_home_bundle, true);
                break;
            case R.id.nav_btn_ingredients:
                Bundle nav_btn_ingredients_bundle = new Bundle();
                nav_btn_ingredients_bundle.putString("categoryID", "1");
                ((NavigationHost) activity).navigateTo(new ProductListFragment(), nav_btn_ingredients_bundle, true);
                break;
            case R.id.nav_btn_furniture:
                Bundle nav_btn_furniture_bundle = new Bundle();
                nav_btn_furniture_bundle.putString("categoryID", "2");
                ((NavigationHost) activity).navigateTo(new ProductListFragment(), nav_btn_furniture_bundle, true);
                break;
            case R.id.nav_btn_new:
                break;
            case R.id.nav_btn_sale:
                break;
            case R.id.nav_btn_cart:
                Bundle nav_btn_cart_bundle = new Bundle();
                ((NavigationHost) activity).navigateTo(new CartFragment(), nav_btn_cart_bundle, true);
                break;
            case R.id.nav_btn_about:
                break;
            case R.id.nav_btn_contact:
                break;
            case R.id.nav_btn_my_account:
                Bundle nav_btn_my_account_bundle = new Bundle();
                ((NavigationHost) activity).navigateTo(new MyAccountFragment(), nav_btn_my_account_bundle, true);
                break;
        }

    }
}


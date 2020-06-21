package com.prm391.project.bingeeproject.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.prm391.project.bingeeproject.Common.LoginActivity;

public class Ultils {

    //check Internet
//    pubic static boolean isConnected(LoginActivity loginActivity) {
//        ConnectivityManager connectivityManager = (ConnectivityManager) loginActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
//
//        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(connectivityManager.TYPE_MOBILE);
//        if ((wifiConn != null && wifiConn.isConnected()) || (mobileConn != null && mobileConn.isConnected())) {
//            return true;
//        } else {
//            return false;
//        }
//    }
    public static boolean isConnected(LoginActivity loginActivity) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) loginActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isConnected = false;
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            isConnected = (activeNetwork != null) && (activeNetwork.isConnectedOrConnecting());
        }

        return isConnected;
    }
}

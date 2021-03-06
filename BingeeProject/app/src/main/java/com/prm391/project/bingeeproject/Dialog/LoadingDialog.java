package com.prm391.project.bingeeproject.Dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.prm391.project.bingeeproject.R;

public class LoadingDialog {
    private Activity activity;
    private AlertDialog dialog;

    public LoadingDialog(Activity myActivity) {
        activity = myActivity;
    }

    public void startLoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.custom_progress_dialog, null))
                .setCancelable(true);
        dialog = builder.create();
        dialog.show();
//        dialog.getWindow().setLayout(400,300);
    }

    public void dismissDialog() {
        dialog.dismiss();
    }
}

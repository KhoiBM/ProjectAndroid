package com.prm391.project.bingeeproject.Common;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.transition.Fade;
import android.transition.Transition;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.internal.service.Common;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;
import com.prm391.project.bingeeproject.Dialog.LoadingDialog;
import com.prm391.project.bingeeproject.Model.User;
import com.prm391.project.bingeeproject.R;
import com.prm391.project.bingeeproject.Utils.Ultils;
import com.prm391.project.bingeeproject.databinding.ActivityLoginBinding;
import com.prm391.project.bingeeproject.databinding.ActivityOnBoardingBinding;

import java.util.Queue;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    //    CountryCodePicker countryCodePicker;
    TextInputLayout phoneNumber, password;
    RelativeLayout progressbar;
    MaterialButton signup;
    private LoadingDialog loadingDialog;
    private ActivityLoginBinding mBinding;
    private FirebaseDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Transition fade = new Fade();
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        getWindow().setExitTransition(fade);
        getWindow().setEnterTransition(fade);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        phoneNumber = mBinding.textInputPhone;
        password = mBinding.textInputPassword;
        progressbar = mBinding.progressbar;
//        countryCodePicker = mBinding.countryCodePicker;
        signup = mBinding.buttonSignup;

        loadingDialog = new LoadingDialog(LoginActivity.this);

        mDatabase = FirebaseDatabase.getInstance();
    }

    public void signup(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);

        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair<View, String>(signup, "transition_signup");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, pairs);
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }

    }

    public void go(final View view) {

        if (!Ultils.isConnected(this)) {
            Ultils.showCustomDialog(this);
        }

        //validate data
        if (!validateField()) {
            return;
        }
//        progressbar.setVisibility(View.VISIBLE);
        loadingDialog.startLoadingDialog();
        //get date
        final String _phoneNumber = phoneNumber.getEditText().getText().toString().trim();
        final String _password = password.getEditText().getText().toString().trim();
        Log.i(TAG, "_phoneNumber: " + _phoneNumber);

//        if (_phoneNumber.charAt(0) == '0') {
//            _phoneNumber = _phoneNumber.substring(1);
//        }
//        final String _completePhoneNumber = "+" + countryCodePicker.getFullNumber() +"-"+ _phoneNumber;
//        Log.i(TAG, "_completePhoneNumber: " + _completePhoneNumber);

        //Database
        Query checkUser = mDatabase.getReference("Users").orderByChild("mPhone").equalTo(_phoneNumber);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    phoneNumber.setError(null);
                    phoneNumber.setErrorEnabled(false);
                    String systemPassWord = dataSnapshot.child(_phoneNumber).child("mPassword").getValue(String.class);
                    if (systemPassWord.equals(_password)) {
                        password.setError(null);
                        password.setErrorEnabled(false);
                        loadingDialog.dismissDialog();
                        String _fullName = dataSnapshot.child(_phoneNumber).child("mFullName").getValue(String.class);
                        String _phoneNo = dataSnapshot.child(_phoneNumber).child("mPhone").getValue(String.class);
                        String _pass = dataSnapshot.child(_phoneNumber).child("mPassword").getValue(String.class);

                        User user = new User();
                        user.setmFullName(_fullName);
                        user.setmPhone(_phoneNo);
                        user.setmPassword(_password);

                        Snackbar.Callback callback = null;
                        callback = snackbarCallBackOnDismissed();
                        Snackbar.make(view, "Login successful", Snackbar.LENGTH_SHORT)
                                .addCallback(callback).show();

                    } else {
                        password.setError("Password is invalid");
                        password.setErrorEnabled(true);
//                        progressbar.setVisibility(View.GONE);
                        loadingDialog.dismissDialog();
                        Snackbar.make(view, "Password does not match!", Snackbar.LENGTH_SHORT)
                                .setAction("No action", null)
                                .show();

                    }
                } else {
                    phoneNumber.setError("Phone isn't exist");
                    phoneNumber.setErrorEnabled(true);
//                    progressbar.setVisibility(View.GONE);
                    loadingDialog.dismissDialog();
                    Snackbar.make(view, "Data does not exist!", Snackbar.LENGTH_SHORT)
                            .setAction("No action", null)
                            .show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
//                progressbar.setVisibility(View.GONE);
                loadingDialog.dismissDialog();
                Snackbar.make(view, databaseError.getMessage(), Snackbar.LENGTH_SHORT)
                        .setAction("No action", null)
                        .show();
            }
        });

    }

    public Snackbar.Callback snackbarCallBackOnDismissed() {
        return new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                super.onDismissed(snackbar, event);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

        };
    }


    public boolean validateField() {
        String _phoneNumber = phoneNumber.getEditText().getText().toString().trim();
        String _password = password.getEditText().getText().toString().trim();
        if (TextUtils.isEmpty(_phoneNumber)) {
            phoneNumber.setError("Phone number can not be empty");
            phoneNumber.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(_password)) {
            password.setError("Password can not be empty");
            password.requestFocus();
            return false;
        } else {
            phoneNumber.setErrorEnabled(false);
            password.setErrorEnabled(false);
            return true;
        }
    }
}
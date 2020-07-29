package com.prm391.project.bingeeproject.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.Fade;
import android.transition.Transition;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.prm391.project.bingeeproject.Dialog.LoadingDialog;
import com.prm391.project.bingeeproject.R;
import com.prm391.project.bingeeproject.Utils.Utils;
import com.prm391.project.bingeeproject.databinding.ActivityLoginBinding;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private TextInputLayout phoneNumber, password;
    private RelativeLayout progressbar;
    private MaterialButton signup;
    private LoadingDialog loadingDialog;
    private ActivityLoginBinding mBinding;
    private FirebaseDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Transition fade = new Fade();
//        fade.excludeTarget(android.R.id.statusBarBackground, true);
//        fade.excludeTarget(android.R.id.navigationBarBackground, true);
//        getWindow().setExitTransition(fade);
//        getWindow().setEnterTransition(fade);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        phoneNumber = mBinding.textInputPhone;
        password = mBinding.textInputPassword;
        progressbar = mBinding.progressbar;
        signup = mBinding.buttonSignup;

        loadingDialog = new LoadingDialog(LoginActivity.this);
//        loadingDialog.startLoadingDialog();
        mDatabase = FirebaseDatabase.getInstance();
    }

    public void signup(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);


    }

    public void go(final View view) {

        if (!Utils.isConnected(this)) {
            Utils.showCustomDialog(this);
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

                        Snackbar.Callback callback = snackbarCallBackOnDismissed(_phoneNo, _pass);
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

    private Snackbar.Callback snackbarCallBackOnDismissed(final String _phoneNo, final String _pass) {
        return new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                super.onDismissed(snackbar, event);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("phoneUser", _phoneNo);
                intent.putExtra("password", _pass);
                intent.putExtra("isAuth",true);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                finish();

            }

        };
    }


    private boolean checkValidPhone(String _phone) {
        Pattern pattern = Pattern.compile("^(\\(\\d{3}\\)|\\d{3})-?\\d{3}-?\\d{4}$");
        Matcher matcher = pattern.matcher(_phone);
        if (TextUtils.isEmpty(_phone)) {
            phoneNumber.setError("Phone can't be empty");
            phoneNumber.requestFocus();
            return false;
        }
        if (!matcher.matches()) {
            phoneNumber.setError("Invalid phone pattern");
            phoneNumber.requestFocus();
            return false;
        }
        return true;
    }

    private boolean checkValidPassword(String _password) {
        Pattern pattern = Pattern.compile("^[A-Za-z0-9]{8,20}$");
        Matcher matcher = pattern.matcher(_password);
        if (TextUtils.isEmpty(_password)) {
            password.setError("Password can't be empty");
            password.requestFocus();
            return false;
        }
        if (!matcher.matches()) {
            password.setError("Invalid password pattern");
            password.requestFocus();
            return false;
        }
        return true;
    }

    private boolean validateField() {
        String _phoneNumber = phoneNumber.getEditText().getText().toString().trim();
        String _password = password.getEditText().getText().toString().trim();
        phoneNumber.setErrorEnabled(false);
        password.setErrorEnabled(false);

        if (checkValidPhone(_phoneNumber) & checkValidPassword(_password)) {
            return true;
        }
        return false;

    }


}
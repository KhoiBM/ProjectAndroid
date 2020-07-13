package com.prm391.project.bingeeproject.Common;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.Fade;
import android.transition.Transition;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
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
import com.prm391.project.bingeeproject.databinding.ActivityRetailerSignUpBinding;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = SignUpActivity.class.getSimpleName();
    //    CountryCodePicker countryCodePicker;
    TextInputLayout phoneNumber, fullName, password;
    ImageView goBack;
    //    RelativeLayout progressbar;
    LinearLayout layoutSignUp;
    private LoadingDialog loadingDialog;
    private ActivityRetailerSignUpBinding mSignUpBinding;
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

//        setContentView(R.layout.activity_retailer_sign_up);
        mSignUpBinding = ActivityRetailerSignUpBinding.inflate(getLayoutInflater());
        setContentView(mSignUpBinding.getRoot());
        phoneNumber = mSignUpBinding.textInputPhone;
        fullName = mSignUpBinding.textInputFullname;
        password = mSignUpBinding.textInputPassword;
//        progressbar = mSignUpBinding.progressbar;
//        countryCodePicker = mSignUpBinding.countryCodePicker;
        goBack = mSignUpBinding.signupBackButton;
        layoutSignUp= mSignUpBinding.layoutSignUp;

        loadingDialog = new LoadingDialog(SignUpActivity.this);

        mDatabase = FirebaseDatabase.getInstance();
    }

    public void signup(final View view) {
        if (!Ultils.isConnected(this)) {
            Ultils.showCustomDialog(this);
        }
        if (!validateField()) {
            return;
        }
//        progressbar.setVisibility(View.VISIBLE);
        loadingDialog.startLoadingDialog();

        final String _phoneNumber = phoneNumber.getEditText().getText().toString().trim();
        final String _fullName = fullName.getEditText().getText().toString().trim();
        final String _password = password.getEditText().getText().toString().trim();
        Log.i(TAG, "_fullName: " + _fullName);
        Log.i(TAG, "_password: " + _password);
//        if (_phoneNumber.charAt(0) == '0') {
//            _phoneNumber = _phoneNumber.substring(1);
//        }
//        final String _completePhoneNumber = "+" + countryCodePicker.getFullNumber()  +"-"+  _phoneNumber;
//        Log.i(TAG, "_completePhoneNumber: " + _completePhoneNumber);
//        Database
        final DatabaseReference table_user = mDatabase.getReference("Users");
        Query checkUser = table_user.orderByChild("mPhone").equalTo(_phoneNumber);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    phoneNumber.setError("Phone is exist!");
                    phoneNumber.setErrorEnabled(true);
                    //                    progressbar.setVisibility(View.GONE);
                    loadingDialog.dismissDialog();
                    Snackbar.make(view, "User already exists!", Snackbar.LENGTH_SHORT)
                            .setAction("No action", null)
                            .show();
                } else {
//                    progressbar.setVisibility(View.GONE);
                    loadingDialog.dismissDialog();

                    User user = new User();
                    user.setmFullName(_fullName);
                    user.setmPhone(_phoneNumber);
                    user.setmPassword(_password);
                    table_user.child(_phoneNumber).setValue(user);

                    Snackbar.Callback callback = null;
                    callback = snackbarCallBackOnDismissed();
                    Snackbar.make(view, "SignUp successful", Snackbar.LENGTH_LONG)
                            .addCallback(callback).show();
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
                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

        };
    }

    public void goBack(View view) {
        Intent intent = new Intent(this, LoginActivity.class);

        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair<View, String>(layoutSignUp, "transition_signup");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this,pairs);
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }
        finish();

    }

    public boolean validateField() {
        String _phoneNumber = phoneNumber.getEditText().getText().toString().trim();
        String _fullName = fullName.getEditText().getText().toString().trim();
        String _password = password.getEditText().getText().toString().trim();
        if (TextUtils.isEmpty(_phoneNumber)) {
            phoneNumber.setError("Phone number can not be empty");
            phoneNumber.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(_fullName)) {
            fullName.setError("FullName can not be empty");
            fullName.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(_password)) {
            password.setError("Password can not be empty");
            password.requestFocus();
            return false;
        } else {
            fullName.setErrorEnabled(false);
            phoneNumber.setErrorEnabled(false);
            password.setErrorEnabled(false);
            return true;
        }
    }
}
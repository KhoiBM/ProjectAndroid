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
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.prm391.project.bingeeproject.Dialog.LoadingDialog;
import com.prm391.project.bingeeproject.Model.User;
import com.prm391.project.bingeeproject.R;
import com.prm391.project.bingeeproject.Utils.Utils;
import com.prm391.project.bingeeproject.databinding.ActivityRetailerSignUpBinding;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = SignUpActivity.class.getSimpleName();
    TextInputLayout phoneNumber, fullName, password;
    ImageView goBack;
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

        mSignUpBinding = ActivityRetailerSignUpBinding.inflate(getLayoutInflater());
        setContentView(mSignUpBinding.getRoot());

        phoneNumber = mSignUpBinding.textInputPhone;
        fullName = mSignUpBinding.textInputFullname;
        password = mSignUpBinding.textInputPassword;

        goBack = mSignUpBinding.signupBackButton;
        layoutSignUp= mSignUpBinding.layoutSignUp;

        loadingDialog = new LoadingDialog(SignUpActivity.this);

        mDatabase = FirebaseDatabase.getInstance();
    }

    public void signup(final View view) {
        if (!Utils.isConnected(this)) {
            Utils.showCustomDialog(this);
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

                    Snackbar.Callback callback= snackbarCallBackOnDismissed(_phoneNumber,_password);
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

    public Snackbar.Callback snackbarCallBackOnDismissed(final String _phoneNumber, final String _password) {
        return new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                super.onDismissed(snackbar, event);
                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                intent.putExtra("phoneUser", _phoneNumber);
                intent.putExtra("password",_password);
                intent.putExtra("isAuth",true);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                finish();
            }

        };
    }

    public void goBack(View view) {
        Intent intent = new Intent(this, LoginActivity.class);

//        Pair[] pairs = new Pair[1];
//        pairs[0] = new Pair<View, String>(layoutSignUp, "transition_signup");

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this,pairs);
//            startActivity(intent, options.toBundle());
//        } else {
            startActivity(intent);
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);

//        }

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

    private boolean checkFullName(String _fullName) {
        Pattern pattern = Pattern.compile("^[A-Za-z ]{4,20}$");
        Matcher matcher = pattern.matcher(_fullName);
        if (TextUtils.isEmpty(_fullName)) {
            fullName.setError("Full name can't be empty");
            fullName.requestFocus();
            return false;
        }
        if (!matcher.matches()) {
            fullName.setError("Invalid full name pattern");
            fullName.requestFocus();
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
        String _fullName = fullName.getEditText().getText().toString().trim();
        String _password = password.getEditText().getText().toString().trim();
        phoneNumber.setErrorEnabled(false);
        fullName.setErrorEnabled(false);
        password.setErrorEnabled(false);

        if (checkValidPhone(_phoneNumber) & checkValidPassword(_password) & checkFullName(_fullName)) {
            return true;
        }
        return false;

    }

}
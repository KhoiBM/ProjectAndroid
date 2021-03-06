package com.prm391.project.bingeeproject.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.prm391.project.bingeeproject.Fragment.HomeFragment;
import com.prm391.project.bingeeproject.Interface.NavigationHost;
import com.prm391.project.bingeeproject.R;

public class MainActivity extends AppCompatActivity implements NavigationHost {

    private static final String TAG = MainActivity.class.getSimpleName();
    private String phoneUser;
    private String password;
    private boolean isAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        phoneUser = intent.getStringExtra("phoneUser");
        password = intent.getStringExtra("password");
        isAuth = intent.getBooleanExtra("isAuth",false);
        Bundle bundle = new Bundle();
        bundle.putBoolean("isAuth",isAuth);
        HomeFragment homeFragment = new HomeFragment();
        homeFragment.setArguments(bundle);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, homeFragment)
                    .commit();
        }
        Log.i(TAG, "phoneUser: " + phoneUser);
        Log.i(TAG, "password: " + password);
        Log.i(TAG, "isAuth" + isAuth);
    }

    /**
     * Navigate to the given fragment.
     *
     * @param fragment       Fragment to navigate to.
     * @param addToBackstack Whether or not the current fragment should be added to the backstack.
     */
    @Override
    public void navigateTo(Fragment fragment, Bundle bundle, boolean addToBackstack) {

        Log.i(TAG, "phoneUser: " + phoneUser);
        Log.i(TAG, "password: " + password);
        Log.i(TAG, "isAuth" + isAuth);

        bundle.putString("phoneUser", phoneUser);
        bundle.putString("password", password);
        bundle.putBoolean("isAuth",isAuth);

        fragment.setArguments(bundle);
        FragmentTransaction transaction =
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, fragment);

        if (addToBackstack) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }

    @Override
    public void logout() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void login() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

}

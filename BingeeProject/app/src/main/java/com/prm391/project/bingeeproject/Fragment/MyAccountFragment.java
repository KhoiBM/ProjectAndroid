package com.prm391.project.bingeeproject.Fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.prm391.project.bingeeproject.Common.MainActivity;
import com.prm391.project.bingeeproject.Common.NavigationHost;
import com.prm391.project.bingeeproject.Common.NavigationIconClickListener;
import com.prm391.project.bingeeproject.Common.SplashActivity;
import com.prm391.project.bingeeproject.Dialog.LoadingDialog;
import com.prm391.project.bingeeproject.R;
import com.prm391.project.bingeeproject.Utils.HandleNavMenu;
import com.prm391.project.bingeeproject.Utils.HandleSearchComponent;
import com.prm391.project.bingeeproject.Utils.Utils;
import com.prm391.project.bingeeproject.databinding.FragmentMyAccountBinding;
import com.prm391.project.bingeeproject.databinding.FragmentProfileBinding;


public class MyAccountFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = MyAccountFragment.class.getSimpleName();
    private FragmentMyAccountBinding mBinding;
    private String phoneUser, password;
    private TextView text_name;
    private Button edit, trackOrder, logout;
    private LoadingDialog loadingDialog;
    private FirebaseDatabase mDatabase;
    private DatabaseReference table_user;
    private boolean isAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabase = FirebaseDatabase.getInstance();
        table_user = mDatabase.getReference("Users");

        setHasOptionsMenu(true);

        Bundle bundle = this.getArguments();
        phoneUser = bundle.getString("phoneUser");
        password = bundle.getString("password");
        isAuth = bundle.getBoolean("isAuth");
        Log.i(TAG, "isAuth" + isAuth);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentMyAccountBinding.inflate(inflater, container, false);
        View view = mBinding.getRoot();

        setUpToolbar(view);

        HandleSearchComponent.handleSearchView(view, getActivity());

        text_name = mBinding.textName;

        buttonsSetUp(view);

        loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.startLoadingDialog();


        loadProfileUser();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.findViewById(R.id.my_account_grid).setBackgroundResource(R.drawable.corner_cut_grid_background_shape);
//            view.findViewById(R.id.scroll_view_myaccount).setBackgroundResource(R.drawable.corner_cut_grid_background_shape);
        }

        return view;
    }

    private void loadProfileUser() {
        //Database
        Query checkUser = table_user.orderByChild("mPhone").equalTo(phoneUser);
        checkUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    loadingDialog.dismissDialog();
                    String systemPassWord = dataSnapshot.child(phoneUser).child("mPassword").getValue(String.class);
                    if (systemPassWord.equals(password)) {
                        showProfileWhenUserValid(dataSnapshot);
                    }
                } else {
                    loadingDialog.dismissDialog();
                    Utils.showSnackbarWithNoAction(getView(), "Data does not exist!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingDialog.dismissDialog();
                Utils.showSnackbarWithNoAction(getView(), databaseError.getMessage());
            }
        });
    }

    private String getFullname(DataSnapshot dataSnapshot, String id) {
        return dataSnapshot.child(id).child("mFullName").getValue(String.class);
    }

    private void showProfileWhenUserValid(DataSnapshot dataSnapshot) {
        text_name.setText(getFullname(dataSnapshot, phoneUser));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.bin_toolbar_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    private void setUpToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.app_bar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
        }

        toolbar.setNavigationOnClickListener(new NavigationIconClickListener(
                getContext(),
                view.findViewById(R.id.my_account_grid),
                new AccelerateDecelerateInterpolator(),
                getContext().getResources().getDrawable(R.drawable.bin_menu),
                getContext().getResources().getDrawable(R.drawable.ic_close),view)
                );
    }

    private Button nav_btn_ingredients, nav_btn_furniture, nav_btn_go_home, nav_btn_cart, nav_btn_my_account;

    private void buttonsSetUp(View view) {
        edit = mBinding.btnEditProfile;
        trackOrder = mBinding.btnTrackOrder;
        logout = mBinding.btnLogOut;

        edit.setOnClickListener(this);
        trackOrder.setOnClickListener(this);
        logout.setOnClickListener(this);

        //menu button setup

        nav_btn_go_home = view.findViewById(R.id.nav_btn_go_home);
        nav_btn_go_home.setOnClickListener(this);
        nav_btn_ingredients = view.findViewById(R.id.nav_btn_ingredients);
        nav_btn_ingredients.setOnClickListener(this);
        nav_btn_cart = view.findViewById(R.id.nav_btn_cart);
        nav_btn_cart.setOnClickListener(this);
        nav_btn_furniture = view.findViewById(R.id.nav_btn_furniture);
        nav_btn_furniture.setOnClickListener(this);
        nav_btn_my_account = view.findViewById(R.id.nav_btn_my_account);
        setActionForBtnMyAccount();

        //--------------------------------
    }
    private void setActionForBtnMyAccount(){
        if(isAuth){
            nav_btn_my_account.setText("MY ACCOUNT");
        }else{
            nav_btn_my_account.setText("LOGIN");
            nav_btn_my_account.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((NavigationHost) getActivity()).login();
                }
            });
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_Edit_Profile:
                Bundle btn_Edit_Profile_bundle = new Bundle();
                ((NavigationHost) getActivity()).navigateTo(new ProfileFragment(), btn_Edit_Profile_bundle, true);
                break;
            case R.id.btn_Track_Order:
                Bundle btn_Track_Order_bundle = new Bundle();
                ((NavigationHost) getActivity()).navigateTo(new TrackRequestOrderListFragment(), btn_Track_Order_bundle, true);
                break;
            case R.id.btn_log_out:
                ((NavigationHost) getActivity()).logout();
                break;
        }
        HandleNavMenu.commonNavigationMenuForCategory(v, getActivity());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Utils.handleOnOptionsItemSelected(item,getActivity());
        return super.onOptionsItemSelected(item);
    }
}
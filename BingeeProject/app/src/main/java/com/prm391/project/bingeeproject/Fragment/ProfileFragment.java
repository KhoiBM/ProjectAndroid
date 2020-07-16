package com.prm391.project.bingeeproject.Fragment;

import android.os.Bundle;

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
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

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
import com.prm391.project.bingeeproject.Common.LoginActivity;
import com.prm391.project.bingeeproject.Common.NavigationHost;
import com.prm391.project.bingeeproject.Dialog.LoadingDialog;
import com.prm391.project.bingeeproject.Model.User;
import com.prm391.project.bingeeproject.R;
import com.prm391.project.bingeeproject.Utils.Ultils;
import com.prm391.project.bingeeproject.databinding.ActivityLoginBinding;
import com.prm391.project.bingeeproject.databinding.FragmentProfileBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;


public class ProfileFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = ProfileFragment.class.getSimpleName();
    private FragmentProfileBinding mBinding;
    private String phoneUser, password;
    //    CountryCodePicker countryCodePicker;
    TextInputLayout phoneNumber, fullName, dob, address, email;
    RadioButton rbtnMale, rbtnFemale;
    MaterialButton update;
    ImageView goback;
    private LoadingDialog loadingDialog;
    private FirebaseDatabase mDatabase;
    private DatabaseReference table_user;
    boolean phoneChanged = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        phoneUser = "0387741552";
        password = "12345678";
        mDatabase = FirebaseDatabase.getInstance();
        table_user = mDatabase.getReference("Users");

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        mBinding = FragmentProfileBinding.inflate(inflater, container, false);
        View view = mBinding.getRoot();

        setUpToolbar(view);

        phoneNumber = mBinding.textInputPhone;
        fullName = mBinding.textInputFullname;
        dob = mBinding.textInputDob;
        address = mBinding.textInputAddress;
        email = mBinding.textInputEmail;
        rbtnMale = mBinding.rbtnMale;
        rbtnFemale = mBinding.rbtnFemale;
//        countryCodePicker = mBinding.countryCodePicker;
        update = mBinding.btnUpdateProfile;
//        goback = mBinding.profileBackButton;

        update.setOnClickListener(this);
//        goback.setOnClickListener(this);

        loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.startLoadingDialog();
        //Database
        Query checkUser = table_user.orderByChild("mPhone").equalTo(phoneUser);
        checkUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    loadingDialog.dismissDialog();
                    String systemPassWord = dataSnapshot.child(phoneUser).child("mPassword").getValue(String.class);
                    if (systemPassWord.equals(password)) {
                        String _fullName = dataSnapshot.child(phoneUser).child("mFullName").getValue(String.class);
                        String _phoneNo = dataSnapshot.child(phoneUser).child("mPhone").getValue(String.class);
                        String _dob = dataSnapshot.child(phoneUser).child("mDOB").getValue(String.class);
                        String _address = dataSnapshot.child(phoneUser).child("mAddress").getValue(String.class);
                        String _email = dataSnapshot.child(phoneUser).child("mEmail").getValue(String.class);
                        Boolean _gender = dataSnapshot.child(phoneUser).child("mGender").getValue(Boolean.class);
                        fullName.getEditText().setText(_fullName);
                        phoneNumber.getEditText().setText(_phoneNo);
                        dob.getEditText().setText(_dob);
                        address.getEditText().setText(_address);
                        email.getEditText().setText(_email);
                        if (_gender) {
                            rbtnMale.setChecked(true);
                        } else {
                            rbtnFemale.setChecked(true);
                        }
                    }
                } else {
                    loadingDialog.dismissDialog();
                    Snackbar.make(container, "Data does not exist!", Snackbar.LENGTH_SHORT)
                            .setAction("No action", null)
                            .show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingDialog.dismissDialog();
                Snackbar.make(container, databaseError.getMessage(), Snackbar.LENGTH_SHORT)
                        .setAction("No action", null)
                        .show();
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    private void update(final View view) {
        loadingDialog.startLoadingDialog();
        Query checkUser = table_user.orderByChild("mPhone").equalTo(phoneUser);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    loadingDialog.dismissDialog();
                    String systemPassWord = dataSnapshot.child(phoneUser).child("mPassword").getValue(String.class);
                    if (systemPassWord.equals(password)) {
                        String _fullName = dataSnapshot.child(phoneUser).child("mFullName").getValue(String.class);
                        String _phoneNo = dataSnapshot.child(phoneUser).child("mPhone").getValue(String.class);
                        String _dob = dataSnapshot.child(phoneUser).child("mDOB").getValue(String.class);
                        String _address = dataSnapshot.child(phoneUser).child("mAddress").getValue(String.class);
                        String _email = dataSnapshot.child(phoneUser).child("mEmail").getValue(String.class);
                        Boolean _gender = dataSnapshot.child(phoneUser).child("mGender").getValue(Boolean.class);

                        User user = new User();
                        user.setmFullName(_fullName);
                        user.setmPassword(systemPassWord);
                        user.setmPhone(_phoneNo);
                        user.setmDOB(_dob);
                        user.setmAddress(_address);
                        user.setmEmail(_email);
                        user.setmGender(_gender);
//                        isPhoneNoChanged(user, view);
                        if (isFullNameChanged(_fullName) | isDOBChanged(_dob) | isAddressChanged(_address) | isEmailChanged(_email) | isGenderChanged(_gender)) {
                            Snackbar.make(view, "Data has been updated", Snackbar.LENGTH_SHORT)
                                    .setAction("No action", null)
                                    .show();
                        }

                    } else {
//                        progressbar.setVisibility(View.GONE);
                        Snackbar.make(view, "Please login again to update because password not match!", Snackbar.LENGTH_SHORT)
                                .setAction("No action", null)
                                .show();

                    }
                } else {
                    loadingDialog.dismissDialog();
                    Snackbar.make(view, "Data does not exist!", Snackbar.LENGTH_SHORT)
                            .setAction("No action", null)
                            .show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingDialog.dismissDialog();
                Snackbar.make(view, databaseError.getMessage(), Snackbar.LENGTH_SHORT)
                        .setAction("No action", null)
                        .show();
            }
        });


    }

//    public void isPhoneNoChanged(final User user, final View view) {
//        final String up_phoneNo = phoneNumber.getEditText().getText().toString().trim();
//        if (!up_phoneNo.equals(user.getmPhone())) {
//            Query checkPhoneExist = table_user.orderByChild("mPhone").equalTo(up_phoneNo);
//            checkPhoneExist.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    if (dataSnapshot.exists()) {
//                        Snackbar.make(view, "Phone already exists!", Snackbar.LENGTH_SHORT)
//                                .setAction("No action", null)
//                                .show();
//                    } else {
//                        table_user.child(phoneUser).removeValue();
//                        User userClone = user;
//                        userClone.setmPhone(up_phoneNo);
//                        table_user.child(up_phoneNo).setValue(userClone);
//                        phoneUser = up_phoneNo;
//                        Snackbar.make(view, "Phone has been updated", Snackbar.LENGTH_SHORT)
//                                .setAction("No action", null)
//                                .show();
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//                    Snackbar.make(view, databaseError.getMessage(), Snackbar.LENGTH_SHORT)
//                            .setAction("No action", null)
//                            .show();
//                }
//            });
//        }
//    }


    private boolean isFullNameChanged(String _fullName) {
        String up_fullName = fullName.getEditText().getText().toString().trim();
        if (!up_fullName.equals(_fullName)) {
            table_user.child(phoneUser).child("mFullName").setValue(up_fullName);
            return true;
        }
        return false;
    }

    private boolean isDOBChanged(String _dob) {
        String up_dob = dob.getEditText().getText().toString().trim();
        if (!up_dob.equals(_dob)) {
            table_user.child(phoneUser).child("mDOB").setValue(up_dob);
            return true;
        }
        return false;
    }

    private boolean isAddressChanged(String _address) {
        String up_address = address.getEditText().getText().toString().trim();
        if (!up_address.equals(_address)) {
            HashMap<String, Object> values = new HashMap<>();
            values.put("mAddress", up_address);
            table_user.child(phoneUser).updateChildren(values);
            return true;
        }
        return false;
    }

    private boolean isEmailChanged(String _email) {
        String up_email = email.getEditText().getText().toString().trim();
        if (!up_email.equals(_email)) {
            table_user.child(phoneUser).child("mEmail").setValue(up_email);
            return true;
        }
        return false;
    }

    private boolean isGenderChanged(Boolean _gender) {
        if (!_gender == rbtnMale.isChecked()) {
            table_user.child(phoneUser).child("mGender").setValue(rbtnMale.isChecked());
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_update_profile:
                update(v);
                break;
//            case R.id.profile_back_button:
//                goBack(v);
//                break;
        }
    }

    private void goBack(View v) {
        ((NavigationHost) getActivity()).navigateTo(new HomeFragment(), true);
    }
    private void setUpToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.app_bar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
        }
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.bin_toolbar_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                break;
            case R.id.shopping_cart:
                ((NavigationHost) getActivity()).navigateTo(new CartFragment(), true);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
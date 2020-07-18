package com.prm391.project.bingeeproject.Fragment;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.RadioButton;

import androidx.annotation.NonNull;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.prm391.project.bingeeproject.Common.NavigationHost;
import com.prm391.project.bingeeproject.Dialog.LoadingDialog;
import com.prm391.project.bingeeproject.R;
import com.prm391.project.bingeeproject.Utils.HandleSearchComponent;
import com.prm391.project.bingeeproject.Utils.Utils;
import com.prm391.project.bingeeproject.databinding.FragmentProfileBinding;

import java.util.Calendar;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ProfileFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = ProfileFragment.class.getSimpleName();
    private FragmentProfileBinding mBinding;
    private String phoneUser, password;
    private TextInputLayout phoneNumber, fullName, dob, address, email;
    private RadioButton rbtnMale, rbtnFemale;
    private MaterialButton update;
    private LoadingDialog loadingDialog;
    private FirebaseDatabase mDatabase;
    private DatabaseReference table_user;

    private DatePickerDialog.OnDateSetListener setListener;
    private DatePicker datePicker;

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
        mBinding = FragmentProfileBinding.inflate(inflater, container, false);
        View view = mBinding.getRoot();

        setUpToolbar(view);

        HandleSearchComponent.handleSearchView(view,getActivity());


        phoneNumber = mBinding.textInputPhone;
        fullName = mBinding.textInputFullname;
        dob = mBinding.textInputDob;
        address = mBinding.textInputAddress;
        email = mBinding.textInputEmail;

        rbtnMale = mBinding.rbtnMale;
        rbtnFemale = mBinding.rbtnFemale;

        update = mBinding.btnUpdateProfile;

        update.setOnClickListener(this);

        loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.startLoadingDialog();

        createDOBDialog();

        loadProfileUser();

        return view;
    }
    private  void createDOBDialog(){
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        mBinding.editTextDob.setShowSoftInputOnFocus(false);

        mBinding.editTextDob.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), android.R.style.ThemeOverlay_Material_Dialog,
                        setListener,year,month,day);
                datePickerDialog.show();
            }
        });

        setListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = dayOfMonth + "/" + month + "/" + year;
                dob.getEditText().setText(date);
            }
        };

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

    public String getPhoneNo(DataSnapshot dataSnapshot, String id) {
        return dataSnapshot.child(id).child("mPhone").getValue(String.class);
    }

    public String getDob(DataSnapshot dataSnapshot, String id) {
        return dataSnapshot.child(id).child("mDOB").getValue(String.class);
    }


    public String getAddress(DataSnapshot dataSnapshot, String id) {
        return dataSnapshot.child(id).child("mAddress").getValue(String.class);
    }


    public String getEmail(DataSnapshot dataSnapshot, String id) {
        return dataSnapshot.child(id).child("mEmail").getValue(String.class);
    }


    public Boolean getGender(DataSnapshot dataSnapshot, String id) {
        return dataSnapshot.child(id).child("mGender").getValue(Boolean.class);
    }

    private void showProfileWhenUserValid(DataSnapshot dataSnapshot) {

        fullName.getEditText().setText(getFullname(dataSnapshot, phoneUser));
        phoneNumber.getEditText().setText(getPhoneNo(dataSnapshot, phoneUser));
        dob.getEditText().setText(getDob(dataSnapshot, phoneUser));
        address.getEditText().setText(getAddress(dataSnapshot, phoneUser));
        email.getEditText().setText(getEmail(dataSnapshot, phoneUser));
        if (getGender(dataSnapshot, phoneUser)) {
            rbtnMale.setChecked(true);
        } else {
            rbtnFemale.setChecked(true);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    private void update(final View view) {
        if (!validateField()) {
            return;
        }
        loadingDialog.startLoadingDialog();
        Query checkUser = table_user.orderByChild("mPhone").equalTo(phoneUser);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    loadingDialog.dismissDialog();
                    String systemPassWord = dataSnapshot.child(phoneUser).child("mPassword").getValue(String.class);
                    if (systemPassWord.equals(password)) {
                        updateWhenUserValid(dataSnapshot);
                    } else {
                        Utils.showSnackbarWithNoAction(getView(), "Please login again to update because password not match!");
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

    private void updateWhenUserValid(DataSnapshot dataSnapshot) {
        if (isFullNameChanged(getFullname(dataSnapshot, phoneUser)) | isDOBChanged(getDob(dataSnapshot, phoneUser)) | isAddressChanged(getAddress(dataSnapshot, phoneUser)) | isEmailChanged(getEmail(dataSnapshot, phoneUser)) | isGenderChanged(getGender(dataSnapshot, phoneUser))) {
            Utils.showSnackbarWithNoAction(getView(), "Data has been updated");
        }
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
        }
    }

    private void goBack(View v) {
        Bundle bundle = new Bundle();
        ((NavigationHost) getActivity()).navigateTo(new HomeFragment(),bundle, true);
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
        inflater.inflate(R.menu.bin_toolbar_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Utils.handleOnOptionsItemSelected(item,getActivity());
        return super.onOptionsItemSelected(item);
    }

    //check valid

    private boolean validateField() {
        String _fullName = fullName.getEditText().getText().toString().trim();
        String _address = address.getEditText().getText().toString().trim();
        String _email = email.getEditText().getText().toString().trim();
        fullName.setErrorEnabled(false);
        address.setErrorEnabled(false);
        email.setErrorEnabled(false);

        if (checkValidFullName(_fullName) && checkValidAddress(_address) && checkValidEmail(_email)) {
            return true;
        }
        return false;

    }

    private boolean checkValidFullName(String _fullName) {
//        Pattern pattern = Pattern.compile("^[A-Za-z]{1,20}$");
//        Matcher matcher = pattern.matcher(_fullName);
        if (TextUtils.isEmpty(_fullName) ) {
            fullName.setError("Full name field can't be empty");
            fullName.requestFocus();
            return false;
        }
//        if (!matcher.matches()) {
//            fullName.setError("Invalid Full name pattern");
//            fullName.requestFocus();
//            return false;
//        }
        return true;
    }

    private boolean checkValidAddress(String _address) {
//        Pattern pattern = Pattern.compile("^[A-Za-z0-9]{1,20}$");
//        Matcher matcher = pattern.matcher(_address);
        if (TextUtils.isEmpty(_address)) {
            address.setError("Address field can't be empty");
            address.requestFocus();
            return false;
        }
//        if (!matcher.matches()) {
//            address.setError("Invalid Address pattern");
//            address.requestFocus();
//            return false;
//        }
        return true;
    }

    private boolean checkValidEmail(String _email) {
        Pattern pattern = Pattern.compile("^[a-z][a-z0-9_\\.]{5,32}@[a-z0-9]{2,}(\\.[a-z0-9]{2,4}){1,2}$");
        Matcher matcher = pattern.matcher(_email);
        if (TextUtils.isEmpty(_email)) {
            email.setError("Email field can't be empty");
            email.requestFocus();
            return false;
        }
        if (!matcher.matches()) {
            email.setError("Invalid Email pattern");
            email.requestFocus();
            return false;
        }
        return true;
    }



}
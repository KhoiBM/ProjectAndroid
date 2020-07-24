package com.prm391.project.bingeeproject.Fragment;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.prm391.project.bingeeproject.Adapter.GridItemDecoration;
import com.prm391.project.bingeeproject.Adapter.ItemCardRecyclerViewAdapter;
import com.prm391.project.bingeeproject.Common.NavigationHost;
import com.prm391.project.bingeeproject.Common.NavigationIconClickListener;
import com.prm391.project.bingeeproject.Databases.CartDAO;
import com.prm391.project.bingeeproject.Model.Order;
import com.prm391.project.bingeeproject.Model.Request;
import com.prm391.project.bingeeproject.R;
import com.prm391.project.bingeeproject.Utils.HandleNavMenu;
import com.prm391.project.bingeeproject.Utils.HandleSearchComponent;
import com.prm391.project.bingeeproject.Utils.Utils;
import com.prm391.project.bingeeproject.databinding.FragmentCheckoutBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CheckoutFragment extends Fragment implements View.OnClickListener {


    private static final String TAG = CheckoutFragment.class.getSimpleName();
    private FragmentCheckoutBinding mBinding;
    private RecyclerView recyclerView;
    private TextView totalPrice;
    private Button placeOrder;
    private View snackbarLayout;
    private List<Order> carts;
    private ItemCardRecyclerViewAdapter<Order, CheckoutFragment> adapter;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private CartDAO<CheckoutFragment> cartDAO;
    private TextInputLayout phoneLayout, fullnameLayout, addressLayout;
    private RadioButton rbtnCOD, rbtnCreditCard;
    private FirebaseDatabase mDatabase;
    private double total = 0;
    private String phoneUser;
    private String password;
    private boolean isAuth;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        storage = FirebaseStorage.getInstance("gs://bingee-358c7.appspot.com");
        storageRef = storage.getReference("product");

        cartDAO = new CartDAO(getActivity());

        mDatabase = FirebaseDatabase.getInstance();

        Bundle bundle = this.getArguments();
        phoneUser = bundle.getString("phoneUser");
        password =bundle.getString("password");
        isAuth = bundle.getBoolean("isAuth");
        Log.i(TAG, "isAuth" + isAuth);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentCheckoutBinding.inflate(inflater, container, false);
        View view = mBinding.getRoot();

        setUpToolbar(view);

        HandleSearchComponent.handleSearchView(view,getActivity());

        buttonsSetUp(view);

        totalPrice = mBinding.cartTotalPrice;
        snackbarLayout = mBinding.snackBarCart;
        phoneLayout = mBinding.textInputPhone;
        fullnameLayout = mBinding.textInputFullname;
        addressLayout = mBinding.textInputAddress;
        rbtnCOD = mBinding.rbtnCOD;
        rbtnCreditCard = mBinding.rbtnCreditCard;
        placeOrder = mBinding.btnPlaceOrder;
        placeOrder.setOnClickListener(this);

        recyclerView = mBinding.recyclerViewCart;
        recyclerView.setHasFixedSize(false);

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false));

        int largePadding = getResources().getDimensionPixelSize(R.dimen.bin_item_grid_spacing);
        int smallPadding = getResources().getDimensionPixelSize(R.dimen.bin_category_grid_spacing_small);
        recyclerView.addItemDecoration(new GridItemDecoration(largePadding, smallPadding));

        loadViewCart();
        loadInformationUserForCheckout();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.findViewById(R.id.checkout_grid).setBackgroundResource(R.drawable.corner_cut_grid_background_shape);
        }

        return view;
    }

    private void loadInformationUserForCheckout() {
        //Database
        final DatabaseReference table_user = mDatabase.getReference("Users");
        Query checkUser = table_user.orderByChild("mPhone").equalTo(phoneUser);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String systemPassWord = dataSnapshot.child(phoneUser).child("mPassword").getValue(String.class);
                    if (systemPassWord.equals(password)) {
                        showInformationWhenUserValid(dataSnapshot);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Utils.showSnackbarWithNoAction(getView(), databaseError.getMessage());
            }
        });
    }

    private void showInformationWhenUserValid(DataSnapshot dataSnapshot) {
        fullnameLayout.getEditText().setText(getFullname(dataSnapshot, phoneUser));
        phoneLayout.getEditText().setText(getPhoneNo(dataSnapshot, phoneUser));
        addressLayout.getEditText().setText(getAddress(dataSnapshot, phoneUser));
    }

    private String getFullname(DataSnapshot dataSnapshot, String id) {
        return dataSnapshot.child(id).child("mFullName").getValue(String.class);
    }

    public String getPhoneNo(DataSnapshot dataSnapshot, String id) {
        return dataSnapshot.child(id).child("mPhone").getValue(String.class);
    }

    public String getAddress(DataSnapshot dataSnapshot, String id) {
        return dataSnapshot.child(id).child("mAddress").getValue(String.class);
    }

    public void loadViewCart() {
        getListCart();
        loadTotalPrice();
        loadListCart();
    }

    public void reLoadViewCart() {
        getReloadListCart();
        loadTotalPrice();
        loadListCart();
    }

    private void getListCart() {
        carts = new ArrayList<>();
        carts = cartDAO.getCarts();
    }

    private void getReloadListCart() {
        carts = new ArrayList<>();
        carts = cartDAO.getCarts();
    }

    public void reloadWhenChangeQuantityDifferentZero() {
        getListCart();
        loadTotalPrice();
    }

    private void loadTotalPrice() {
        total = 0;
        for (Order item : carts) {
            total += item.getmPrice() * item.getmQuantity();
        }
        totalPrice.setText(String.format("%.2f", total) + "$");
    }

    private void loadListCart() {
        adapter = new ItemCardRecyclerViewAdapter<>(getView(), getActivity(), carts, snackbarLayout, storageRef, CheckoutFragment.this, R.layout.item_in_checkout_card);
        recyclerView.setAdapter(adapter);
    }

    private void setUpToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.app_bar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
        }

        toolbar.setNavigationOnClickListener(new NavigationIconClickListener(
                getContext(),
                view.findViewById(R.id.checkout_grid),
                new AccelerateDecelerateInterpolator(),
                getContext().getResources().getDrawable(R.drawable.bin_menu),
                getContext().getResources().getDrawable(R.drawable.ic_close),view));
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

    private Button nav_btn_ingredients, nav_btn_furniture, nav_btn_go_home, nav_btn_my_account, nav_btn_cart;

    private void buttonsSetUp(View view) {
        //menu button setup

        nav_btn_go_home = view.findViewById(R.id.nav_btn_go_home);
        nav_btn_go_home.setOnClickListener(this);
        nav_btn_ingredients = view.findViewById(R.id.nav_btn_ingredients);
        nav_btn_ingredients.setOnClickListener(this);
        nav_btn_furniture = view.findViewById(R.id.nav_btn_furniture);
        nav_btn_furniture.setOnClickListener(this);
        nav_btn_cart = view.findViewById(R.id.nav_btn_cart);
        nav_btn_cart.setOnClickListener(this);
        nav_btn_my_account = view.findViewById(R.id.nav_btn_my_account);
        setActionForBtnMyAccount();

        //--------------------------------
    }
    private void setActionForBtnMyAccount(){
        if(isAuth){
            nav_btn_my_account.setText("MY ACCOUNT");
            nav_btn_my_account.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        int id = v.getId();
            switch (id) {
            case R.id.btn_place_order:
                placeOrder(v);
                break;
        }
        HandleNavMenu.commonNavigationMenuForCategory(v, getActivity());
    }

    private void placeOrder(final View view) {
        if (!validateField()) {
            return;
        }

        final DatabaseReference table_requests = mDatabase.getReference("Requests");

        table_requests.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                String shipingToPhone = phoneLayout.getEditText().getText().toString().trim();
                String shipingToFullname = fullnameLayout.getEditText().getText().toString().trim();
                String shipingToAddress = addressLayout.getEditText().getText().toString().trim();
                String paymentMethod = rbtnCOD.isChecked() ? "Cash on Delivery (COD)" : "Credit Card";

                Request request = new Request();
                request.setmPhoneUser(phoneUser);
                request.setmShippingToPhone(shipingToPhone);
                request.setmShippingToFullname(shipingToFullname);
                request.setmShippingToAddress(shipingToAddress);
                request.setmPaymentMethod(paymentMethod);
                request.setmStatus("Place");
                String currentDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
                request.setmDateOrder(currentDate);
                request.setmTotal(total);
                request.setmOrders(carts);

                table_requests.push().setValue(request);

                boolean isCleanCart = cartDAO.cleanCartWhenFinishCheckout(snackbarLayout, "");
                if (isCleanCart) {
                    Snackbar.Callback callback = snackbarCallBackOnDismissed();
                    Snackbar.make(view, "Place Order is successful", Snackbar.LENGTH_SHORT)
                            .addCallback(callback).show();
                } else {
                    Utils.showSnackbarWithNoAction(view, "Place Order is fail");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Utils.showSnackbarWithNoAction(view, databaseError.getMessage());
            }
        });
    }

    public Snackbar.Callback snackbarCallBackOnDismissed() {
        return new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                super.onDismissed(snackbar, event);
                Bundle bundle = new Bundle();
                ((NavigationHost) getActivity()).navigateTo(new HomeFragment(), bundle, true);
            }

        };
    }
    private boolean validateField() {
        String _phone = phoneLayout.getEditText().getText().toString().trim();
        String _fullName = fullnameLayout.getEditText().getText().toString().trim();
        String _address = addressLayout.getEditText().getText().toString().trim();
        phoneLayout.setErrorEnabled(false);
        fullnameLayout.setErrorEnabled(false);
        addressLayout.setErrorEnabled(false);
        if (checkValidFullName(_fullName) & checkValidAddress(_address) & checkValidPhone(_phone)) {
            return true;
        }
        return false;

    }
    private boolean checkValidPhone(String _phone) {
        Pattern pattern = Pattern.compile("^(\\(\\d{3}\\)|\\d{3})-?\\d{3}-?\\d{4}$");
        Matcher matcher = pattern.matcher(_phone);
        if (TextUtils.isEmpty(_phone)) {
            phoneLayout.setError("Phone can't be empty");
            phoneLayout.requestFocus();
            return false;
        }
        if (!matcher.matches()) {
            phoneLayout.setError("Invalid phone pattern");
            phoneLayout.requestFocus();
            return false;
        }
        return true;
    }
    private boolean checkValidFullName(String _fullName) {
        if (TextUtils.isEmpty(_fullName)) {
            fullnameLayout.setError("Full name field can't be empty");
            fullnameLayout.requestFocus();
            return false;
        }
        return true;
    }

    private boolean checkValidAddress(String _address) {
        if (TextUtils.isEmpty(_address)) {
            addressLayout.setError("Address field can't be empty");
            addressLayout.requestFocus();
            return false;
        }
        return true;
    }


}
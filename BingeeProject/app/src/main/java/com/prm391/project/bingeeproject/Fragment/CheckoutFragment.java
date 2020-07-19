package com.prm391.project.bingeeproject.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.prm391.project.bingeeproject.Common.MainActivity;
import com.prm391.project.bingeeproject.Common.NavigationHost;
import com.prm391.project.bingeeproject.Common.SignUpActivity;
import com.prm391.project.bingeeproject.Databases.CartDAO;
import com.prm391.project.bingeeproject.Dialog.LoadingDialog;
import com.prm391.project.bingeeproject.Model.Order;
import com.prm391.project.bingeeproject.Model.Request;
import com.prm391.project.bingeeproject.Model.User;
import com.prm391.project.bingeeproject.R;
import com.prm391.project.bingeeproject.Utils.HandleSearchComponent;
import com.prm391.project.bingeeproject.Utils.Utils;
import com.prm391.project.bingeeproject.databinding.FragmentCheckoutBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentCheckoutBinding.inflate(inflater, container, false);
        View view = mBinding.getRoot();

        setUpToolbar(view);

        HandleSearchComponent.handleSearchView(view,getActivity());


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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_place_order:
                placeOrder(v);
                break;
        }
    }

    private void placeOrder(final View view) {

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
                request.setmTotal(Double.parseDouble(String.format("%.2f", total)));
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

}
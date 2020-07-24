package com.prm391.project.bingeeproject.Fragment;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.prm391.project.bingeeproject.databinding.FragmentViewDetailRequestOrderBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ViewDetailRequestOrderFragment extends Fragment implements View.OnClickListener {


    private static final String TAG = ViewDetailRequestOrderFragment.class.getSimpleName();
    private FragmentViewDetailRequestOrderBinding mBinding;
    private RecyclerView recyclerView;
    private TextView totalPrice;
    private List<Order> carts;
    private ItemCardRecyclerViewAdapter<Order, ViewDetailRequestOrderFragment> adapter;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private CartDAO<ViewDetailRequestOrderFragment> cartDAO;
    private TextInputLayout phoneLayout, fullnameLayout, addressLayout;
    private RadioButton rbtnCOD, rbtnCreditCard,rbtnPlace,rbtnShipping,rbtnFinish ;
    private FirebaseDatabase mDatabase;
    private String phoneUser;
    private String requestId;
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
        isAuth = bundle.getBoolean("isAuth");
        Log.i(TAG, "isAuth" + isAuth);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentViewDetailRequestOrderBinding.inflate(inflater, container, false);
        View view = mBinding.getRoot();

        setUpToolbar(view);

        HandleSearchComponent.handleSearchView(view,getActivity());

        buttonsSetUp(view);

        totalPrice = mBinding.cartTotalPrice;
        phoneLayout = mBinding.textInputPhone;
        fullnameLayout = mBinding.textInputFullname;
        addressLayout = mBinding.textInputAddress;
        rbtnCOD = mBinding.rbtnCOD;
        rbtnCreditCard = mBinding.rbtnCreditCard;
        rbtnPlace=mBinding.rbtnPlace;
        rbtnShipping=mBinding.rbtnShipping;
        rbtnFinish=mBinding.rbtnFinish;

        recyclerView = mBinding.recyclerViewCartRequestOrder;
        recyclerView.setHasFixedSize(false);

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false));

        int largePadding = getResources().getDimensionPixelSize(R.dimen.bin_item_grid_spacing);
        int smallPadding = getResources().getDimensionPixelSize(R.dimen.bin_category_grid_spacing_small);
        recyclerView.addItemDecoration(new GridItemDecoration(largePadding, smallPadding));

        Bundle bundle = this.getArguments();
        phoneUser = bundle.getString("phoneUser");
        requestId = bundle.getString("requestId");

        loadInformationRequestOrder();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.findViewById(R.id.view_detail_order_grid).setBackgroundResource(R.drawable.corner_cut_grid_background_shape);
        }

        return view;
    }

    private void loadInformationRequestOrder() {
        //Database
        Query requestList = mDatabase.getReference().child("Requests").child(requestId);
        requestList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    Request request = dataSnapshot.getValue(Request.class);
                    phoneLayout.getEditText().setText(request.getmShippingToPhone());
                    fullnameLayout.getEditText().setText(request.getmShippingToFullname());
                    addressLayout.getEditText().setText(request.getmShippingToAddress());
                    totalPrice.setText(String.format("%.2f", request.getmTotal()) + "$");
                    rbtnCOD.setChecked(request.getmPaymentMethod().equals("Cash on Delivery (COD)"));
                    rbtnCreditCard.setChecked(request.getmPaymentMethod().equals("Credit Card"));
                    rbtnPlace.setChecked(request.getmStatus().equals("Place"));
                    rbtnShipping.setChecked(request.getmStatus().equals("Shipping"));
                    rbtnFinish.setChecked(request.getmStatus().equals("Finish"));
                    carts = new ArrayList<>(request.getmOrders());
                    if (carts != null) {
                        loadListCart();
                    }
                    for (Order o : request.getmOrders()) {
                        System.out.println(o.toString());
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Utils.showSnackbarWithNoAction(getView(), databaseError.getMessage());
            }
        });
    }


    private void loadListCart() {
        adapter = new ItemCardRecyclerViewAdapter<>(getView(), getActivity(), carts, null, storageRef, ViewDetailRequestOrderFragment.this, R.layout.item_in_view_detail_order_card);
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
                view.findViewById(R.id.view_detail_order_grid),
                new AccelerateDecelerateInterpolator(),
                getContext().getResources().getDrawable(R.drawable.bin_menu),
                getContext().getResources().getDrawable(R.drawable.ic_close),view));
    }

    private Button nav_btn_go_home, nav_btn_ingredients, nav_btn_furniture, nav_btn_cart, nav_btn_my_account;

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
        HandleNavMenu.commonNavigationMenuForCategory(v, getActivity());
    }
}
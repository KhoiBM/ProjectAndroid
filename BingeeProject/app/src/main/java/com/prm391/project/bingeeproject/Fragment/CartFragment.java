package com.prm391.project.bingeeproject.Fragment;

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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.prm391.project.bingeeproject.Adapter.GridItemDecoration;
import com.prm391.project.bingeeproject.Adapter.ItemCardRecyclerViewAdapter;
import com.prm391.project.bingeeproject.Common.NavigationHost;
import com.prm391.project.bingeeproject.Databases.CartDAO;
import com.prm391.project.bingeeproject.Model.Order;
import com.prm391.project.bingeeproject.R;
import com.prm391.project.bingeeproject.Utils.Utils;
import com.prm391.project.bingeeproject.databinding.FragmentCartBinding;

import java.util.ArrayList;
import java.util.List;

import it.sephiroth.android.library.numberpicker.NumberPicker;


public class CartFragment extends Fragment implements View.OnClickListener {


    private static final String TAG = CartFragment.class.getSimpleName();
    private FragmentCartBinding mBinding;
    private RecyclerView recyclerView;
    private TextView totalPrice;
    private Button checkout, cleanCart;
    private View snackbarLayout;
    private RelativeLayout layoutHeaderCart, layoutBodyCart, layoutEmptyCart;
    private List<Order> carts;
    private ItemCardRecyclerViewAdapter<Order,CartFragment> adapter;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private NumberPicker numberPickerQuantity;
    private  CartDAO<CartFragment> cartDAO;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        storage = FirebaseStorage.getInstance("gs://bingee-358c7.appspot.com");
        storageRef = storage.getReference("product");

         cartDAO = new CartDAO(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentCartBinding.inflate(inflater, container, false);
        View view = mBinding.getRoot();

        setUpToolbar(view);

        recyclerView = mBinding.recyclerViewCart;
        totalPrice = mBinding.cartTotalPrice;
        checkout = mBinding.btnCheckout;
        cleanCart = mBinding.btnCleanCart;
        snackbarLayout = mBinding.snackBarCart;
        layoutHeaderCart = mBinding.layoutHeaderCart;
        layoutBodyCart = mBinding.layoutBodyCart;
        layoutEmptyCart = mBinding.layoutEmptyCart;
        layoutEmptyCart.setVisibility(View.GONE);
        numberPickerQuantity=view.findViewById(R.id.number_picker_quantity);

        checkout.setOnClickListener(this);
        cleanCart.setOnClickListener(this);

        recyclerView.setHasFixedSize(false);

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false));

        int largePadding = getResources().getDimensionPixelSize(R.dimen.bin_item_grid_spacing);
        int smallPadding = getResources().getDimensionPixelSize(R.dimen.bin_category_grid_spacing_small);
        recyclerView.addItemDecoration(new GridItemDecoration(largePadding, smallPadding));

        loadViewCart();

        return view;
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
        if (carts.size() == 0) {
            layoutHeaderCart.setVisibility(View.GONE);
            layoutBodyCart.setVisibility(View.GONE);
            layoutEmptyCart.setVisibility(View.VISIBLE);
        }
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
        double total = 0;
        for (Order item : carts) {
            total += item.getmPrice() * item.getmQuantity();
        }
        totalPrice.setText(String.format("%.2f",total) + "$");
    }

    private void loadListCart() {
        if(getActivity()!=null){
            adapter = new ItemCardRecyclerViewAdapter<>(getView(), getActivity(), carts, snackbarLayout,storageRef,CartFragment.this,R.layout.item_cart_card);
            recyclerView.setAdapter(adapter);
        }
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
        switch (item.getItemId()) {
            case R.id.search:
                break;
            case R.id.shopping_cart:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_checkout:
                checkout();
                break;
            case R.id.btn_clean_cart:
                cleanCart();
                break;
        }
    }

    private void cleanCart() {
        if (carts.size() != 0) {
            cartDAO.cleanCart(snackbarLayout, "cleanCart");
        } else {
            Utils.showSnackbarWithNoAction(snackbarLayout,"Cart is empty");
        }

    }

    private void checkout() {
        Bundle bundle = new Bundle();
        ((NavigationHost) getActivity()).navigateTo(new CheckoutFragment(),bundle, true);
    }
}
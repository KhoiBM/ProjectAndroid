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
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.prm391.project.bingeeproject.Adapter.CategoryCardViewHolder;
import com.prm391.project.bingeeproject.Adapter.GridItemDecoration;
import com.prm391.project.bingeeproject.Adapter.ItemCardRecyclerViewAdapter;
import com.prm391.project.bingeeproject.Adapter.ItemCardViewHolder;
import com.prm391.project.bingeeproject.Common.NavigationHost;
import com.prm391.project.bingeeproject.Databases.CartDAO;
import com.prm391.project.bingeeproject.Model.Category;
import com.prm391.project.bingeeproject.Model.Order;
import com.prm391.project.bingeeproject.R;
import com.prm391.project.bingeeproject.databinding.FragmentCartBinding;
import com.prm391.project.bingeeproject.databinding.FragmentProductDetailBinding;

import java.util.ArrayList;
import java.util.List;


public class CartFragment extends Fragment implements View.OnClickListener {


    private static final String TAG = CartFragment.class.getSimpleName();
    private FragmentCartBinding mBinding;
    private RecyclerView recyclerView;
    private TextView totalPrice;
    private Button checkout;
    private List<Order> carts;
    private ItemCardRecyclerViewAdapter<Order> adapter;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        storage = FirebaseStorage.getInstance("gs://bingee-358c7.appspot.com");
        storageRef = storage.getReference("product");

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

        checkout.setOnClickListener(this);

        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1, GridLayoutManager.VERTICAL, false));
        int largePadding = getResources().getDimensionPixelSize(R.dimen.bin_item_grid_spacing);
        int smallPadding = getResources().getDimensionPixelSize(R.dimen.bin_category_grid_spacing_small);
        recyclerView.addItemDecoration(new GridItemDecoration(largePadding, smallPadding));

        getListCart();

        loadTotalPrice();

        loadListCart();


        return view;
    }
    private void getListCart() {
        CartDAO cartDAO = new CartDAO(getContext());
        carts = new ArrayList<>();
        carts = cartDAO.getCarts();
    }

    private void loadTotalPrice() {
        double total=0;
        for (Order item : carts) {
            total+=item.getmPrice()*item.getmQuantity();
        }
        totalPrice.setText(String.valueOf(total)+ "$");
    }

    private void loadListCart() {
        adapter = new ItemCardRecyclerViewAdapter<>(carts);
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
        switch (item.getItemId()) {
            case R.id.search:
                break;
            case R.id.shopping_cart:
                ((NavigationHost) getActivity()).navigateTo(new CartFragment(), true);
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
        CartDAO cartDAO = new CartDAO(getContext());
        cartDAO.cleanCart();

        //refresh
        getListCart();
        loadTotalPrice();
        loadListCart();
    }

    private void checkout() {
    }
}
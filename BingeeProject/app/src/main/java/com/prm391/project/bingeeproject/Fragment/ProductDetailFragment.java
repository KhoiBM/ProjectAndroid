package com.prm391.project.bingeeproject.Fragment;

import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.prm391.project.bingeeproject.Adapter.GridItemDecoration;
import com.prm391.project.bingeeproject.Adapter.ProductCardViewHolder;
import com.prm391.project.bingeeproject.Adapter.ProductDetailCardViewHolder;
import com.prm391.project.bingeeproject.Common.NavigationHost;
import com.prm391.project.bingeeproject.Databases.CartDAO;
import com.prm391.project.bingeeproject.Databases.DBHelper;
import com.prm391.project.bingeeproject.Dialog.LoadingDialog;
import com.prm391.project.bingeeproject.Interface.ItemClickListener;
import com.prm391.project.bingeeproject.Model.Order;
import com.prm391.project.bingeeproject.Model.Product;
import com.prm391.project.bingeeproject.R;
import com.prm391.project.bingeeproject.Utils.HandleSearchComponent;
import com.prm391.project.bingeeproject.Utils.Utils;
import com.prm391.project.bingeeproject.databinding.FragmentProductDetailBinding;
import com.prm391.project.bingeeproject.databinding.FragmentProfileBinding;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = ProductDetailFragment.class.getSimpleName();
    private FirebaseDatabase mDatabase;
    private DatabaseReference product;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private StorageReference imagesRef;
    private FragmentProductDetailBinding mBinding;
    private ImageView productImage;
    private TextView productTitle, productPrice, productDescription;
    private MaterialButton addToCart,buyNow;
    private Product currentProduct;
    private String productID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabase = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance("gs://bingee-358c7.appspot.com");
        storageRef = storage.getReference("product");

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentProductDetailBinding.inflate(inflater, container, false);
        View view = mBinding.getRoot();

        setUpToolbar(view);

        HandleSearchComponent.handleSearchView(view,getActivity());

        productImage = mBinding.productImage;
        productTitle = mBinding.productTitle;
        productPrice = mBinding.productPrice;
        productDescription = mBinding.productDescription;
        addToCart=mBinding.btnAddToCart;
        buyNow=mBinding.btnBuyNow;

        addToCart.setOnClickListener(this);
        buyNow.setOnClickListener(this);

        Bundle bundle = this.getArguments();
        productID = bundle.getString("productID");

        product = mDatabase.getReference().child("Product").child(productID);

        loadProduct();


        return view;
    }

    private void loadProduct() {

        product.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    currentProduct = snapshot.getValue(Product.class);

                    productTitle.setText(snapshot.child("mName").getValue(String.class));
                    productPrice.setText("Just " + snapshot.child("mPrice").getValue(Double.class) + " $");
                    productDescription.setText(snapshot.child("mDescription").getValue(String.class));

                    imagesRef = storageRef.child(snapshot.child("mImage").getValue(String.class));

                    if (getActivity()!=null) {
                        Glide.with(getActivity()).load(imagesRef).into(productImage);
                    }
                } else {
                    Utils.showSnackbarWithNoAction(getView(),"Product does not exist!");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Utils.showSnackbarWithNoAction(getView(), error.getMessage());
            }
        });

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
            case R.id.btn_add_to_cart:
                addToCart("addToCart");
                break;
            case R.id.btn_buy_now:
                addToCart("buyNow");
                break;
        }
    }

    private void addToCart(String type) {
        if (getActivity()!=null) {
            CartDAO cartDAO = new CartDAO(getActivity());
            Order order = new Order();
            order.setmProductId(productID);
            order.setmProductName(currentProduct.getmName());
            order.setmPrice(currentProduct.getmPrice());
            order.setmQuantity(1);
            cartDAO.addToCart(order, getView(), getActivity(), type);
        }
    }
}
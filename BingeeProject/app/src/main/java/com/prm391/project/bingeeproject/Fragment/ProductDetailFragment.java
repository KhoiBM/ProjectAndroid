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
import com.prm391.project.bingeeproject.databinding.FragmentProductDetailBinding;
import com.prm391.project.bingeeproject.databinding.FragmentProfileBinding;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = ProductDetailFragment.class.getSimpleName();
    private FirebaseDatabase mDatabase;
    private DatabaseReference product;
    private LoadingDialog loadingDialog;
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
        loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.startLoadingDialog();

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
                    imagesRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downUri = task.getResult();
                                String imageUrl = downUri.toString();
                                Glide.with(productImage.getContext()).load(imageUrl).into(productImage);
//                            Snackbar.make(getView(), "" + imageUrl, Snackbar.LENGTH_SHORT)
//                                    .setAction("No action", null)
//                                    .show();
                            } else {
                                Snackbar.make(getView(), "" + task.getException(), Snackbar.LENGTH_SHORT)
                                        .setAction("No action", null)
                                        .show();
                            }
                        }
                    });


                } else {
                    Snackbar.make(getView(), "Product does not exist!", Snackbar.LENGTH_SHORT)
                            .setAction("No action", null)
                            .show();
                }
                loadingDialog.dismissDialog();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingDialog.dismissDialog();
                Snackbar.make(getView(), error.getMessage(), Snackbar.LENGTH_SHORT)
                        .setAction("No action", null)
                        .show();
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
            case R.id.btn_add_to_cart:
                addToCart();
                break;
            case R.id.btn_buy_now:
                buyNow();
                break;
        }
    }

    private void buyNow() {
        addToCart();
        ((NavigationHost) getActivity()).navigateTo(new CartFragment(), true);

    }

    private void addToCart() {

        CartDAO cartDAO = new CartDAO(getContext());
        Order order= new Order();
        order.setmProductId(productID);
        order.setmProductName(currentProduct.getmName());
        order.setmPrice(currentProduct.getmPrice());
        order.setmQuantity(1);
        cartDAO.addToCart(order,getView());
    }
}
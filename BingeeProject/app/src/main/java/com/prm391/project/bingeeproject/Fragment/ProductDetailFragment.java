package com.prm391.project.bingeeproject.Fragment;

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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.prm391.project.bingeeproject.Interface.NavigationHost;
import com.prm391.project.bingeeproject.Utils.NavigationIconClickListener;
import com.prm391.project.bingeeproject.Databases.CartDAO;
import com.prm391.project.bingeeproject.Model.Order;
import com.prm391.project.bingeeproject.Model.Product;
import com.prm391.project.bingeeproject.R;
import com.prm391.project.bingeeproject.Utils.HandleNavMenu;
import com.prm391.project.bingeeproject.Utils.HandleSearchComponent;
import com.prm391.project.bingeeproject.Utils.Utils;
import com.prm391.project.bingeeproject.databinding.FragmentProductDetailBinding;

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
    private boolean isAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabase = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance("gs://bingee-358c7.appspot.com");
        storageRef = storage.getReference("product");

        setHasOptionsMenu(true);

        Bundle bundle = this.getArguments();
        isAuth = bundle.getBoolean("isAuth");
        Log.i(TAG, "isAuth" + isAuth);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentProductDetailBinding.inflate(inflater, container, false);
        View view = mBinding.getRoot();

        setUpToolbar(view);

        HandleSearchComponent.handleSearchView(view,getActivity());

        buttonsSetUp(view);

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.findViewById(R.id.product_detail_grid).setBackgroundResource(R.drawable.corner_cut_grid_background_shape);
        }

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

        toolbar.setNavigationOnClickListener(new NavigationIconClickListener(
                getContext(),
                view.findViewById(R.id.product_detail_grid),
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
            case R.id.btn_add_to_cart:
                addToCart("addToCart");
                break;
            case R.id.btn_buy_now:
                addToCart("buyNow");
                break;
        }
        HandleNavMenu.commonNavigationMenuForCategory(v, getActivity());
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
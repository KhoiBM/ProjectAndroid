package com.prm391.project.bingeeproject.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.text.Editable;
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
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.prm391.project.bingeeproject.Adapter.CategoryCardViewHolder;
import com.prm391.project.bingeeproject.Adapter.GlideApp;
import com.prm391.project.bingeeproject.Adapter.GridItemDecoration;
import com.prm391.project.bingeeproject.Adapter.ProductCardViewHolder;
import com.prm391.project.bingeeproject.Common.LoginActivity;
import com.prm391.project.bingeeproject.Common.NavigationHost;
import com.prm391.project.bingeeproject.Common.NavigationIconClickListener;
import com.prm391.project.bingeeproject.Common.OnBoardingActivity;
import com.prm391.project.bingeeproject.Common.SplashActivity;
import com.prm391.project.bingeeproject.Dialog.LoadingDialog;
import com.prm391.project.bingeeproject.Interface.ItemClickListener;
import com.prm391.project.bingeeproject.Model.Category;
import com.prm391.project.bingeeproject.Model.Product;
import com.prm391.project.bingeeproject.R;
import com.prm391.project.bingeeproject.Utils.HandleNavMenu;
import com.prm391.project.bingeeproject.Utils.HandleSearchComponent;
import com.prm391.project.bingeeproject.Utils.Utils;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import xyz.sahildave.widget.SearchViewLayout;


public class ProductListFragment extends Fragment implements View.OnClickListener {


    private static final String TAG = ProductListFragment.class.getSimpleName();
    private FirebaseDatabase mDatabase;
    private Query product;
    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter<Product, ProductCardViewHolder> adapter;
    private LoadingDialog loadingDialog;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private StorageReference imagesRef;
    private int lastPosition;
    private Parcelable recylerViewState;
    private Timer timer;
    private String categoryID;
    private String searchKeyword;
    private boolean isAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.startLoadingDialog();

        mDatabase = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance("gs://bingee-358c7.appspot.com");
        storageRef = storage.getReference("product");

        setHasOptionsMenu(true);

        Bundle bundle = this.getArguments();
        isAuth = bundle.getBoolean("isAuth");
        Log.i(TAG, "isAuth" + isAuth);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product_list, container, false);

        setUpToolbar(view);

        HandleSearchComponent.handleSearchView(view, getActivity());

        Bundle bundle = this.getArguments();
        categoryID = bundle.getString("categoryID");
        searchKeyword = bundle.getString("searchKeyword");

        buttonsSetUp(view);

        if (!TextUtils.isEmpty(searchKeyword)) {
            HandleSearchComponent.showSearchAndSetKeyword(searchKeyword);
            product = mDatabase.getReference().child("Product").orderByChild("mName").startAt(searchKeyword.toUpperCase()).endAt(searchKeyword.toLowerCase() + "\uf8ff");

        } else if (!TextUtils.isEmpty(categoryID)) {
            product = mDatabase.getReference().child("Product").orderByChild("mCategoryID").equalTo(categoryID);

        } else {
            product = mDatabase.getReference().child("Product");
        }

        recyclerView = view.findViewById(R.id.recycler_view_product);
        recyclerView.setHasFixedSize(false);

        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);

        int largePadding = getResources().getDimensionPixelSize(R.dimen.bin_category_grid_spacing);
        int smallPadding = getResources().getDimensionPixelSize(R.dimen.bin_category_grid_spacing_small);
        recyclerView.addItemDecoration(new GridItemDecoration(largePadding, smallPadding));

        loadListProduct();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                loadingDialog.dismissDialog();
            }
        };

        timer = new Timer();
        timer.schedule(task, 1000);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.findViewById(R.id.product_list_grid).setBackgroundResource(R.drawable.corner_cut_grid_background_shape);
        }
        return view;
    }

    private void loadListProduct() {
        FirebaseRecyclerOptions<Product> options =
                new FirebaseRecyclerOptions.Builder<Product>()
                        .setQuery(product, Product.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Product, ProductCardViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ProductCardViewHolder holder, int position, @NonNull final Product model) {


                holder.productTitle.setText(model.getmName());
                holder.productPrice.setText(model.getmPrice() + "$");

                imagesRef = storageRef.child(model.getmImage());

                if (getActivity() != null) {
                    GlideApp.with(getActivity()).load(imagesRef).into(holder.productImage);
                }

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Bundle bundle = new Bundle();
                        bundle.putString("productID", adapter.getRef(position).getKey());
                        ProductDetailFragment productDetailFragment = new ProductDetailFragment();
//                        productDetailFragment.setArguments(bundle);
                        ((NavigationHost) getActivity()).navigateTo(productDetailFragment, bundle, true);
                    }
                });
                if (!TextUtils.isEmpty(holder.productTitle.getText()) & !TextUtils.isEmpty(holder.productPrice.getText())) {
                    loadingDialog.dismissDialog();
                }
            }


            @NonNull
            @Override
            public ProductCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_card, parent, false);
                return new ProductCardViewHolder(layoutView);
            }
        };
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();

    }

    private void setUpToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.app_bar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
        }

        toolbar.setNavigationOnClickListener(new NavigationIconClickListener(
                getContext(),
                view.findViewById(R.id.product_list_grid),
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
        Utils.handleOnOptionsItemSelected(item, getActivity());
        return super.onOptionsItemSelected(item);
    }

    private Button nav_btn_ingredients, nav_btn_furniture, nav_btn_go_home, nav_btn_my_account, nav_btn_cart;

    private void buttonsSetUp(View view) {

        //menu button setup

        HandleNavMenu.setupButton(this, view, nav_btn_go_home, R.id.nav_btn_go_home);
        if(TextUtils.isEmpty(categoryID)){
            HandleNavMenu.setupButton(this, view, nav_btn_ingredients, R.id.nav_btn_ingredients);
            HandleNavMenu.setupButton(this, view, nav_btn_furniture, R.id.nav_btn_furniture);
        }else {
            switch (categoryID) {
                case "1":
                    HandleNavMenu.setupButton(this, view, nav_btn_furniture, R.id.nav_btn_furniture);
                    break;
                case "2":
                    HandleNavMenu.setupButton(this, view, nav_btn_ingredients, R.id.nav_btn_ingredients);
                    break;
            }
        }
        HandleNavMenu.setupButton(this, view, nav_btn_cart, R.id.nav_btn_cart);
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
        HandleNavMenu.commonNavigationMenuForCategory(v, getActivity());
    }
}
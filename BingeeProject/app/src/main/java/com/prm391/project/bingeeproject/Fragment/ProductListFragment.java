package com.prm391.project.bingeeproject.Fragment;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.prm391.project.bingeeproject.Common.NavigationHost;
import com.prm391.project.bingeeproject.Dialog.LoadingDialog;
import com.prm391.project.bingeeproject.Interface.ItemClickListener;
import com.prm391.project.bingeeproject.Model.Category;
import com.prm391.project.bingeeproject.Model.Product;
import com.prm391.project.bingeeproject.R;

import java.util.Objects;


public class ProductListFragment extends Fragment {


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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadingDialog = new LoadingDialog(getActivity());
//        loadingDialog.startLoadingDialog();

        mDatabase = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance("gs://bingee-358c7.appspot.com");
        storageRef = storage.getReference("product");

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product_list, container, false);

        setUpToolbar(view);

        Bundle bundle = this.getArguments();
        String categoryID = bundle.getString("categoryID");

        product = mDatabase.getReference().child("Product").orderByChild("mCategoryID").equalTo(categoryID);

        recyclerView = view.findViewById(R.id.recycler_view_product);
        recyclerView.setHasFixedSize(false);

        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);

        int largePadding = getResources().getDimensionPixelSize(R.dimen.bin_category_grid_spacing);
        int smallPadding = getResources().getDimensionPixelSize(R.dimen.bin_category_grid_spacing_small);
        recyclerView.addItemDecoration(new GridItemDecoration(largePadding, smallPadding));

        loadListProduct();

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

                if (getActivity()!=null){
                    GlideApp.with(getActivity()).load(imagesRef).into(holder.productImage);
                }

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Bundle bundle = new Bundle();
                        bundle.putString("productID", adapter.getRef(position).getKey());
                        ProductDetailFragment productDetailFragment = new ProductDetailFragment();
//                        productDetailFragment.setArguments(bundle);
                        ((NavigationHost) getActivity()).navigateTo(productDetailFragment, bundle,true);
                    }
                });
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
                Bundle bundle = new Bundle();
                ((NavigationHost) getActivity()).navigateTo(new CartFragment(),bundle, true);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
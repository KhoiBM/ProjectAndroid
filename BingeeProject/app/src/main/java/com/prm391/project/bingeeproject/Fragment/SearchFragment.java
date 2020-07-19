package com.prm391.project.bingeeproject.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.prm391.project.bingeeproject.Adapter.GlideApp;
import com.prm391.project.bingeeproject.Adapter.GridItemDecoration;
import com.prm391.project.bingeeproject.Adapter.ProductCardViewHolder;
import com.prm391.project.bingeeproject.Common.NavigationHost;
import com.prm391.project.bingeeproject.Dialog.LoadingDialog;
import com.prm391.project.bingeeproject.Interface.ItemClickListener;
import com.prm391.project.bingeeproject.Model.Product;
import com.prm391.project.bingeeproject.R;
import com.prm391.project.bingeeproject.Utils.HandleSearchComponent;
import com.prm391.project.bingeeproject.Utils.Utils;

import java.util.Timer;
import java.util.TimerTask;


public class SearchFragment extends Fragment {


    private static final String TAG = SearchFragment.class.getSimpleName();
    private FirebaseDatabase mDatabase;
    private Query product;
    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter<Product, ProductCardViewHolder> adapter;
    private LoadingDialog loadingDialog;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private StorageReference imagesRef;
    ;
    private Timer timer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.startLoadingDialog();

        mDatabase = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance("gs://bingee-358c7.appspot.com");
        storageRef = storage.getReference("product");


    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        product = mDatabase.getReference().child("Product");

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

}
package com.prm391.project.bingeeproject.Fragment;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.prm391.project.bingeeproject.Adapter.GridItemDecoration;
import com.prm391.project.bingeeproject.Adapter.ProductCardViewHolder;
import com.prm391.project.bingeeproject.Common.NavigationHost;
import com.prm391.project.bingeeproject.Dialog.LoadingDialog;
import com.prm391.project.bingeeproject.Interface.ItemClickListener;
import com.prm391.project.bingeeproject.Model.Category;
import com.prm391.project.bingeeproject.Model.Product;
import com.prm391.project.bingeeproject.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProductListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product_list, container, false);

        setUpToolbar(view);

        Bundle bundle = this.getArguments();
        String categoryID = bundle.getString("categoryID");


        product = mDatabase.getReference().child("Product").orderByChild("mCategoryID").equalTo(categoryID);
        recyclerView = view.findViewById(R.id.recycler_view_product);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false));
        int largePadding = getResources().getDimensionPixelSize(R.dimen.bin_category_grid_spacing);
        int smallPadding = getResources().getDimensionPixelSize(R.dimen.bin_category_grid_spacing_small);
        recyclerView.addItemDecoration(new GridItemDecoration(largePadding, smallPadding));
        loadProduct();

        return view;
    }

    private void loadProduct() {
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
                imagesRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downUri = task.getResult();
                            String imageUrl = downUri.toString();
                            Glide.with(holder.productImage.getContext()).load(imageUrl).into(holder.productImage);
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
//                Glide.with(holder.productImage.getContext()).load(model.getmImage()).into(holder.productImage);
                loadingDialog.dismissDialog();
                final Product clickItem = model;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Toast.makeText(getContext(), "productName: " + clickItem.getmName() + "/ keyProduct" + adapter.getRef(position).getKey(), Toast.LENGTH_SHORT).show();
                        Bundle bundle = new Bundle();
                        bundle.putString("productID", adapter.getRef(position).getKey());
                        ProductDetailFragment productDetailFragment = new ProductDetailFragment();
                        productDetailFragment.setArguments(bundle);
                        ((NavigationHost) getActivity()).navigateTo(productDetailFragment, true);

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
}
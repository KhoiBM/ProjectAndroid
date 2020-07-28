package com.prm391.project.bingeeproject.Adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.prm391.project.bingeeproject.Interface.NavigationHost;
import com.prm391.project.bingeeproject.Databases.CartDAO;
import com.prm391.project.bingeeproject.Fragment.ProductDetailFragment;
import com.prm391.project.bingeeproject.Interface.ItemClickListener;
import com.prm391.project.bingeeproject.Model.Order;
import com.prm391.project.bingeeproject.Model.Product;
import com.prm391.project.bingeeproject.Utils.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import it.sephiroth.android.library.numberpicker.NumberPicker;

import static it.sephiroth.android.library.numberpicker.NumberPicker.*;

public class ItemCardRecyclerViewAdapter<T, C extends Fragment> extends RecyclerView.Adapter<ItemCardViewHolder> {

    private List<T> carts;
    private View snackbarLayout;
    private FragmentActivity activity;
    private CartDAO<C> cartDAO;
    private StorageReference storageRef;
    private FirebaseDatabase mDatabase;
    private DatabaseReference product;
    private View view;
    private C fragment;
    private int layout;

    public ItemCardRecyclerViewAdapter(View view, FragmentActivity activity, List<T> carts, View snackbarLayout, StorageReference storageRef, C fragment, int layout) {
        this.carts = carts;
        this.snackbarLayout = snackbarLayout;
        this.activity = activity;
        cartDAO = new CartDAO(activity);
        this.storageRef = storageRef;
        this.view = view;
        this.fragment = fragment;
        this.layout = layout;
    }

    @NonNull
    @Override
    public ItemCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new ItemCardViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemCardViewHolder holder, int position) {
        if (carts != null && position < carts.size()) {
            final Order order = (Order) carts.get(position);
            mDatabase = FirebaseDatabase.getInstance();
            product = mDatabase.getReference().child("Product").child(order.getmProductId());
            product.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Product currentProduct = snapshot.getValue(Product.class);
                        StorageReference imagesRef = storageRef.child(snapshot.child("mImage").getValue(String.class));
                        if (activity != null) {
                            GlideApp.with(holder.productImage.getContext()).load(imagesRef).into(holder.productImage);
                        }
                    } else {
                        Utils.showSnackbarWithNoAction(view, "Product does not exist!");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Utils.showSnackbarWithNoAction(view, error.getMessage());
                }
            });

            holder.productTitle.setText(order.getmProductName());
            holder.productPrice.setText(String.valueOf(order.getmPrice()) + "$");

            holder.quantity.setProgress(Integer.valueOf(order.getmQuantity()));
            if (snackbarLayout != null) {
                holder.quantity.setNumberPickerChangeListener(new OnNumberPickerChangeListener() {

                    @Override
                    public void onProgressChanged(@NotNull NumberPicker numberPicker, int i, boolean b) {
//                    Utils.showSnackbarWithNoAction(snackbarLayout, "onProgressChanged");
                        updateQuantity(order, snackbarLayout, view, holder.quantity, fragment);
                    }

                    @Override
                    public void onStartTrackingTouch(@NotNull NumberPicker numberPicker) {
                        updateQuantity(order, snackbarLayout, view, holder.quantity, fragment);
                    }

                    @Override
                    public void onStopTrackingTouch(@NotNull NumberPicker numberPicker) {
                        updateQuantity(order, snackbarLayout, view, holder.quantity, fragment);
                    }
                });

                holder.setDeleteItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        CartDAO cartDAO = new CartDAO(activity);
                        cartDAO.delete(order.getmProductId(), order.getmProductName(), snackbarLayout, view, "deleteItemCart");
                    }
                });
            }
            holder.setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int position, boolean isLongClick) {
                    Bundle bundle = new Bundle();
                    bundle.putString("productID", order.getmProductId());
                    ProductDetailFragment productDetailFragment = new ProductDetailFragment();
//                    productDetailFragment.setArguments(bundle);
                    ((NavigationHost) activity).navigateTo(productDetailFragment, bundle, true);
                }
            });
        }
    }

    private void updateQuantity(Order order, View snackbarLayout, View view, NumberPicker quantity, C c) {
        String productId = order.getmProductId();
        String productName = order.getmProductName();
        cartDAO.updateQuantity(snackbarLayout, view, productId, productName, quantity.getProgress(), c);
    }


    @Override
    public int getItemCount() {
        return carts.size();
    }


}

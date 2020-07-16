package com.prm391.project.bingeeproject.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prm391.project.bingeeproject.Model.Order;
import com.prm391.project.bingeeproject.Model.Product;
import com.prm391.project.bingeeproject.R;

import java.util.List;

public class ItemCardRecyclerViewAdapter<T> extends RecyclerView.Adapter<ItemCardViewHolder> {

    private List<T> carts;

    public ItemCardRecyclerViewAdapter(List<T> carts) {
        this.carts = carts;
    }

    @NonNull
    @Override
    public ItemCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_card, parent, false);
        return new ItemCardViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemCardViewHolder holder, int position) {
        if (carts != null && position < carts.size()) {
            Order order = (Order) carts.get(position);
            holder.productTitle.setText(order.getmProductName());
            holder.productPrice.setText(String.valueOf(order.getmPrice()));
            holder.quantity.getEditText().setText(String.valueOf(order.getmQuantity()));
        }
    }

    @Override
    public int getItemCount() {
        return carts.size();
    }
}

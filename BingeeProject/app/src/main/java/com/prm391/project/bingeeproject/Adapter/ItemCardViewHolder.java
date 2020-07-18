package com.prm391.project.bingeeproject.Adapter;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prm391.project.bingeeproject.Interface.ItemClickListener;
import com.prm391.project.bingeeproject.R;

import it.sephiroth.android.library.numberpicker.NumberPicker;

public class ItemCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public ImageView productImage;
    public TextView productTitle, productPrice;
    public NumberPicker quantity;
    public Button delete;
    public ItemClickListener deleteItemClickListener, itemClickListener;

    public ItemCardViewHolder(@NonNull View itemView) {
        super(itemView);
        productImage = itemView.findViewById(R.id.product_image);
        productTitle = itemView.findViewById(R.id.product_title);
        productPrice = itemView.findViewById(R.id.product_price);
        quantity = itemView.findViewById(R.id.number_picker_quantity);
        delete = itemView.findViewById(R.id.btn_delete_item);
        delete.setOnClickListener(this);
        productTitle.setOnClickListener(this);
    }

    public void setDeleteItemClickListener(ItemClickListener deleteItemClickListener) {
        this.deleteItemClickListener = deleteItemClickListener;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_delete_item:
                deleteItemClickListener.onClick(v, getAdapterPosition(), false);
                break;
            case R.id.product_title:
                itemClickListener.onClick(v, getAdapterPosition(), false);
                break;
        }
    }
}

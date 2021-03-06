package com.prm391.project.bingeeproject.Adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prm391.project.bingeeproject.Interface.ItemClickListener;
import com.prm391.project.bingeeproject.R;

public class ProductCardViewHolder  extends RecyclerView.ViewHolder implements View.OnClickListener{

    public ImageView productImage;
    public TextView productTitle;
    public TextView productPrice;
    private ItemClickListener itemClickListener;
    public ProductCardViewHolder(@NonNull View itemView) {
        super(itemView);
        productImage=itemView.findViewById(R.id.product_image);
        productTitle=itemView.findViewById(R.id.product_title);
        productPrice=itemView.findViewById(R.id.product_price);
        itemView.setOnClickListener(this);
    }
    public void setItemClickListener(ItemClickListener itemClickListener)
    {
        this.itemClickListener = itemClickListener;
    }
    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition(),false);
    }
}
package com.prm391.project.bingeeproject.Adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prm391.project.bingeeproject.Interface.ItemClickListener;
import com.prm391.project.bingeeproject.R;

public class CategoryCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public ImageView categoryImage;
    public TextView categoryTitle;
    private ItemClickListener itemClickListener;
    public CategoryCardViewHolder(@NonNull View itemView) {
        super(itemView);
        categoryImage=itemView.findViewById(R.id.category_image);
        categoryTitle=itemView.findViewById(R.id.category_title);

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

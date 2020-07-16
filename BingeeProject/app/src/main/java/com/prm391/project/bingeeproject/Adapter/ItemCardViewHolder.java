package com.prm391.project.bingeeproject.Adapter;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import com.prm391.project.bingeeproject.R;

public class ItemCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

//    public ImageView productImage;
    public TextView productTitle, productPrice;
    public TextInputLayout quantity;
    public Button delete;

    public ItemCardViewHolder(@NonNull View itemView) {
        super(itemView);
//        productImage = itemView.findViewById(R.id.product_image);
        productTitle = itemView.findViewById(R.id.product_title);
        productPrice = itemView.findViewById(R.id.product_price);
        quantity = itemView.findViewById(R.id.text_input_quantity);
        delete = itemView.findViewById(R.id.btn_delete_item);
        delete.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_delete_item:
             break;
        }
    }
}

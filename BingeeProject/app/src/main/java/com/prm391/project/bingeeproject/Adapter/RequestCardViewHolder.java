package com.prm391.project.bingeeproject.Adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prm391.project.bingeeproject.Interface.ItemClickListener;
import com.prm391.project.bingeeproject.R;

public class RequestCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView ordinalNumbers,title,date;
    private ItemClickListener itemClickListener;

    public RequestCardViewHolder(@NonNull View itemView) {
        super(itemView);
        ordinalNumbers=itemView.findViewById(R.id.request_ordinal_numbers);
        title=itemView.findViewById(R.id.request_title);
        date=itemView.findViewById(R.id.request_date);
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

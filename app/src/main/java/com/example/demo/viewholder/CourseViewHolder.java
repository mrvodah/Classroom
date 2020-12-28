package com.example.demo.viewholder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.demo.R;
import com.example.demo.interface1.OnClickListener;

public class CourseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView name;
    public TextView type;

    OnClickListener itemClickListener;

    public CourseViewHolder(View itemView) {
        super(itemView);

        name = itemView.findViewById(R.id.tv_name);
        type = itemView.findViewById(R.id.tv_type);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(OnClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);
    }
}

package com.example.demo.viewholder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.demo.R;
import com.example.demo.interface1.OnClickListener;

public class LessonViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView name, description;
    public TextView author;

    OnClickListener itemClickListener;

    public LessonViewHolder(View itemView) {
        super(itemView);

        name = itemView.findViewById(R.id.tv_name);
        description = itemView.findViewById(R.id.tv_description);
        author = itemView.findViewById(R.id.tv_author);

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

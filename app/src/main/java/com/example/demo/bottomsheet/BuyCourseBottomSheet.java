package com.example.demo.bottomsheet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.example.demo.R;
import com.example.demo.common.Common;
import com.example.demo.database.Course;
import com.example.demo.interface1.OnClickListener;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BuyCourseBottomSheet extends CustomBottomSheetDialogFragment {
    private static BuyCourseBottomSheet instance;
    @BindView(R.id.iv_image)
    AppCompatImageView ivImage;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_price)
    TextView tvPrice;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;

    public static BuyCourseBottomSheet getInstance(Course item) {
        if (instance == null) {
            instance = new BuyCourseBottomSheet();
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", item);
        instance.setArguments(bundle);
        return instance;
    }

    private Course course;
    private OnClickListener listener;

    public void setListener(OnClickListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_buy_course, container, false);
        ButterKnife.bind(this, v);

        course = (Course) getArguments().getSerializable("data");

        if (course.image != null) {
            Picasso.get().load(course.image).into(ivImage);
        }

        tvName.setText(course.name);
        tvPrice.setText(Common.getMoney(course.price));
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v, 0, false);
            }
        });

        return v;
    }
}

package com.example.demo;

import android.content.DialogInterface;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.example.demo.bottomsheet.BuyCourseBottomSheet;
import com.example.demo.common.Common;
import com.example.demo.database.Course;
import com.example.demo.interface1.OnClickListener;
import com.example.demo.viewholder.CourseViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Courses extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference list;
    DatabaseReference license;

    FirebaseRecyclerAdapter<Course, CourseViewHolder> adapter;
    @BindView(R.id.rcv_course)
    RecyclerView rcvCourse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        database = FirebaseDatabase.getInstance();
        list = database.getReference("List");
        license = database.getReference("License").child(Common.getEmail(Common.currentUser.email));

        if (getIntent().hasExtra("buyCourse")) {
            loadCourses(license.orderByKey());
        } else {
            loadCourses(list.orderByChild("type").equalTo(getIntent().getStringExtra("type")));
        }
    }

    private void loadCourses(Query query) {
        adapter = new FirebaseRecyclerAdapter<Course, CourseViewHolder>(
                Course.class,
                R.layout.item_course,
                CourseViewHolder.class,
                query) {
            @Override
            protected void populateViewHolder(CourseViewHolder viewHolder, Course model, int position) {
                viewHolder.name.setText(model.name);
                viewHolder.type.setText(model.type + " - " + Common.getMoney(model.price));

                viewHolder.setItemClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        license = database.getReference("License/" + Common.getEmail(Common.currentUser.email) + "/" + adapter.getRef(position).getKey());
                        license.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    Intent intent = new Intent(Courses.this, LessonScreen.class);
                                    intent.putExtra("courseId", adapter.getRef(position).getKey());
                                    intent.putExtra("email", Common.getEmail(model.email));
                                    startActivity(intent);
                                } else {
                                    BuyCourseBottomSheet buyCourseBottomSheet = BuyCourseBottomSheet.getInstance(model);
                                    buyCourseBottomSheet.setListener(new OnClickListener() {
                                        @Override
                                        public void onClick(View view, int position, boolean isLongClick) {
                                            buyCourseBottomSheet.dismiss();
                                            license.setValue(model);
                                            Toast.makeText(Courses.this, "Mua khóa học thành công!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    buyCourseBottomSheet.show(Courses.this.getSupportFragmentManager(), "tag");
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                });
            }
        };

        adapter.notifyDataSetChanged();
        rcvCourse.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}

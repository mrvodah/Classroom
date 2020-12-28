package com.example.demo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.demo.common.Common;
import com.example.demo.database.Lesson;
import com.example.demo.interface1.OnClickListener;
import com.example.demo.viewholder.LessonViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LessonScreen extends AppCompatActivity {

    @BindView(R.id.rcv_lesson)
    RecyclerView rcvLesson;

    FirebaseDatabase database;
    DatabaseReference lessons;

    FirebaseRecyclerAdapter<Lesson, LessonViewHolder> adapter;

    MaterialEditText name, link, content;
    Button confirm;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_screen);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (!Common.currentUser.teacher) {
            fab.setVisibility(View.GONE);
        }

        database = FirebaseDatabase.getInstance();
        if (Common.currentUser.teacher) {
            lessons = database.getReference("Lesson").child(Common.getEmail(Common.currentUser.email)).child(getIntent().getStringExtra("courseId"));
        } else {
            lessons = database.getReference("Lesson").child(getIntent().getStringExtra("email")).child(getIntent().getStringExtra("courseId"));
        }


        loadLessons();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadLessons() {
        adapter = new FirebaseRecyclerAdapter<Lesson, LessonViewHolder>(
                Lesson.class,
                R.layout.item_lesson,
                LessonViewHolder.class,
                lessons.orderByKey()
        ) {
            @Override
            protected void populateViewHolder(LessonViewHolder viewHolder, final Lesson model, int position) {
                viewHolder.name.setText(model.name);
                viewHolder.description.setText(model.link);

                viewHolder.setItemClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent intent = new Intent(LessonScreen.this, DetailScreen.class);
                        intent.putExtra("link", model.link);
                        startActivity(intent);
                    }
                });
            }
        };

        adapter.notifyDataSetChanged();
        rcvLesson.setAdapter(adapter);
    }

    @OnClick(R.id.fab)
    public void onViewClicked() {
        showAddLessonDialog();
    }

    private void showAddLessonDialog() {
        LayoutInflater inflater = this.getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_add_lesson, null);

        name = v.findViewById(R.id.edtName);
        link = v.findViewById(R.id.edtAmount);
        content = v.findViewById(R.id.edtContent);
        confirm = v.findViewById(R.id.btn_confirm);

        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Add Course")
                .setView(v)
                .create();
        alertDialog.show();

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();

                Lesson lesson = new Lesson(
                        name.getText().toString(),
                        link.getText().toString(),
                        content.getText().toString()
                );

                lessons.push().setValue(lesson);
                Toast.makeText(LessonScreen.this, "Course register successfully!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
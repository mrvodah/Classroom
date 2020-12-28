package com.example.demo;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.demo.common.Common;
import com.example.demo.database.Course;
import com.example.demo.interface1.OnClickListener;
import com.example.demo.viewholder.CourseViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.Button;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    LinearLayout profile;
    TextView profilename, profilemail;

    FirebaseDatabase database;
    DatabaseReference courses;
    DatabaseReference list;
    FirebaseStorage storage;
    StorageReference storageReference;

    FirebaseRecyclerAdapter<Course, CourseViewHolder> adapter;
    @BindView(R.id.rcv_course)
    RecyclerView rcvCourse;

    MaterialEditText name, amount, content;
    Spinner type;
    Button confirm, select, upload;
    @BindView(R.id.ln_course)
    LinearLayout lnCourse;
    @BindView(R.id.ln_category)
    TextView lnCategory;
    @BindView(R.id.cv_category)
    CardView cvCategory;

    Uri saveUri;
    String linkUri = "";
    public final int PICK_IMAGE_REQUEST = 11;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddCourseDialog();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        profile = header.findViewById(R.id.profile);
        profile.setOnClickListener(this);

        profilename = header.findViewById(R.id.profilename);
        profilemail = header.findViewById(R.id.profilemail);

        profilename.setText(Common.currentUser.fullName);
        profilemail.setText(Common.currentUser.email);
        database = FirebaseDatabase.getInstance();
        list = database.getReference("List");

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        if (Common.currentUser.teacher) {
            lnCategory.setVisibility(View.GONE);
            cvCategory.setVisibility(View.GONE);

            courses = database.getReference("Course").child(Common.getEmail(Common.currentUser.email));

            loadCourses();
        } else {
            lnCourse.setVisibility(View.GONE);
            rcvCourse.setVisibility(View.GONE);
            fab.setVisibility(View.GONE);
        }
    }

    private void showAddCourseDialog() {
        LayoutInflater inflater = this.getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_add_course, null);

        name = v.findViewById(R.id.edtName);
        amount = v.findViewById(R.id.edtAmount);
        content = v.findViewById(R.id.edtContent);
        type = v.findViewById(R.id.sp_type);
        confirm = v.findViewById(R.id.btn_confirm);
        select = v.findViewById(R.id.btn_add_image);
        upload = v.findViewById(R.id.btn_upload_image);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,
                Common.types);
        type.setAdapter(adapter);

        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Add Course")
                .setView(v)
                .create();
        alertDialog.show();

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();

                Course course = new Course(
                        name.getText().toString(),
                        Integer.valueOf(amount.getText().toString()),
                        type.getSelectedItem().toString(),
                        Common.currentUser.email,
                        content.getText().toString()
                );
                if (!linkUri.isEmpty()) {
                    course.setImage(linkUri);
                }

                String timeInterval = String.valueOf(System.currentTimeMillis() / 1000);
                courses.child(timeInterval).setValue(course);
                list.child(timeInterval).setValue(course);
                Toast.makeText(Home.this, "Course register successfully!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST &&
                resultCode == RESULT_OK
                && data != null &&
                data.getData() != null) {
            saveUri = data.getData();
            select.setText("Image Selected");
        }
    }

    private void uploadImage() {
        if (saveUri != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Uploading ...");
            progressDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/" + imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(Home.this, "Uploaded !", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    linkUri = uri.toString();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(Home.this, "Upload failure ... " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded: " + progress + "%");
                        }
                    });
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void loadCourses() {
        adapter = new FirebaseRecyclerAdapter<Course, CourseViewHolder>(
                Course.class,
                R.layout.item_course,
                CourseViewHolder.class,
                courses.orderByKey()
        ) {
            @Override
            protected void populateViewHolder(CourseViewHolder viewHolder, Course model, int position) {
                viewHolder.name.setText(model.name);
                viewHolder.type.setText(model.type + " - " + Common.getMoney(model.price));

                viewHolder.setItemClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent intent = new Intent(Home.this, LessonScreen.class);
                        intent.putExtra("courseId", adapter.getRef(position).getKey());
                        startActivity(intent);
                    }
                });
            }
        };

        adapter.notifyDataSetChanged();
        rcvCourse.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_courses) {
            Intent intent = new Intent(getApplicationContext(), Courses.class);
            startActivity(intent);
        } else if (id == R.id.nav_events) {
            Intent intent = new Intent(getApplicationContext(), Events.class);
            startActivity(intent);
        } else if (id == R.id.nav_lectures) {
            Intent intent = new Intent(getApplicationContext(), Lectures.class);
            startActivity(intent);
        } else if (id == R.id.nav_announcements) {
            Intent intent = new Intent(getApplicationContext(), Announcements.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(getApplicationContext(), Settings.class);
            startActivity(intent);

        } else if (id == R.id.nav_logout) {
            Intent intent = new Intent(getApplicationContext(), LoginScreen.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_rate) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.profile) {
            Intent intent = new Intent(getApplicationContext(), MyProfile.class);
            startActivity(intent);
        }
    }

    @OnClick({R.id.ln_english, R.id.ln_math, R.id.ln_physical})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ln_english:
                openCourse("English");
                break;
            case R.id.ln_math:
                openCourse("Math");
                break;
            case R.id.ln_physical:
                openCourse("Physical");
                break;
        }
    }

    private void openCourse(String type) {
        Intent intent = new Intent(getApplicationContext(), Courses.class);
        intent.putExtra("type", type);
        startActivity(intent);
    }
}

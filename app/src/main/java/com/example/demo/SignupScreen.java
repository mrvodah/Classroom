package com.example.demo;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;

import android.widget.EditText;
import android.widget.Toast;

import com.example.demo.database.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignupScreen extends AppCompatActivity {

    @BindView(R.id.fullname)
    EditText fullname;
    @BindView(R.id.email)
    EditText email;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.cb_is_teacher)
    AppCompatCheckBox cbIsTeacher;

    FirebaseDatabase database;
    DatabaseReference table_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_screen);
        ButterKnife.bind(this);

        database = FirebaseDatabase.getInstance();
        table_user = database.getReference("User");
    }

    @OnClick(R.id.sign_up)
    public void onViewClicked() {
        final String newEmail = email.getText().toString().replace('.', '1');
        table_user.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(newEmail).exists())
                    Toast.makeText(SignupScreen.this, "Email already register!", Toast.LENGTH_SHORT).show();
                else {
                    User user = new User(
                            fullname.getText().toString(),
                            email.getText().toString(),
                            password.getText().toString(),
                            cbIsTeacher.isChecked()
                    );

                    table_user.child(newEmail).setValue(user);
                    table_user.removeEventListener(this);
                    Toast.makeText(SignupScreen.this, "Email register successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

package com.example.demo;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.demo.common.Common;
import com.example.demo.database.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;


public class LoginScreen extends AppCompatActivity implements View.OnClickListener {

    Button LoginScreen;
    TextView signUp;
    @BindView(R.id.email)
    EditText email;
    @BindView(R.id.password)
    EditText password;

    FirebaseDatabase database;
    DatabaseReference table_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        ButterKnife.bind(this);

        LoginScreen = findViewById(R.id.sign_in);
        signUp = findViewById(R.id.sign_up);
        LoginScreen.setOnClickListener(this);
        signUp.setOnClickListener(this);

        database = FirebaseDatabase.getInstance();
        table_user = database.getReference("User");
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.sign_in) {
            table_user.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    //check if user not exist in database
                    String newEmail = email.getText().toString().replace('.', '1');
                    if (dataSnapshot.child(newEmail).exists()) {
                        // get user information
                        User user = dataSnapshot.child(newEmail).getValue(User.class);

                        if (user.getPassword().equals(password.getText().toString())) {
                            Common.currentUser = user;
                            Toast.makeText(LoginScreen.this, "Login successfully!", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(getApplicationContext(), Home.class);
                            startActivity(intent);
                            finish();
                            table_user.removeEventListener(this);

                        } else {
                            Toast.makeText(LoginScreen.this, "Login failed!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginScreen.this, "User not exists", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
          
        } else if (view.getId() == R.id.sign_up) {
            Intent intent = new Intent(getApplicationContext(), SignupScreen.class);
            startActivity(intent);
        }
    }
}

package com.ok.flex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountActivity extends AppCompatActivity {


    Toolbar toolbar;
    CircleImageView user_profile_image;
    TextView username, email;

    TextView txt_your_channel, txt_setting,txt_help;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference reference;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

       init();

       auth = FirebaseAuth.getInstance();
       user = auth.getCurrentUser();
       reference = FirebaseDatabase.getInstance().getReference();

       getData();
    }

    private void getData()
    {
        reference.child("Users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    String n = Objects.requireNonNull(snapshot.child("username").getValue()).toString();
                    String e = Objects.requireNonNull(snapshot.child("email").getValue()).toString();
                    String p = Objects.requireNonNull(snapshot.child("profile").getValue()).toString();

                    username.setText(n);
                    email.setText(e);


                    try {
                        Picasso.get().load(p).placeholder(R.drawable.baseline_account_circle_24).into(user_profile_image);
                    }catch (Exception exception){
                        exception.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(AccountActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void init()
    {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> finish());

        user_profile_image = findViewById(R.id.user_profile_image);
        username = findViewById(R.id.user_channel_name);
        txt_setting = findViewById(R.id.txt_setting);
        txt_help = findViewById(R.id.txt_help);
    }
}
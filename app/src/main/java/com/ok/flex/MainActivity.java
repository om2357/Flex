package com.ok.flex;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ok.flex.fragment.ExploreFragment;
import com.ok.flex.fragment.HistoryFragment;
import com.ok.flex.fragment.HomeFragment;
import com.ok.flex.fragment.MusicFragment;
import com.ok.flex.fragment.VideoFragment;

import java.util.HashMap;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    BottomNavigationView bottomNavigationView;
    FrameLayout frameLayout;
    FirebaseAuth auth;
    FirebaseUser user;
    ImageView userprofile_image;
    GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setTitle("");

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        frameLayout = findViewById(R.id.frame_layout);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        userprofile_image = findViewById(R.id.user_profile_image);
        GoogleSignInOptions gsc = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(MainActivity.this, gsc);


            bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {

            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId())
                {
                    case R.id.home:
                        HomeFragment homeFragment = new HomeFragment();
                        selectedFragment(homeFragment);
                        break;

                    case R.id.explore:
                        ExploreFragment exploreFragment = new ExploreFragment();
                        selectedFragment(exploreFragment);
                        break;

                    case R.id.local_Video:
                        VideoFragment videoFragment = new VideoFragment();
                        selectedFragment(videoFragment);
                        break;

                    case R.id.local_music:
                        MusicFragment musicFragment = new MusicFragment();
                        selectedFragment(musicFragment);
                        break;

                    case R.id.history:
                        HistoryFragment historyFragment = new HistoryFragment();
                        selectedFragment(historyFragment);
                        break;
                }

                return false;
            }

            private void selectedFragment(Fragment fragment) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout,fragment);
                fragmentTransaction.commit();
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.home);

        userprofile_image.setOnClickListener(view -> {
            if(user!= null)
            {
                Toast.makeText(MainActivity.this, "User Already Sign In", Toast.LENGTH_SHORT).show();
            }
            else {
                showDialog();
            }
        });
    }

    private void showDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(true);

        ViewGroup viewGroup = findViewById(android.R.id.content);
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_signin_dialogue, viewGroup, false);
        builder.setView(view);

        TextView txt_google_signIn = view.findViewById(R.id.txt_google_signin);
        txt_google_signIn.setOnClickListener(view1 -> signIn());
        builder.create().show();
    }

    private void signIn() {

        Intent intent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN)
        {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
                auth.signInWithCredential(credential).addOnCompleteListener(task1 -> {
                    if(task1.isSuccessful())
                    {
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("username", account.getDisplayName());
                        map.put("email", account.getEmail());
                        map.put("profile", String.valueOf(account.getPhotoUrl()));
                        assert firebaseUser != null;
                        map.put("uid", firebaseUser.getUid());
                        map.put("search", Objects.requireNonNull(account.getDisplayName()).toLowerCase());


                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("User");
                        reference.child(firebaseUser.getUid()).setValue(map);
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, Objects.requireNonNull(task1.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }

                });
            }catch (Exception e)
            {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.notification:
                Toast.makeText(this, "Notification", Toast.LENGTH_SHORT).show();
                break;

            case R.id.search:
                Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show();

//            case R.id.Account:
//                Toast.makeText(this, "Account", Toast.LENGTH_SHORT).show();

            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }
}
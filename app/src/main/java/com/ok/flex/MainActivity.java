package com.ok.flex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ok.flex.fragment.ExploreFragment;
import com.ok.flex.fragment.HistoryFragment;
import com.ok.flex.fragment.HomeFragment;
import com.ok.flex.fragment.MusicFragment;
import com.ok.flex.fragment.VideoFragment;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    BottomNavigationView bottomNavigationView;
    FrameLayout frameLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setTitle("");

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        frameLayout = findViewById(R.id.frame_layout);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
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
        }
        return super.onOptionsItemSelected(item);
    }
}
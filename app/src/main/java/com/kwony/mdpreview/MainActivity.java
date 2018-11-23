package com.kwony.mdpreview;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.kwony.mdpreview.Tabs.Pager.MainPagerAdapter;
import com.kwony.mdpreview.Tabs.Pager.MainViewPager;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    MainViewPager viewPager;

    private ImageButton ibShare;
    private ImageButton ibOpen;
    private ImageButton ibSave;

    private final static int ASK_OPEN_PERMISSION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionbarSetup();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                case R.id.action_markdown:
                    viewPager.setCurrentItem(0);
                    break;
                case R.id.action_recent:
                    viewPager.setCurrentItem(1);
                    break;
                case R.id.action_theme:
                    viewPager.setCurrentItem(2);
                    break;
                }
                return true;
            }
        });

        viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(new MainPagerAdapter(getSupportFragmentManager()));
    }

    private void actionbarSetup() {
        View v;

        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar_custom);
        v = getSupportActionBar().getCustomView();

        ibShare = v.findViewById(R.id.ib_share);
        ibOpen = v.findViewById(R.id.ib_open);
        ibSave = v.findViewById(R.id.ib_save);

        ibShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        ibOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[] {
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        ASK_OPEN_PERMISSION);
            }
        });

        ibSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch(requestCode) {
        case ASK_OPEN_PERMISSION:
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(getApplicationContext(), R.string.toast_unable_contiue_without_perm,
                            Toast.LENGTH_LONG).show();
                    return;
                }
            }

            break;
        }
    }
}

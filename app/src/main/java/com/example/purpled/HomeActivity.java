package com.example.purpled;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ui.AppBarConfiguration;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Paper.init(this);

        mToolbar = findViewById(R.id.nav_action);
        mToolbar.setTitle(R.string.app_name);
        setSupportActionBar(mToolbar);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(HomeActivity.this, mDrawerLayout, R.string.nav_open, R.string.nav_close);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.home:
                Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
                break;

            case R.id.logout:
                finish();
                break;

        }
        return false;
    }
}
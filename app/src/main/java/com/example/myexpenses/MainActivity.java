package com.example.myexpenses;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private NavigationView mNavigationView;
    private FirebaseAuth mAuth;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        fab = findViewById(R.id.fab);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mToolbar = findViewById(R.id.toolbar);
        mNavigationView = findViewById(R.id.navigation_view);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("MyExpenses App");

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MainActivity.this, mDrawerLayout, mToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(this);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HomeFragment.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.category:
                Intent categoryIntent = new Intent(MainActivity.this, AddCategory.class);
                startActivity(categoryIntent);
                break;

            case R.id.logout:
                logout();
                break;

            case R.id.alarm:
                Intent alarmIntent = new Intent(MainActivity.this, AlarmNotificationFragment.class);
                startActivity(alarmIntent);
                break;

            case R.id.profile:
                Intent profileIntent = new Intent(MainActivity.this, ProfileFragment.class);
                startActivity(profileIntent);
                break;

        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout(){
        mAuth.signOut();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }


}
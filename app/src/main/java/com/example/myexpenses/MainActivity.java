package com.example.myexpenses;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private NavigationView mNavigationView;
    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;
    private FloatingActionButton fab;
    private ArrayList<ModelExpenseRecord> expenseRecordArrayList;
    private AdapterExpenseRecord adapterExpenseRecord;
    private RecyclerView recyclerViewER;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        fab = findViewById(R.id.fab);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mToolbar = findViewById(R.id.toolbar);
        mNavigationView = findViewById(R.id.navigation_view);
        recyclerViewER = findViewById(R.id.recyclerViewER);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("MyExpenses App");

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MainActivity.this, mDrawerLayout, mToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(this);

        recyclerViewER.setHasFixedSize(true);
        recyclerViewER.setLayoutManager(new LinearLayoutManager(this));

        expenseRecordArrayList = new ArrayList<>();
        adapterExpenseRecord = new AdapterExpenseRecord(expenseRecordArrayList,MainActivity.this);
        userID = mAuth.getCurrentUser().getUid();
        recyclerViewER.setAdapter(adapterExpenseRecord);

        loadExpenseRecords();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HomeFragment.class);
                startActivity(intent);
            }
        });
    }

    private void loadExpenseRecords() {
        fStore.collection("User Record Information").document(userID)
                .collection("Records").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                expenseRecordArrayList.clear();

                for (int i=0; i<queryDocumentSnapshots.size(); i++){
                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(i);
                    if (documentSnapshot.exists()){
                        String id = documentSnapshot.getId();
                        String image = documentSnapshot.getString("Image");
                        String title = documentSnapshot.getString("Title");
                        String date = documentSnapshot.getString("Date");
                        String category = documentSnapshot.getString("Category");
                        String amount = documentSnapshot.getString("Amount");

                        ModelExpenseRecord modelExpenseRecord = new ModelExpenseRecord(id, image, title, date, category, amount);

                        expenseRecordArrayList.add(modelExpenseRecord);
                    }
                }
                adapterExpenseRecord.notifyDataSetChanged();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.ocr:
                Intent i = new Intent(MainActivity.this, OCRFragment.class);
                startActivity(i);
                break;

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

            case R.id.chart:
                Intent chartsIntent = new Intent(MainActivity.this, ChartsFragment.class);
                startActivity(chartsIntent);
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
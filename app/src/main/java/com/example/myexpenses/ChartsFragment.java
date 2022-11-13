package com.example.myexpenses;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ChartsFragment extends AppCompatActivity {

    private RecyclerView recyclerViewHistory;
    private Toolbar mToolbar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;
    private ArrayList<ModelAnalytics> analyticsArrayList;
    private AdapterAnalytics adapterAnalytics;
    private String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charts_fragment);

        recyclerViewHistory = findViewById(R.id.recyclerViewHistory);
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Analytics");

        //set back button on the action bar to go back to home
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        recyclerViewHistory.setHasFixedSize(true);
        recyclerViewHistory.setLayoutManager(new LinearLayoutManager(this));

        analyticsArrayList = new ArrayList<>();
        adapterAnalytics = new AdapterAnalytics(analyticsArrayList,ChartsFragment.this);
        userID = mAuth.getCurrentUser().getUid();
        recyclerViewHistory.setAdapter(adapterAnalytics);

        loadAnalytics();
    }

    private void loadAnalytics() {

        fStore.collection("Categories").document(userID)
                .collection("Category").orderBy("category", Query.Direction.ASCENDING).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        analyticsArrayList.clear();

                        for (int i=0; i<queryDocumentSnapshots.size(); i++){
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(i);
                            if (documentSnapshot.exists()){
                                String id = documentSnapshot.getId();
                                String category = documentSnapshot.getString("category");
                                String amount = documentSnapshot.getString("total amount");

                                ModelAnalytics modelAnalytics = new ModelAnalytics(id, category, amount);

                                analyticsArrayList.add(modelAnalytics);
                            }
                        }
                        adapterAnalytics.notifyDataSetChanged();

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
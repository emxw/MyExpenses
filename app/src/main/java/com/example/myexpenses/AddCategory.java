package com.example.myexpenses;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AddCategory extends AppCompatActivity {

    private Toolbar mToolbar;
    private Button add_category;
    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;
    private ArrayList<ModelCategory> categoryArrayList;
    private AdapterCategory adapterCategory;
    private RecyclerView recyclerView;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        add_category = findViewById(R.id.addCategory);
        recyclerView = findViewById(R.id.recyclerViewCategory);
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Categories");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        categoryArrayList = new ArrayList<>();
        adapterCategory = new AdapterCategory(categoryArrayList,AddCategory.this);
        userID = mAuth.getCurrentUser().getUid();
        recyclerView.setAdapter(adapterCategory);

        loadCategories();

        add_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddCategory.this, Category.class);
                startActivity(intent);
            }
        });
    }

    private void loadCategories() {
        fStore.collection("Categories").document(userID)
                .collection("Category").orderBy("category", Query.Direction.ASCENDING)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                categoryArrayList.clear();

                for (int i=0; i<queryDocumentSnapshots.size(); i++){
                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(i);
                    if (documentSnapshot.exists()){
                        String id = documentSnapshot.getId();
                        String category = documentSnapshot.getString("category");

                        ModelCategory modelCategory = new ModelCategory(id, category, userID);

                        categoryArrayList.add(modelCategory);
                    }
                }
                adapterCategory.notifyDataSetChanged();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()){
//            case android.R.id.home:
//                finish();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }
}
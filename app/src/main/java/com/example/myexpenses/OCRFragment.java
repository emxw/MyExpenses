package com.example.myexpenses;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class OCRFragment extends AppCompatActivity {

    private Toolbar mToolbar;
    private FloatingActionButton fab;
    private RecyclerView recyclerViewOCR;
    private ArrayList<ModelOCROutput> ocrArrayList;
    private AdapterOCROutput adapterOCROutput;
    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr_fragment);

        recyclerViewOCR = findViewById(R.id.recyclerViewOCR);
        fab = findViewById(R.id.fab);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("OCR");

        //set back button on the action bar to go back to home
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        recyclerViewOCR.setHasFixedSize(true);
        recyclerViewOCR.setLayoutManager(new LinearLayoutManager(this));

        ocrArrayList = new ArrayList<>();
        adapterOCROutput = new AdapterOCROutput(ocrArrayList,OCRFragment.this);
        userID = mAuth.getCurrentUser().getUid();
        recyclerViewOCR.setAdapter(adapterOCROutput);

        loadOCRRecords();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OCRFragment.this, OCRScan.class);
                startActivity(intent);
            }
        });
    }

    private void loadOCRRecords() {
        fStore.collection("User Record Information").document(userID)
                .collection("OCR").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        ocrArrayList.clear();

                        for (int i=0; i<queryDocumentSnapshots.size(); i++){
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(i);
                            if (documentSnapshot.exists()){
                                String id = documentSnapshot.getId();
                                String ocrTitle = documentSnapshot.getString("OCR Title");
                                String ocrOutput = documentSnapshot.getString("OCR Output");

                                ModelOCROutput modelOCROutput = new ModelOCROutput(id, ocrTitle, ocrOutput);

                                ocrArrayList.add(modelOCROutput);
                            }
                        }
                        adapterOCROutput.notifyDataSetChanged();
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
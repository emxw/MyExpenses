package com.example.myexpenses;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Category extends AppCompatActivity {

    private Toolbar mToolbar;
    private Button submit_category;
    private EditText categoryEt;
    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Add Category");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        submit_category = findViewById(R.id.submitCategory);
        categoryEt = findViewById(R.id.categoryEt);
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Please wait");
        mProgressDialog.setCanceledOnTouchOutside(false);

        submit_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String category = categoryEt.getText().toString().trim();
                String userID = mAuth.getCurrentUser().getUid();

                if (TextUtils.isEmpty(category)){
                    Toast.makeText(Category.this, "Please enter category", Toast.LENGTH_SHORT).show();

                }
                else{
                    mProgressDialog.setMessage("Adding category");
                    mProgressDialog.show();

                    long timeStamp = System.currentTimeMillis();

                    DocumentReference documentReference = fStore.collection("Categories").document(userID)
                            .collection("Category").document(category);
                    Map<String, Object> addCategory = new HashMap<>();
                    addCategory.put("category", ""+category);
                    addCategory.put("uid", ""+mAuth.getUid());
                    addCategory.put("total amount", "0");
                    documentReference.set(addCategory);
                    mProgressDialog.dismiss();
                    Toast.makeText(Category.this, "Category added successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Category.this, AddCategory.class);
                    startActivity(intent);
                }
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
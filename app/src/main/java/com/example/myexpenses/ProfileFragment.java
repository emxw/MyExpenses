package com.example.myexpenses;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProfileFragment extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView username, mobileNumber, email;
    private ImageView profilePic;
    private Uri imageUri;
    private Button updateProfile;
    private FirebaseAuth mAuth;
    private FirebaseFirestore fstore;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_fragment);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Profile");

        //set back button on the action bar to go back to home
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        username = findViewById(R.id.username);
        mobileNumber = findViewById(R.id.mobileNumber);
        email = findViewById(R.id.email);
        updateProfile = findViewById(R.id.updateProfile);

        mAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        userID = mAuth.getCurrentUser().getUid();
        DocumentReference documentReference = fstore.collection("Users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                username.setText(documentSnapshot.getString("username"));
                email.setText(documentSnapshot.getString("email"));
                mobileNumber.setText(documentSnapshot.getString("mobileNumber"));
            }
        });

        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usernameString = username.getText().toString();
                String mobileNumString = mobileNumber.getText().toString();

                if (TextUtils.isEmpty(usernameString)){
                    username.setError("Username is required");
                    username.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(mobileNumString)){
                    mobileNumber.setError("Mobile number is required");
                    mobileNumber.requestFocus();
                    return;
                }
                else{
                    DocumentReference documentReference = fstore.collection("Users").document(userID);
                    Map<String, Object> profileInfo = new HashMap<>();
                    profileInfo.put("username", usernameString);
                    profileInfo.put("mobileNumber", mobileNumString);
                    Toast.makeText(ProfileFragment.this, "Profile have been updated successfully!", Toast.LENGTH_LONG).show();
                    documentReference.update(profileInfo);
                }
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
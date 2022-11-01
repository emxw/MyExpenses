package com.example.myexpenses;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterUser extends AppCompatActivity {

    private EditText username, mobileNumber, email, password;
    private Button register;
    private TextView login_button;

    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        username = findViewById(R.id.username);
        mobileNumber = findViewById(R.id.mobileNumber);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        register = findViewById(R.id.register_button);
        login_button = findViewById(R.id.back_to_login);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(this);

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterUser.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String usernameString = username.getText().toString();
                final String mobileNumberString = mobileNumber.getText().toString();
                final String emailString = email.getText().toString();
                final String passwordString = password.getText().toString();
                if (TextUtils.isEmpty(usernameString)){
                    username.setError("Username is required");
                    username.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(mobileNumberString)){
                    mobileNumber.setError("Mobile number is required");
                    mobileNumber.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(emailString)){
                    email.setError("Email is required");
                    email.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(emailString).matches()){
                    email.setError("Please provide a valid email");
                    email.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(passwordString)){
                    password.setError("Password is required");
                    password.requestFocus();
                    return;
                }
                else {
                    progressDialog.setMessage("In Progress");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    mAuth.createUserWithEmailAndPassword(emailString, passwordString).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                String userID = mAuth.getCurrentUser().getUid();
                                DocumentReference documentReference = fStore.collection("Users").document(userID);
                                Map<String, Object> user = new HashMap<>();
                                user.put("username", usernameString);
                                user.put("mobileNumber", mobileNumberString);
                                user.put("email", emailString);
                                documentReference.set(user);
                                Toast.makeText(RegisterUser.this, "User is registered", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(RegisterUser.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else{
                                Toast.makeText(RegisterUser.this, "Failed to register! Please try again", Toast.LENGTH_LONG).show();
                            }
                            progressDialog.dismiss();
                        }
                    });
                }
            }
        });
    }
}
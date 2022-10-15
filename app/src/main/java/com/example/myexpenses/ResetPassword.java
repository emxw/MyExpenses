package com.example.myexpenses;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class ResetPassword extends AppCompatActivity {

    private EditText email;
    private Button confirm_button;
    private TextView cancelReset;

    FirebaseAuth mAuth;
    //private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        email = findViewById(R.id.email);
        confirm_button = findViewById(R.id.confirm);
        cancelReset = findViewById(R.id.cancelResetPassword);

        mAuth = FirebaseAuth.getInstance();
        //progressDialog = new ProgressDialog(this);

        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailString = email.getText().toString();
                if (emailString.isEmpty()){
                    email.setError("Email is required");
                    email.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(emailString).matches()){
                    email.setError("Please provide a valid email");
                    email.requestFocus();
                    return;
                }

                mAuth.sendPasswordResetEmail(emailString).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(ResetPassword.this, "Check your email to reset your password", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(ResetPassword.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else{
                            Toast.makeText(ResetPassword.this, "Something went wrong! Please try again", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        cancelReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResetPassword.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }


}
package com.nisith.firebaseauth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    private EditText emailIdEditText, passwordEditText;
    private Button createAccountButton;
    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Create Account");
        setContentView(R.layout.activity_register);
        emailIdEditText = findViewById(R.id.email_id_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);
        createAccountButton = findViewById(R.id.create_account_button);
        firebaseAuth = FirebaseAuth.getInstance();
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailId = emailIdEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if (emailId.isEmpty()){
                    emailIdEditText.setError("Email Required");
                    emailIdEditText.requestFocus();
                }else if (password.isEmpty()){
                    passwordEditText.setError("Password Required");
                    passwordEditText.requestFocus();
                }else {
                    createAccount(emailId,password);
                }
            }
        });

    }



    private void createAccount(String emailAddress, String password){
        progressBar.setVisibility(View.VISIBLE);
        createAccountButton.setEnabled(false);
        firebaseAuth.createUserWithEmailAndPassword(emailAddress,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                createAccountButton.setEnabled(true);
                if (task.isSuccessful()){
                    Toast.makeText(RegisterActivity.this,"Registration Successful",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finishAffinity();//Clear all back stack Activities
                }else {
                    Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}

package com.nisith.firebaseauth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {
    private EditText emailIdEditText, passwordEditText;
    private Button loginButton;
    private TextView forgetPasswordTextView, signUpTextView;
    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Login");
        emailIdEditText = findViewById(R.id.email_id_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        loginButton = findViewById(R.id.logout_button);
        forgetPasswordTextView = findViewById(R.id.forget_password_text_view);
        signUpTextView = findViewById(R.id.sign_up_text_view);
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);
        firebaseAuth = FirebaseAuth.getInstance();
        loginButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                String emailId = emailIdEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if (emailId.isEmpty()){
                    emailIdEditText.setError("Email Required");
                    emailIdEditText.requestFocus();
                }else if (password.isEmpty()){
                    passwordEditText.setError("Password Required");
                    passwordEditText.requestFocus();
                }else {
                    login(emailId,password);
                }
            }
        });

      forgetPasswordTextView.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              if (progressBar.getVisibility() == View.VISIBLE){
                  Toast.makeText(LoginActivity.this, "Login Operation is Going On", Toast.LENGTH_SHORT).show();
                  return;
              }
              AlertDialog alertDialog = createResetPasswordAlertDialog(v);
              alertDialog.show();
          }
      });

      signUpTextView.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              if (progressBar.getVisibility() == View.VISIBLE){
                  Toast.makeText(LoginActivity.this, "Login Operation is Going On", Toast.LENGTH_SHORT).show();
                  return;
              }
              startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
          }
      });


    }




    private AlertDialog createResetPasswordAlertDialog( View view){
        final EditText editText = new EditText(view.getContext());
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(view.getContext())
                .setTitle("Reset Password")
                .setMessage("Enter Your Email Address")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String email = editText.getText().toString().trim();
                        if (email.isEmpty()){
                            editText.setError("Email Required");
                            editText.requestFocus();
                            return;
                        }
                        resetUserPassword(email);
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setView(editText);

        return dialogBuilder.create();
    }

    private void resetUserPassword(String email){
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                    Toast.makeText(LoginActivity.this, "Password Reset Link is Send to Your Email. Please Check That", Toast.LENGTH_SHORT).show();

                }else {
                    Toast.makeText(LoginActivity.this, "Reset Password Operation is Not Successful "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser= firebaseAuth.getCurrentUser();
        if (currentUser != null){
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }

    }

    private void login(String emailId, String password){
        progressBar.setVisibility(View.VISIBLE);
        loginButton.setEnabled(false);
        firebaseAuth.signInWithEmailAndPassword(emailId,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                loginButton.setEnabled(true);
                if (task.isSuccessful()){
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

    }




}

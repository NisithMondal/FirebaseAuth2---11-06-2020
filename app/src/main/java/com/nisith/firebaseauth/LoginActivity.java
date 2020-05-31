package com.nisith.firebaseauth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    private LoginButton facebookLoginButton;
    private Button createAccountButton, googleSignButton, button;
    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;
    private static final int REQUEST_CODE = 1;
    private CallbackManager callbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Login");
        emailIdEditText = findViewById(R.id.email_id_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        loginButton = findViewById(R.id.login_button);
        googleSignButton = findViewById(R.id.google_signIn_button);
        facebookLoginButton = findViewById(R.id.facebook_login_button);
        createAccountButton = findViewById(R.id.create_account_button);
        firebaseAuth = FirebaseAuth.getInstance();
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                facebookLoginButton.performClick();
            }
        });




        callbackManager = CallbackManager.Factory.create();
        facebookLoginButton.setReadPermissions(Arrays.asList("email","public_profile", "user_friends"));


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this,gso);
        loginButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                String emailId = emailIdEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if (emailId.isEmpty()){
                    Toast.makeText(LoginActivity.this, "Enter Email Address", Toast.LENGTH_SHORT).show();
                }else if (password.isEmpty()){
                    Toast.makeText(LoginActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
                }else {
                    login(emailId,password);
                }


            }
        });

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

        googleSignButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInThroughGoogleAccount();
            }
        });

      facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
          @Override
          public void onSuccess(LoginResult loginResult) {
              firebaseAuthWithFacebook(loginResult.getAccessToken());
          }

          @Override
          public void onCancel() {

          }

          @Override
          public void onError(FacebookException error) {
              Log.d("ABCD","Sign in not Successful "+error);
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
        firebaseAuth.signInWithEmailAndPassword(emailId,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
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



    private void signInThroughGoogleAccount(){
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, REQUEST_CODE);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && data != null){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                //Sign In Successful
                GoogleSignInAccount googleSignInAccount = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(googleSignInAccount.getIdToken());

            }catch (ApiException e){
                Log.d("ABCDE", "Sign In Failed:" + e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken){
        AuthCredential authCredential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "Sign in Successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Toast.makeText(LoginActivity.this, "Not SignIn: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }




    private void firebaseAuthWithFacebook(AccessToken accessToken){
        AuthCredential authCredential = FacebookAuthProvider.getCredential(accessToken.getToken());
        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "Sign in Successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Toast.makeText(LoginActivity.this, "Not SignIn: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("ABCDE","ERROR: "+task.getException().getMessage());
                }
            }
        });

    }


}

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
import android.widget.ProgressBar;
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
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;

public class SignUpActivity extends AppCompatActivity {

    private Button createAccountButton, googleSignButton, button;
    private LoginButton facebookLoginButton;
    private FirebaseAuth firebaseAuth;
    private CallbackManager callbackManager;
    private GoogleSignInClient googleSignInClient;
    private static final int REQUEST_CODE = 1;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        googleSignButton = findViewById(R.id.google_signIn_button);
        facebookLoginButton = findViewById(R.id.facebook_login_button);
        createAccountButton = findViewById(R.id.create_account_button);
        button = findViewById(R.id.button);
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.INVISIBLE);
        firebaseAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this,gso);
        callbackManager = CallbackManager.Factory.create();
        facebookLoginButton.setReadPermissions(Arrays.asList("email","public_profile", "user_friends"));

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

        googleSignButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInThroughGoogleAccount();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                facebookLoginButton.performClick();
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
                Toast.makeText(SignUpActivity.this, "Sign in Not Successful: "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

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
            }
        }
    }


    private void signInThroughGoogleAccount(){
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, REQUEST_CODE);

    }


    private void firebaseAuthWithGoogle(final String idToken){
        progressBar.setVisibility(View.VISIBLE);
        googleSignButton.setEnabled(false);
        button.setEnabled(false);
        createAccountButton.setEnabled(false);
        AuthCredential authCredential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                googleSignButton.setEnabled(true);
                button.setEnabled(true);
                createAccountButton.setEnabled(true);
                progressBar.setVisibility(View.INVISIBLE);
                if (task.isSuccessful()){
                    Toast.makeText(SignUpActivity.this, "Sign in Successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finishAffinity();//Clear all back stack Activities
                }else {
                    Toast.makeText(SignUpActivity.this, "Not SignIn: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }




    private void firebaseAuthWithFacebook(AccessToken accessToken){
        progressBar.setVisibility(View.VISIBLE);
        googleSignButton.setEnabled(false);
        button.setEnabled(false);
        createAccountButton.setEnabled(false);
        AuthCredential authCredential = FacebookAuthProvider.getCredential(accessToken.getToken());
        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.INVISIBLE);
                googleSignButton.setEnabled(true);
                button.setEnabled(true);
                createAccountButton.setEnabled(true);
                if (task.isSuccessful()){
                    Toast.makeText(SignUpActivity.this, "Sign in Successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finishAffinity();//Clear all back stack Activities
                }else {
                    Toast.makeText(SignUpActivity.this, "Not SignIn: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("ABCDE","ERROR: "+task.getException().getMessage());
                }
            }
        });

    }


}

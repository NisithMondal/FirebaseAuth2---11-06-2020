package com.nisith.firebaseauth;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseAuth firebaseAuth1 = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser1 = firebaseAuth.getCurrentUser();
        FirebaseUser firebaseUser2 = firebaseAuth1.getCurrentUser();

        Log.d("EFGH","Main Activity: firebase user-1 UID= "+firebaseUser1.getUid());
        Log.d("EFGH","Main Activity: firebase user-2 UID= "+firebaseUser2.getUid());

    }
}

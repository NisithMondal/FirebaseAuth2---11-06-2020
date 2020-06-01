package com.nisith.firebaseauth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setTitle("Home Activity");
        Button logoutButton = findViewById(R.id.logout_button);
        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseAuth firebaseAuth1 = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser1 = firebaseAuth.getCurrentUser();
        FirebaseUser firebaseUser2 = firebaseAuth1.getCurrentUser();

        Log.d("EFGH","Home Activity: firebase user-1 UID= "+firebaseUser1.getUid());
        Log.d("EFGH","Home Activity: firebase user-2 UID= "+firebaseUser2.getUid());

        logoutButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                firebaseAuth.signOut();
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();

            }
        });

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }
}

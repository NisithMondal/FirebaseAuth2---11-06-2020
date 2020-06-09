package com.nisith.firebaseauth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {
    private CircleImageView userProfileImage;
    private TextView nameTextView, ageTextView, genderTextView,
                     countryNameTextView, channelNameTextView;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference userProfileDocumentRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        inatilizedViews();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userProfileDocumentRef = firebaseFirestore.collection("all-bloggers").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        loadUserProfile();

    }

    private void inatilizedViews(){
        userProfileImage = findViewById(R.id.profile_image_view);
        nameTextView = findViewById(R.id.name_text_view);
        ageTextView = findViewById(R.id.age_text_view);
        genderTextView = findViewById(R.id.gender_text_view);
        countryNameTextView = findViewById(R.id.country_name_text_view);
        channelNameTextView = findViewById(R.id.channel_name_text_view);
    }

    private void loadUserProfile(){
        if (userProfileDocumentRef != null){

            userProfileDocumentRef.get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()){
                                DocumentSnapshot documentSnapshot = task.getResult();
                                if(documentSnapshot != null){
                                    if (documentSnapshot.get("profileImage") != null){
                                        String profileImageUrl = String.valueOf(documentSnapshot.get("profileImage"));
                                        Picasso.get().load(profileImageUrl).into(userProfileImage);
                                    }
                                    nameTextView.setText("Name: "+String.valueOf(documentSnapshot.get("name")));
                                    ageTextView.setText("Age: "+String.valueOf(documentSnapshot.get("age")));
                                    genderTextView.setText("Gender: "+String.valueOf(documentSnapshot.get("gender")));
                                    countryNameTextView.setText("Country: "+String.valueOf(documentSnapshot.get("country")));
                                    channelNameTextView.setText("Channel: "+String.valueOf(documentSnapshot.get("channelName")));
                                }
                            }else {
                                Toast.makeText(ProfileActivity.this, "User Profiles Not Loaded. Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }else {
            Toast.makeText(this, "User Profiles Not Loaded. Something went Wrong", Toast.LENGTH_SHORT).show();
        }
    }

}

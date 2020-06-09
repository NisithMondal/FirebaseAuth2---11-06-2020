package com.nisith.firebaseauth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.appevents.codeless.internal.UnityReflection;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class CreateProfileActivity extends AppCompatActivity {

    private CircleImageView circleImageView;
    private EditText nameEditText, ageEditText, genderEditText, countryNameEditText, channelNameEditText;
    private Button saveButton;
    private ProgressBar progressBar;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference mStorageRef;
    private static final int PICK_IMAGE_REQUEST = 101;
    private Uri galleryImageUri;
    private Uri profileImageUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        circleImageView = findViewById(R.id.profile_image);
        nameEditText = findViewById(R.id.name_edit_text);
        ageEditText = findViewById(R.id.age_edit_text);
        genderEditText = findViewById(R.id.gender_edit_text);
        countryNameEditText = findViewById(R.id.country_name_edit_text);
        channelNameEditText = findViewById(R.id.channel_name_edit_text);
        saveButton = findViewById(R.id.save_button);
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);
        firebaseFirestore = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference("user_profile_pic");
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserProfile();
            }
        });

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImageFromGallery();
            }
        });
    }


    private void pickImageFromGallery(){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            galleryImageUri = data.getData();
            saveUserProfileImage();
        }
    }

    private void saveUserProfileImage(){
        if(galleryImageUri != null){
            progressBar.setVisibility(View.VISIBLE);
            StorageReference childStorageRef = mStorageRef.child(System.currentTimeMillis()+".jpg");
            childStorageRef.putFile(galleryImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> task = taskSnapshot.getStorage().getDownloadUrl();
                            task.addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    progressBar.setVisibility(View.GONE);
                                    profileImageUrl = task.getResult();
                                    Picasso.get().load(profileImageUrl).into(circleImageView);
                                    Toast.makeText(CreateProfileActivity.this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(CreateProfileActivity.this, "Image Not Upload", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }

    private void saveUserProfile(){
        String name = nameEditText.getText().toString();
        String age = ageEditText.getText().toString();
        String gender = genderEditText.getText().toString();
        String countName = countryNameEditText.getText().toString();
        String channelName = channelNameEditText.getText().toString();
        if (name.isEmpty()){
            nameEditText.setError("Name is Required");
            nameEditText.requestFocus();
            return;
        }

        if (name.trim().equalsIgnoreCase("null")){
            nameEditText.setError("Name Can Not be null");
            nameEditText.requestFocus();
            return;
        }
        if (age.isEmpty()){
            ageEditText.setError("Age is Required");
            ageEditText.requestFocus();
            return;
        }
        if (gender.isEmpty()){
            genderEditText.setError("Gender is Required");
            genderEditText.requestFocus();
            return;
        }

        if (countName.isEmpty()){
            countryNameEditText.setError("Country Name Required");
            countryNameEditText.requestFocus();
            return;
        }

        if (channelName.isEmpty()){
            channelNameEditText.setError("Channel Name is Required");
            channelNameEditText.requestFocus();
            return;
        }

        DocumentReference documentReference = firebaseFirestore.collection("all-bloggers").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        Map<String,Object> userProfile = new HashMap<>();
        userProfile.put("name",name);
        userProfile.put("age",age);
        userProfile.put("gender",gender);
        userProfile.put("country",countName);
        userProfile.put("channelName",channelName);
        if (profileImageUrl != null) {
            userProfile.put("profileImage",profileImageUrl.toString());
        }else {
            userProfile.put("profileImage",null);
        }

        saveButton.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        documentReference.set(userProfile)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        saveButton.setEnabled(true);
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()){
                            Toast.makeText(CreateProfileActivity.this, "Profile Saved", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(CreateProfileActivity.this, CreateBlog.class));
                            finish();
                        }else {
                            Toast.makeText(CreateProfileActivity.this, "Profile Not Saved. Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });




    }

}

package com.nisith.firebaseauth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {
    private CircleImageView userProfileImage;
    private TextView nameTextView, ageTextView, genderTextView,
                     countryNameTextView, channelNameTextView;
    private ProgressBar progressBar;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference userProfileDocumentRef;
    private String profileImageUrl, name, age, gender, country, channel;
    private ListenerRegistration listenerRegistration;
    private StorageReference mStorageRef;
    private FirebaseStorage firebaseStorage;
    private static final int PICK_IMAGE_REQUEST = 101;
    private Uri galleryImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        inatilizedViews();
        userProfileImage.setOnClickListener(new MyTextViewClickListener());
        nameTextView.setOnClickListener(new MyTextViewClickListener());
        ageTextView.setOnClickListener(new MyTextViewClickListener());
        genderTextView.setOnClickListener(new MyTextViewClickListener());
        countryNameTextView.setOnClickListener(new MyTextViewClickListener());
        channelNameTextView.setOnClickListener(new MyTextViewClickListener());
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference("user_profile_pic");
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
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (userProfileDocumentRef != null){
            loadUserProfile();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (listenerRegistration != null){
            listenerRegistration.remove();
        }
    }

    private void loadUserProfile(){

       listenerRegistration = userProfileDocumentRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
           @Override
           public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
               if(documentSnapshot != null){
                   if (documentSnapshot.get("profileImage") != null){
                       profileImageUrl = String.valueOf(documentSnapshot.get("profileImage"));
                       Picasso.get().load(profileImageUrl).placeholder(R.drawable.user).into(userProfileImage);
                   }
                   name = String.valueOf(documentSnapshot.get("name"));
                   nameTextView.setText("Name: "+name);
                   age = String.valueOf(documentSnapshot.get("age"));
                   ageTextView.setText("Age: "+age);
                   gender = String.valueOf(documentSnapshot.get("gender"));
                   genderTextView.setText("Gender: "+gender);
                   country = String.valueOf(documentSnapshot.get("country"));
                   countryNameTextView.setText("Country: "+country);
                   channel = String.valueOf(documentSnapshot.get("channelName"));
                   channelNameTextView.setText("Channel: "+channel);
               }else {
                   Toast.makeText(ProfileActivity.this, "User Profile Not Loaded", Toast.LENGTH_SHORT).show();
               }
           }
       });
    }

    private class MyTextViewClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){

                case R.id.profile_image_view:
                    pickImageFromGallery();
                    break;
                case R.id.name_text_view:
                    showAlertDialog(v, "Name","name",name);
                    break;
                case R.id.age_text_view:
                    showAlertDialog(v, "Age","age",age);
                    break;
                case R.id.gender_text_view:
                    showAlertDialog(v, "Gender","gender",gender);
                    break;
                case R.id.country_name_text_view:
                    showAlertDialog(v, "Country","country",country);
                    break;
                case R.id.channel_name_text_view:
                    showAlertDialog(v, "Channel Name","channelName",channel);
                    break;
            }
        }
    }

    private void showAlertDialog(View view, String title, final String fieldName, final String text){
        final EditText editText = new EditText(view.getContext());
        editText.setText(text);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(view.getContext())
                .setTitle("Update Your "+title)
                .setView(editText)
                .setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateUserProfile(fieldName, editText.getText().toString());
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        dialogBuilder.create().show();

    }

    private void updateUserProfile(String fieldName, String text){
        userProfileDocumentRef.update(fieldName,text)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(ProfileActivity.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(ProfileActivity.this, "Update Not Successful. Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
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
                                    final Uri newProfileImageUrl = task.getResult();
                                    if (newProfileImageUrl != null) {
                                        if (profileImageUrl != null) {
                                            progressBar.setVisibility(View.VISIBLE);
                                            firebaseStorage.getReferenceFromUrl(profileImageUrl).delete()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            progressBar.setVisibility(View.GONE);
                                                            if (task.isSuccessful()){
                                                                updateUserProfile("profileImage", newProfileImageUrl.toString());
                                                            }else {
                                                                Toast.makeText(ProfileActivity.this, "Profile Image Not Changed. Something Went Wrong", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        }else {
                                            updateUserProfile("profileImage", newProfileImageUrl.toString());
                                        }

                                    }else {
                                        updateUserProfile("profileImage", null);
                                    }
                                    Picasso.get().load(newProfileImageUrl).placeholder(R.drawable.user).into(userProfileImage);
                                    Toast.makeText(ProfileActivity.this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(ProfileActivity.this, "Image Not Upload", Toast.LENGTH_SHORT).show();
                        }
                    });
        }else {
            Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show();
        }

    }


}






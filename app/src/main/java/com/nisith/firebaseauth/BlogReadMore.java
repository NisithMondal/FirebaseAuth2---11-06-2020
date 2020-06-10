package com.nisith.firebaseauth;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class BlogReadMore extends AppCompatActivity {

    private TextView titleTextView, categoryTextView, publishedDateTextView, blogTextView, totalViewsTextView, likeTextView, dislikeTextView;
    private ProgressBar progressBar;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference blogDocumentRef;
    private ListenerRegistration listenerRegistration;
    private long totalViews, totalLikes, totalDislikes;
    private String likeDislikeUserValue;
    private boolean stopThread = false;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_read_more);
        inatilaziedViews();
        Intent intent = getIntent();
        String documentId = intent.getStringExtra("document_id");
        firebaseFirestore = FirebaseFirestore.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        blogDocumentRef = firebaseFirestore.collection("all-bloggers").document(currentUserId)
                .collection("all-blogs").document(documentId);
//        fetchBlogData();
        calculateViews();

        likeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likeButtonOperation();
            }
        });

        dislikeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dislikeButtonOperation();
            }
        });
    }


    private void likeButtonOperation(){
        dislikeTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_black_dislike_icon,0,0,0);
        Drawable drawable = likeTextView.getCompoundDrawables()[0];
        if (drawable.getConstantState().equals(getDrawable(R.drawable.ic_black_like_icon).getConstantState())){
            //User Liked The Blog
            likeTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_blue_like_icon,0,0,0);
            Log.d("ABCD","likeDislikeUserValue= "+likeDislikeUserValue);
            if (likeDislikeUserValue != null){
                // LikeDislike user Already exist
                blogDocumentRef.update(currentUserId, "like")
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                blogDocumentRef.update("totalLikes",FieldValue.increment(1));
                                if (totalDislikes >0) {
                                    blogDocumentRef.update("totalDislikes", FieldValue.increment(-1));
                                }
                                Toast.makeText(BlogReadMore.this, "You Liked This Blog", Toast.LENGTH_SHORT).show();
                            }
                        });

            }else {
                // LikeDislike user not exist
                Map<String,Object> map = new HashMap<>();
                map.put(currentUserId,"like");
                blogDocumentRef.set(map, SetOptions.merge())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                blogDocumentRef.update("totalLikes",FieldValue.increment(1));
                                Toast.makeText(BlogReadMore.this, "You Liked This Blog", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }else {
            //User Removed Like
            likeTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_black_like_icon,0,0,0);
            Map<String,Object> map = new HashMap<>();
            map.put(currentUserId, FieldValue.delete());
            blogDocumentRef.update(map)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            blogDocumentRef.update("totalLikes",FieldValue.increment(-1));
                            Toast.makeText(BlogReadMore.this, "You Removed Your Like", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }



    private void dislikeButtonOperation(){
        likeTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_black_like_icon,0,0,0);
        Drawable drawable = dislikeTextView.getCompoundDrawables()[0];
        if (drawable.getConstantState().equals(getDrawable(R.drawable.ic_black_dislike_icon).getConstantState())){
            dislikeTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_blue_dislike_icon,0,0,0);
            Log.d("ABCD","likeDislikeUserValue= "+likeDislikeUserValue);
            if (likeDislikeUserValue != null){
                // LikeDislike user Already exist
                blogDocumentRef.update(currentUserId, "dislike")
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                blogDocumentRef.update("totalDislikes",FieldValue.increment(1));
                                if (totalLikes >0) {
                                    blogDocumentRef.update("totalLikes", FieldValue.increment(-1));
                                }
                                Toast.makeText(BlogReadMore.this, "You dislike This Blog", Toast.LENGTH_SHORT).show();
                            }
                        });
                Log.d("ABCD","Inside If");

            }else {
                // LikeDislike user not exist
                Map<String,Object> map = new HashMap<>();
                map.put(currentUserId,"dislike");
                blogDocumentRef.set(map, SetOptions.merge())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                blogDocumentRef.update("totalDislikes",FieldValue.increment(1));
                                Toast.makeText(BlogReadMore.this, "You dislike This Blog", Toast.LENGTH_SHORT).show();
                                Log.d("ABCD","Inside else");
                            }
                        });
            }

        }else {
            dislikeTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_black_dislike_icon,0,0,0);
            Map<String,Object> map = new HashMap<>();
            map.put(currentUserId, FieldValue.delete());
            blogDocumentRef.update(map)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            blogDocumentRef.update("totalDislikes",FieldValue.increment(-1));
                            Toast.makeText(BlogReadMore.this, "You Removed Your Dislike", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }



    private void inatilaziedViews(){
        titleTextView = findViewById(R.id.blog_title_text_view);
        categoryTextView = findViewById(R.id.blog_category_text_view);
        publishedDateTextView = findViewById(R.id.published_date_text_view);
        blogTextView = findViewById(R.id.blog_text_view);
        totalViewsTextView = findViewById(R.id.total_views_text_view);
        likeTextView = findViewById(R.id.like_text_view);
        dislikeTextView = findViewById(R.id.dislike_text_view);
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (blogDocumentRef != null){
           listenerRegistration = blogDocumentRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if (documentSnapshot != null){
                        titleTextView.setText(String.valueOf(documentSnapshot.get("blogTitle")));
                        categoryTextView.setText(String.valueOf(documentSnapshot.get("blogCategory")));
                        publishedDateTextView.setText(String.valueOf(documentSnapshot.get("publishedDate")));
                        blogTextView.setText(String.valueOf(documentSnapshot.get("blog")));
                        totalViews = (long) documentSnapshot.get("totalViews");
                        totalViewsTextView.setText(String.valueOf(totalViews+" Views"));
                        totalLikes = (long) documentSnapshot.get("totalLikes");
                        likeTextView.setText(String.valueOf(totalLikes));
                        totalDislikes = (long) documentSnapshot.get("totalDislikes");
                        dislikeTextView.setText(String.valueOf(totalDislikes));
                        Object object = documentSnapshot.get(currentUserId);
                        if (object == null){
                            likeDislikeUserValue = null;
                        }else {
                            likeDislikeUserValue = String.valueOf(object);
                        }
                        if (likeDislikeUserValue != null){
                            setLikeDislikeDrawables(likeDislikeUserValue);
                        }

                    }else {
                        Toast.makeText(BlogReadMore.this, "Blog Contents Not Loaded. Something Went wrong", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    private void setLikeDislikeDrawables(String likeDislikeUser){

        if (likeDislikeUser.equalsIgnoreCase("like")){
            likeTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_blue_like_icon,0,0,0);
            dislikeTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_black_dislike_icon,0,0,0);
        }else if (likeDislikeUser.equalsIgnoreCase("dislike")){
            dislikeTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_blue_dislike_icon,0,0,0);
            likeTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_black_like_icon,0,0,0);
        }



    }



    @Override
    protected void onStop() {
        super.onStop();
        if (listenerRegistration != null){
            listenerRegistration.remove();
        }
        stopThread = true;
    }

//    private void fetchBlogData(){
//        progressBar.setVisibility(View.VISIBLE);
//        blogDocumentRef.get()
//                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        progressBar.setVisibility(View.GONE);
//                        if (task.isSuccessful()){
//                            DocumentSnapshot documentSnapshot = task.getResult();
//                            if (documentSnapshot != null){
//                                titleTextView.setText(String.valueOf(documentSnapshot.get("blogTitle")));
//                                categoryTextView.setText(String.valueOf(documentSnapshot.get("blogCategory")));
//                                publishedDateTextView.setText(String.valueOf(documentSnapshot.get("publishedDate")));
//                                blogTextView.setText(String.valueOf(documentSnapshot.get("blog")));
//                                totalViews = (long) documentSnapshot.get("totalViews");
//                                totalViewsTextView.setText(String.valueOf(totalViews+" Views"));
//                                totalLikes = (long) documentSnapshot.get("totalLikes");
//                                likeTextView.setText(String.valueOf(totalLikes));
//                                totalDislikes = (long) documentSnapshot.get("totalDislikes");
//                                dislikeTextView.setText(String.valueOf(totalDislikes));
//                            }else {
//                                Toast.makeText(BlogReadMore.this, "Blog Contents Not Loaded. Something Went wrong", Toast.LENGTH_SHORT).show();
//                            }
//                        }else {
//                            Toast.makeText(BlogReadMore.this, "Blog Contents Not Loaded. Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }


    private void calculateViews(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                int index = 1;
                while (!stopThread) {
                    SystemClock.sleep(1000);
                    if (index == 15) {
                        blogDocumentRef.update("totalViews", FieldValue.increment(1));
                        break;
                    }
                    index++;

                }

            }
        });
        thread.start();
    }



}

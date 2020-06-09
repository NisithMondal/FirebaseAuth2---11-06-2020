package com.nisith.firebaseauth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CreateBlog extends AppCompatActivity {

    private EditText blogTitleEditText, blogCategoryEditText, blogEditText;
    private Button saveBlogButton, seeAllBlogsButton, userProfielButton;
    private ProgressBar progressBar;
    private FirebaseFirestore firebaseFirestoreDB;
    private CollectionReference blogCollectionRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_blog);
        inatializeViews();
        firebaseFirestoreDB = FirebaseFirestore.getInstance();
        blogCollectionRef = firebaseFirestoreDB.collection("blogs");
        saveBlogButton.setOnClickListener(new MyButtonClick());
        seeAllBlogsButton.setOnClickListener(new MyButtonClick());
        userProfielButton.setOnClickListener(new MyButtonClick());
        firebaseFirestoreDB = FirebaseFirestore.getInstance();
        blogCollectionRef = firebaseFirestoreDB.collection("all-bloggers")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("all-blogs");

    }

    private void inatializeViews(){

        blogTitleEditText = findViewById(R.id.blog_title_edit_text);
        blogCategoryEditText = findViewById(R.id.blog_category_edit_text);
        blogEditText = findViewById(R.id.blog_edit_text);
        saveBlogButton = findViewById(R.id.save_blog_button);
        progressBar = findViewById(R.id.progress_bar);
        seeAllBlogsButton = findViewById(R.id.see_all_blog_button);
        userProfielButton = findViewById(R.id.user_profile_button);
        progressBar.setVisibility(View.GONE);
    }

    private class MyButtonClick implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.save_blog_button:
                    saveBlog();
                    break;

                case R.id.user_profile_button:
                    startActivity(new Intent(CreateBlog.this, ProfileActivity.class));
                    break;

                case R.id.see_all_blog_button:
                    startActivity(new Intent(CreateBlog.this, AllBlogs.class));
                    break;
            }
        }
    }


    private void saveBlog(){

        String blogTitle = blogTitleEditText.getText().toString();
        String blogCategory = blogCategoryEditText.getText().toString();
        String blogText = blogEditText.getText().toString();
        if (blogTitle.isEmpty()){
            blogTitleEditText.setError("Blog Title is Required");
            blogTitleEditText.requestFocus();
            return;
        }
        if (blogCategory.isEmpty()){
            blogCategoryEditText.setError("Blog Category is Required");
            blogCategoryEditText.requestFocus();
            return;
        }
        if (blogText.isEmpty()){
            blogEditText.setError("Blog Required");
            blogEditText.requestFocus();
            return;
        }

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String currentDate= formatter.format(date);
        Blog myBlog = new Blog(blogTitle, blogCategory, blogText, currentDate, 0,0);
        saveBlogButton.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        blogCollectionRef.add(myBlog)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        saveBlogButton.setEnabled(true);
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()){
                            Toast.makeText(CreateBlog.this, "Blog Saved Successfully", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(CreateBlog.this, "Blog Not Save. Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });



    }

}


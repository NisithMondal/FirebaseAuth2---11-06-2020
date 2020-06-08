package com.nisith.firebaseauth;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class CreateBlog extends AppCompatActivity {

    private EditText blogTitleEditTerxt, blogCategoryEditText, blogEditText;
    private Button saveBlogButton;
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
    }

    private void inatializeViews(){

        blogTitleEditTerxt = findViewById(R.id.blog_title_edit_text);
        blogCategoryEditText = findViewById(R.id.blog_category_edit_text);
        blogEditText = findViewById(R.id.blog_edit_text);
        saveBlogButton = findViewById(R.id.save_blog_button);
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

    }
}

package com.nisith.firebaseauth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class AllBlogs extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private CollectionReference allBlogsCollectionRef;
    private MyFirestorePagingAdapter myFirestorePagingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_blogs);

        firebaseFirestore = FirebaseFirestore.getInstance();
        allBlogsCollectionRef = firebaseFirestore.collection("all-bloggers").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("all-blogs");

        setUpRecyclerViewWithAdapter();
    }

    private void setUpRecyclerViewWithAdapter(){

        Query query = allBlogsCollectionRef.orderBy("publishedDate");

        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(5)
                .setPageSize(3)
                .setPrefetchDistance(3)
                .setEnablePlaceholders(false)
                .build();

        FirestorePagingOptions<Blog> firestorePagingOptions = new FirestorePagingOptions.Builder<Blog>()
                .setQuery(query,config, Blog.class)
                .build();
        myFirestorePagingAdapter = new MyFirestorePagingAdapter(firestorePagingOptions);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(myFirestorePagingAdapter);
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (myFirestorePagingAdapter != null){
            myFirestorePagingAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (myFirestorePagingAdapter != null){
            myFirestorePagingAdapter.stopListening();
        }
    }
}

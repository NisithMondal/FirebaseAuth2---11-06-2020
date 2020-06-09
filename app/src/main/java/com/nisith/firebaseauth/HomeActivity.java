package com.nisith.firebaseauth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements MyFirestoreRecyclerViewAdapter.OnUpdateViewClickListener {
    private EditText nameEditText, stateNameEditText, countryNameEditText, phoneNumberEditText;
    private Button uploadButton, updateButton, createBlogButton;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private  ListenerRegistration listenerRegistration;
    private CollectionReference collectionReference;
    private CollectionReference allBloggersCollectionRef;
//    private MyFirestoreRecyclerViewAdapter recyclerViewAdapter;
    private DocumentSnapshot documentSnapshot= null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setTitle("Home Activity");
        viewIntalization();
        progressBar.setVisibility(View.GONE);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection("person");
        allBloggersCollectionRef = firebaseFirestore.collection("all-bloggers");
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadDataToFireStore();
            }
        });


        updateButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                firebaseAuth.signOut();
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();

            }
        });

//        updateButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                performUpdates();
//            }
//        });

        createBloggerAccount();
        createBlogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createBlog();
            }
        });


//        setupRecyclerViewWithAdapter();

    }



    private void createBloggerAccount(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null){
            String userId = currentUser.getUid();
            allBloggersCollectionRef.document(userId).collection("all-blogs");
            Log.d("ASDF","currentUser= "+currentUser);
        }else {
            Log.d("ASDF","currentUser= "+currentUser);
        }
    }

    private void createBlog(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        allBloggersCollectionRef.document(currentUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            Object name = task.getResult().get("name");
                            if (name != null){
                                startActivity(new Intent(HomeActivity.this, CreateBlog.class));
                            }else {
                                startActivity(new Intent(HomeActivity.this, CreateProfileActivity.class));
                            }
                        }
                    }
                });
    }


    private void performUpdates(){
        if (documentSnapshot != null){
            final String name = nameEditText.getText().toString().trim();
            String stateName = stateNameEditText.getText().toString();
            String countName = countryNameEditText.getText().toString();
            String phoneNumber = phoneNumberEditText.getText().toString();
            if (name.isEmpty()){
                nameEditText.setError("Name is Required");
                nameEditText.requestFocus();
                return;
            }
            if (stateName.isEmpty()){
                stateNameEditText.setError("State Name Required");
                stateNameEditText.requestFocus();
                return;
            }
            if (countName.isEmpty()){
                countryNameEditText.setError("Country Name Required");
                countryNameEditText.requestFocus();
                return;
            }

            if (phoneNumber.isEmpty()){
                phoneNumberEditText.setError("Phone Number Required");
                phoneNumberEditText.requestFocus();
                return;
            }
            if (phoneNumber.length()!=10){
                phoneNumberEditText.setError("Phone Number Must Be Of 10 Digits");
                phoneNumberEditText.requestFocus();
                return;
            }

            Map<String,Object> map = new HashMap<>();
            map.put("name",name);
            map.put("state",stateName);
            map.put("country", countName);
            map.put("phoneNumber", phoneNumber);


            documentSnapshot.getReference().update(map)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                nameEditText.setText("");
                                stateNameEditText.setText("");
                                countryNameEditText.setText("");
                                phoneNumberEditText.setText("");
                                Toast.makeText(HomeActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(HomeActivity.this, "Not Update, Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }


    @Override
    public void onUpdateViewClick(View view, DocumentSnapshot documentSnapshot) {
        this.documentSnapshot = documentSnapshot;
            Person person = documentSnapshot.toObject(Person.class);
            if (person != null) {
                nameEditText.setText(person.getName());
                stateNameEditText.setText(person.getState());
                countryNameEditText.setText(person.getCountry());
                phoneNumberEditText.setText(person.getPhoneNumber());
            }else {
                Toast.makeText(this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
            }
    }



//    private void setupRecyclerViewWithAdapter(){
//        Query query = collectionReference.orderBy("name");
//        PagedList.Config config = new PagedList.Config.Builder()
//                .setInitialLoadSizeHint(5)
//                .setPageSize(3)
//                .setPrefetchDistance(3)
//                .build();
//        FirestorePagingOptions<Person> firestorePagingOptions = new FirestorePagingOptions.Builder<Person>()
//                .setQuery(query, config, Person.class)
//                .build();
//
//        recyclerViewAdapter = new MyFirestoreRecyclerViewAdapter(firestorePagingOptions, this);
//        RecyclerView recyclerView = findViewById(R.id.recycler_view);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//        recyclerView.setAdapter(recyclerViewAdapter);
//
//    }


    @Override
    protected void onStart() {
        super.onStart();
//        if (firebaseFirestore != null){
////            readDataFromFireStore();
//        }

//        if (recyclerViewAdapter != null){
//            recyclerViewAdapter.startListening();
//        }

    }


    @Override
    protected void onStop() {
        super.onStop();
        if (listenerRegistration != null) {
//            listenerRegistration.remove();
        }
//        if (recyclerViewAdapter != null){
//            recyclerViewAdapter.stopListening();
//        }
    }

    private void readDataFromFireStore(){
        listenerRegistration = firebaseFirestore.collection("person")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        String result="";
//                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
//                            Person person = documentSnapshot.toObject(Person.class);
//                            result = result + "Name: "+person.getName()+ "\n" + "State Name: "+ person.getState()
//                                    + "\n" + "Country Name: "+person.getCountry() + "\n" + "Phone Number: "+ person.getPhoneNumber() + "\n\n\n";
//                        }
                    }
                });

    }

    private void viewIntalization(){
        nameEditText = findViewById(R.id.name_edit_text);
        stateNameEditText = findViewById(R.id.state_name_edit_text);
        countryNameEditText = findViewById(R.id.country_name_edit_text);
        phoneNumberEditText = findViewById(R.id.phone_number_edit_text);
        uploadButton = findViewById(R.id.upload_button);
        updateButton = findViewById(R.id.update_button);
        createBlogButton = findViewById(R.id.create_blog_button);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void uploadDataToFireStore(){
        String name = nameEditText.getText().toString().trim();
        String stateName = stateNameEditText.getText().toString();
        String countName = countryNameEditText.getText().toString();
        String phoneNumber = phoneNumberEditText.getText().toString();
        if (name.isEmpty()){
            nameEditText.setError("Name is Required");
            nameEditText.requestFocus();
            return;
        }
        if (stateName.isEmpty()){
            stateNameEditText.setError("State Name Required");
            stateNameEditText.requestFocus();
            return;
        }
        if (countName.isEmpty()){
            countryNameEditText.setError("Country Name Required");
            countryNameEditText.requestFocus();
            return;
        }

        if (phoneNumber.isEmpty()){
            phoneNumberEditText.setError("Phone Number Required");
            phoneNumberEditText.requestFocus();
            return;
        }
        if (phoneNumber.length()!=10){
            phoneNumberEditText.setError("Phone Number Must Be Of 10 Digits");
            phoneNumberEditText.requestFocus();
            return;
        }

        Person person = new Person(name, stateName, countName, phoneNumber);
        progressBar.setVisibility(View.VISIBLE);
        uploadButton.setEnabled(false);
        DocumentReference documentReference = collectionReference.document(phoneNumber);
        documentReference.set(person)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressBar.setVisibility(View.GONE);
                        uploadButton.setEnabled(true);
                        if (task.isSuccessful()){
                            Toast.makeText(HomeActivity.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(HomeActivity.this, "Upload Not Successful. Error:  "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



}

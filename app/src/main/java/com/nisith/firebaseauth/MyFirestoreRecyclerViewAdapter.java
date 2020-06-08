package com.nisith.firebaseauth;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class MyFirestoreRecyclerViewAdapter extends FirestorePagingAdapter<Person, MyFirestoreRecyclerViewAdapter.MyViewHolder> {

    private Context context;
    private OnUpdateViewClickListener updateViewClickListener;

    public interface OnUpdateViewClickListener{
        void onUpdateViewClick(View view, DocumentSnapshot documentSnapshot);
    }

    public MyFirestoreRecyclerViewAdapter(@NonNull FirestorePagingOptions<Person> options, AppCompatActivity appCompatActivity) {
        super(options);
        updateViewClickListener = (OnUpdateViewClickListener) appCompatActivity;
        context = appCompatActivity.getApplicationContext();
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_appearence, parent,false);
        return new MyViewHolder(view);
    }



    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position, @NonNull Person person) {
        myViewHolder.name.setText("Name: "+person.getName());
        myViewHolder.stateName.setText("State: "+person.getState());
        myViewHolder.countryName.setText("Country: "+person.getCountry());
        myViewHolder.phoneNumber.setText("Phone No.: "+person.getPhoneNumber());
    }


    @Override
    protected void onLoadingStateChanged(@NonNull LoadingState state) {
        super.onLoadingStateChanged(state);
        switch (state){
            case LOADING_INITIAL:
                Log.d("ABCD","LOADING INITIAL Data");
                break;
            case LOADED:
                Log.d("ABCD","Loading Finished. Total Loaded Data= "+getItemCount());
                break;

            case LOADING_MORE:
                Log.d("ABCD","LOADING More Data");
                break;
            case FINISHED:
                Log.d("ABCD","LOADING Finished");
                break;
            case ERROR:
                Log.d("ABCD","LOADING Error");
                break;


        }

    }

    protected class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name, stateName, countryName, phoneNumber, update, delete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name_text_view);
            stateName = itemView.findViewById(R.id.state_name_text_view);
            countryName = itemView.findViewById(R.id.country_name_text_view);
            phoneNumber = itemView.findViewById(R.id.phone_number_text_view);
            update = itemView.findViewById(R.id.update_text_view);
            delete = itemView.findViewById(R.id.delete_text_view);

            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                    DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(getAdapterPosition());
//                    updateViewClickListener.onUpdateViewClick(v, documentSnapshot);
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteUserData(getAdapterPosition());

                }
            });
        }
    }

    private void deleteUserData(final int position){
        DocumentSnapshot documentSnapshot = getItem(position);
        if (documentSnapshot != null){
            documentSnapshot.getReference().delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                notifyItemRemoved(position);
                                Toast.makeText(context, "User Data Deleted Successfully", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(context, "User Data Not Delete, Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }                        }
                    });
        }else {
            Toast.makeText(context, "documentSnapshot is null", Toast.LENGTH_SHORT).show();
        }
    }



}

package com.nisith.firebaseauth;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyFirestorePagingAdapter extends FirestorePagingAdapter<Blog, MyFirestorePagingAdapter.MyViewHolder> {

    public MyFirestorePagingAdapter(@NonNull FirestorePagingOptions<Blog> options) {
        super(options);
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View parentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_layout_for_blog, parent, false);
        return new MyViewHolder(parentView);
    }



    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Blog blog) {
        holder.blogTitle.setText(blog.getBlogTitle());
        holder.blogCategory.setText("Category: "+blog.getBlogCategory());
        holder.publishedDate.setText("Published On: "+blog.getPublishedDate());
        holder.blogText.setText(blog.getBlog());
        holder.viewsTextView.setText(String.valueOf(blog.getTotalViews())+" Views");
        holder.likesTextView.setText(String.valueOf(blog.getTotalLikes())+" Likes");

    }


    @Override
    protected void onLoadingStateChanged(@NonNull LoadingState state) {
        super.onLoadingStateChanged(state);
        switch (state){

            case LOADING_MORE:
                Log.d("ASDF","Loading More...");
                break;
            case LOADED:
                Log.d("ASDF","Total Loaded= "+getItemCount());
                break;
            case FINISHED:
                Log.d("ASDF","finished");
                break;
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView blogTitle, blogCategory, publishedDate, blogText,
                 viewsTextView, likesTextView, readMoreTextView,
                 deleteTextView, editBlogTextView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            blogTitle = itemView.findViewById(R.id.blog_title_text_view);
            blogCategory = itemView.findViewById(R.id.blog_category_text_view);
            publishedDate = itemView.findViewById(R.id.published_date_text_view);
            blogText = itemView.findViewById(R.id.blog_text_view);
            viewsTextView = itemView.findViewById(R.id.views_text_view);
            likesTextView = itemView.findViewById(R.id.likes_text_view);
            readMoreTextView = itemView.findViewById(R.id.read_more_text_view);
            deleteTextView = itemView.findViewById(R.id.delete_text_view);
            editBlogTextView = itemView.findViewById(R.id.edit_blog_text_view);
        }
    }

}

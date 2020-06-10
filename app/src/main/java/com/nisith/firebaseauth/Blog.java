package com.nisith.firebaseauth;

public class Blog {
    private String blogTitle;
    private String blogCategory;
    private String blog;
    private String publishedDate;
    private long totalViews;
    private long totalLikes;
    private long totalDislikes;

    public Blog(){

    }

    public Blog(String blogTitle, String blogCategory, String blog, String publishedDate, long totalViews, long totalLikes, long totalDislikes) {
        this.blogTitle = blogTitle;
        this.blogCategory = blogCategory;
        this.blog = blog;
        this.publishedDate = publishedDate;
        this.totalViews = totalViews;
        this.totalLikes = totalLikes;
        this.totalDislikes = totalDislikes;
    }

    public String getBlogTitle() {
        return blogTitle;
    }

    public String getBlogCategory() {
        return blogCategory;
    }

    public String getBlog() {
        return blog;
    }
    public String getPublishedDate(){
        return publishedDate;
    }

    public long getTotalViews() {
        return totalViews;
    }

    public long getTotalLikes() {
        return totalLikes;
    }

    public long getTotalDislikes() {
        return totalDislikes;
    }
}

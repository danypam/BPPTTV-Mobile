package tv.bppt.app.dev.models.bookmark;

public class BookmarkModel {
    private int id;
    private int postId;
    private String postImageUrl;
    private String postTitle;
    private String postUrl;
    private String postCategory;
    private String formattedDate;

    public BookmarkModel(int id, int postId, String postImageUrl, String postTitle, String postUrl, String postCategory, String formattedDate) {
        this.id = id;
        this.postId = postId;
        this.postImageUrl = postImageUrl;
        this.postTitle = postTitle;
        this.postUrl = postUrl;
        this.postCategory = postCategory;
        this.formattedDate = formattedDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPostId() {
        return postId;
    }

    public String getPostImageUrl() {
        return postImageUrl;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public String getPostUrl() {
        return postUrl;
    }

    public String getPostCategory() {
        return postCategory;
    }

    public String getFormattedDate() {
        return formattedDate;
    }

}
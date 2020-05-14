package tv.bppt.app.dev.api;

import tv.bppt.app.dev.models.category.Category;
import tv.bppt.app.dev.models.comment.Comments;
import tv.bppt.app.dev.models.post.Post;
import tv.bppt.app.dev.models.post.PostDetails;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiInterface {

    @GET(HttpParams.API_CATEGORIES)
    Call<List<Category>> getCategories(@Query(HttpParams.PER_PAGE) int itemCount);


    @GET(HttpParams.API_POSTS)
    Call<List<Post>> getLatestPosts(@Query(HttpParams.PAGE) int pageNo);

    @GET(HttpParams.API_FEATURED_POSTS)
    Call<List<Post>> getFeaturedPosts(@Query(HttpParams.PAGE) int pageNo);


    @GET(HttpParams.API_POSTS)
    Call<List<Post>> getPostsByCategory(@Query(HttpParams.PAGE) int pageNo, @Query(HttpParams.CATEGORIES) int categoryId);

    @GET(HttpParams.API_POST_DETAILS)
    Call<PostDetails> getPostDetails(@Path(HttpParams.ID) int postId);

    @GET(HttpParams.API_POSTS)
    Call<List<Post>> getSearchedPosts(@Query(HttpParams.PAGE) int pageNo, @Query(HttpParams.SEARCH) String searchedText);

    @GET
    Call<List<Comments>> getComments(@Url String url, @Query(HttpParams.PER_PAGE) int pageCount);

    @FormUrlEncoded
    @POST(HttpParams.API_COMMENT)
    Call<String> postComment(@Field(HttpParams.COMMENT_AUTHOR_NAME) String name,
                             @Field(HttpParams.COMMENT_AUTHOR_EMAIL) String email,
                             @Field(HttpParams.COMMENT_CONTENT) String content,
                             @Query(HttpParams.POST) int postID);


}

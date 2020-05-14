package tv.bppt.app.dev.api;


public class HttpParams {

    public static final String PER_PAGE = "per_page";
    public static final String PAGE = "page";
    public static final String CATEGORIES = "categories";
    public static final String ID = "id";
    public static final String SEARCH = "search";
    public static final String POST = "post";

    public static final String API_CATEGORIES = "wp-json/wp/v2/categories?page=1&";
    public static final String API_POSTS = "wp-json/wp/v2/posts?per_page=10&_embed=true";
    public static final String API_FEATURED_POSTS = "wp-json/wp/v2/posts?per_page=10&_embed=true&sticky=true";
    public static final String API_POST_DETAILS = "wp-json/wp/v2/posts/{id}?&_embed=true";
    public static final String API_COMMENT = "wp-json/wp/v2/comments?";

    public static final String TOTAL_PAGE = "x-wp-totalpages";


    public static final String COMMENT_AUTHOR_NAME = "author_name";
    public static final String COMMENT_AUTHOR_EMAIL = "author_email";
    public static final String COMMENT_CONTENT = "content";

}

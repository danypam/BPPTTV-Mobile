package tv.bppt.app.dev.data.sqlite;

import android.provider.BaseColumns;

public class DbConstants implements BaseColumns {

    // commons
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String INTEGER_TYPE = " INTEGER";
    public static final String COLUMN_NAME_NULLABLE = null;

    // notification
    public static final String NOTIFICATION_TABLE_NAME = "notifications";

    public static final String COLUMN_NOTI_TITLE = "not_title";
    public static final String COLUMN_NOTI_MESSAGE = "not_message";
    public static final String COLUMN_NOTI_READ_STATUS = "not_status";
    public static final String COLUMN_NOTI_CONTENT_URL = "content_url";

    public static final String SQL_CREATE_NOTIFICATION_ENTRIES =
            "CREATE TABLE " + NOTIFICATION_TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY," +
                    COLUMN_NOTI_TITLE + TEXT_TYPE + COMMA_SEP +
                    COLUMN_NOTI_MESSAGE + TEXT_TYPE + COMMA_SEP +
                    COLUMN_NOTI_CONTENT_URL + TEXT_TYPE + COMMA_SEP +
                    COLUMN_NOTI_READ_STATUS + TEXT_TYPE +
                    " )";


    public static final String SQL_DELETE_NOTIFICATION_ENTRIES =
            "DROP TABLE IF EXISTS " + NOTIFICATION_TABLE_NAME;


    // favorite
    public static final String BOOKMARK_TABLE_NAME = "favourites";

    public static final String COLUMN_POST_ID = "post_id";
    public static final String COLUMN_POST_IMAGE = "post_image";
    public static final String COLUMN_POST_TITLE = "post_title";
    public static final String COLUMN_POST_URL = "post_url";
    public static final String COLUMN_POST_CATEGORY = "post_category";
    public static final String COLUMN_POST_DATE = "post_date";

    public static final String SQL_CREATE_FAVORITE_ENTRIES =
            "CREATE TABLE " + BOOKMARK_TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY," +
                    COLUMN_POST_ID + INTEGER_TYPE + COMMA_SEP +
                    COLUMN_POST_IMAGE + TEXT_TYPE + COMMA_SEP +
                    COLUMN_POST_TITLE + TEXT_TYPE + COMMA_SEP +
                    COLUMN_POST_URL + TEXT_TYPE + COMMA_SEP +
                    COLUMN_POST_CATEGORY + TEXT_TYPE + COMMA_SEP +
                    COLUMN_POST_DATE + TEXT_TYPE + " )";


    public static final String SQL_DELETE_FAVORITE_ENTRIES =
            "DROP TABLE IF EXISTS " + BOOKMARK_TABLE_NAME;

}

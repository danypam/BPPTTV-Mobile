package tv.bppt.app.dev.data.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import tv.bppt.app.dev.models.bookmark.BookmarkModel;

import java.util.ArrayList;

public class BookmarkDbController {

    private SQLiteDatabase db;

    public BookmarkDbController(Context context) {
        db = DbHelper.getInstance(context).getWritableDatabase();
    }

    public int insertData(int postId, String postImage, String postTitle, String postUrl, String postCategory, String postDate) {

        ContentValues values = new ContentValues();
        values.put(DbConstants.COLUMN_POST_ID, postId);
        values.put(DbConstants.COLUMN_POST_IMAGE, postImage);
        values.put(DbConstants.COLUMN_POST_TITLE, postTitle);
        values.put(DbConstants.COLUMN_POST_URL, postUrl);
        values.put(DbConstants.COLUMN_POST_CATEGORY, postCategory);
        values.put(DbConstants.COLUMN_POST_DATE, postDate);

        // Insert the new row, returning the primary key value of the new row
        return (int) db.insert(
                DbConstants.BOOKMARK_TABLE_NAME,
                DbConstants.COLUMN_NAME_NULLABLE,
                values);
    }

    public ArrayList<BookmarkModel> getAllData() {


        String[] projection = {
                DbConstants._ID,
                DbConstants.COLUMN_POST_IMAGE,
                DbConstants.COLUMN_POST_ID,
                DbConstants.COLUMN_POST_TITLE,
                DbConstants.COLUMN_POST_URL,
                DbConstants.COLUMN_POST_CATEGORY,
                DbConstants.COLUMN_POST_DATE
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder = DbConstants._ID + " DESC";

        Cursor c = db.query(
                DbConstants.BOOKMARK_TABLE_NAME,  // The table name to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        return fetchData(c);
    }

    private ArrayList<BookmarkModel> fetchData(Cursor c) {
        ArrayList<BookmarkModel> favDataArray = new ArrayList<>();

        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    // get  the  data into array,or class variable
                    int itemId = c.getInt(c.getColumnIndexOrThrow(DbConstants._ID));
                    int postId = c.getInt(c.getColumnIndexOrThrow(DbConstants.COLUMN_POST_ID));
                    String postImage = c.getString(c.getColumnIndexOrThrow(DbConstants.COLUMN_POST_IMAGE));
                    String postTitle = c.getString(c.getColumnIndexOrThrow(DbConstants.COLUMN_POST_TITLE));
                    String postUrl = c.getString(c.getColumnIndexOrThrow(DbConstants.COLUMN_POST_URL));
                    String postCategory = c.getString(c.getColumnIndexOrThrow(DbConstants.COLUMN_POST_CATEGORY));
                    String postDate = c.getString(c.getColumnIndexOrThrow(DbConstants.COLUMN_POST_DATE));


                    // wrap up data list and return
                    favDataArray.add(new BookmarkModel(itemId, postId, postImage, postTitle, postUrl, postCategory, postDate));
                } while (c.moveToNext());
            }
            c.close();
        }
        return favDataArray;
    }

    public void deleteEachFav(int postId) {
        // Which row to update, based on the ID
        String selection = DbConstants.COLUMN_POST_ID + "=?";
        String[] selectionArgs = {String.valueOf(postId)};

        db.delete(
                DbConstants.BOOKMARK_TABLE_NAME,
                selection,
                selectionArgs);
    }

    public void deleteAllFav() {
        db.delete(
                DbConstants.BOOKMARK_TABLE_NAME,
                null,
                null);
    }

}

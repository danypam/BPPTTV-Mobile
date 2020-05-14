package tv.bppt.app.dev.data.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import tv.bppt.app.dev.models.notification.NotificationModel;

import java.util.ArrayList;

public class NotificationDbController {

    private SQLiteDatabase mDb;

    private static final String mREAD = "read", mUNREAD = "unread";

    public NotificationDbController(Context context) {
        mDb = DbHelper.getInstance(context).getWritableDatabase();
    }

    public int insertData(String title, String message, String contentUrl) {

        ContentValues values = new ContentValues();
        values.put(DbConstants.COLUMN_NOTI_TITLE, title);
        values.put(DbConstants.COLUMN_NOTI_MESSAGE, message);
        values.put(DbConstants.COLUMN_NOTI_READ_STATUS, mUNREAD);
        values.put(DbConstants.COLUMN_NOTI_CONTENT_URL, contentUrl);

        // Insert the new row, returning the primary key value of the new row
        return (int) mDb.insert(
                DbConstants.NOTIFICATION_TABLE_NAME,
                DbConstants.COLUMN_NAME_NULLABLE,
                values);
    }

    public ArrayList<NotificationModel> getAllData() {


        String[] projection = {
                DbConstants._ID,
                DbConstants.COLUMN_NOTI_TITLE,
                DbConstants.COLUMN_NOTI_MESSAGE,
                DbConstants.COLUMN_NOTI_READ_STATUS,
                DbConstants.COLUMN_NOTI_CONTENT_URL
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder = DbConstants._ID + " DESC";

        Cursor c = mDb.query(
                DbConstants.NOTIFICATION_TABLE_NAME,  // The table name to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        return fetchData(c);
    }

    public ArrayList<NotificationModel> getUnreadData() {


        String[] projection = {
                DbConstants._ID,
                DbConstants.COLUMN_NOTI_TITLE,
                DbConstants.COLUMN_NOTI_MESSAGE,
                DbConstants.COLUMN_NOTI_READ_STATUS,
                DbConstants.COLUMN_NOTI_CONTENT_URL
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder = DbConstants._ID + " DESC";
        String selection = DbConstants.COLUMN_NOTI_READ_STATUS + "=?";
        String[] selectionArgs = {mUNREAD};

        Cursor c = mDb.query(
                DbConstants.NOTIFICATION_TABLE_NAME,  // The table name to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        return fetchData(c);
    }

    private ArrayList<NotificationModel> fetchData(Cursor c) {
        ArrayList<NotificationModel> ntyDataArray = new ArrayList<>();

        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    // get  the  data into array,or class variable
                    int itemId = c.getInt(c.getColumnIndexOrThrow(DbConstants._ID));
                    String title = c.getString(c.getColumnIndexOrThrow(DbConstants.COLUMN_NOTI_TITLE));
                    String message = c.getString(c.getColumnIndexOrThrow(DbConstants.COLUMN_NOTI_MESSAGE));
                    String status = c.getString(c.getColumnIndexOrThrow(DbConstants.COLUMN_NOTI_READ_STATUS));
                    String contentUrl = c.getString(c.getColumnIndexOrThrow(DbConstants.COLUMN_NOTI_CONTENT_URL));

                    boolean isUnread = !status.equals(mREAD);

                    // wrap up data list and return
                    ntyDataArray.add(new NotificationModel(itemId, title, message, isUnread, contentUrl));
                } while (c.moveToNext());
            }
            c.close();
        }
        return ntyDataArray;
    }

    public void updateStatus(int itemId, boolean read) {

        String readStatus = mUNREAD;
        if (read) {
            readStatus = mREAD;
        }

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(DbConstants.COLUMN_NOTI_READ_STATUS, readStatus);

        // Which row to update, based on the ID
        String selection = DbConstants._ID + "=?";
        String[] selectionArgs = {String.valueOf(itemId)};

        mDb.update(
                DbConstants.NOTIFICATION_TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }

    // Delete All Notifications
    public void deleteAllNot() {
        mDb.delete(
                DbConstants.NOTIFICATION_TABLE_NAME,
                null,
                null);
    }

}

package com.gscasu.yourwords;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

import com.gscasu.yourwords.data.WordsContract;
import com.gscasu.yourwords.data.WordsDbHelper;

/**
 * Created by Sa'eed Abdullah on 022, 22, 2, 2015.
 */
public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    public void deleteAllRecords(){
        mContext.getContentResolver().delete(
                WordsContract.WordsEntry.CONTENT_URI,
                null,
                null
        );


        Cursor cursor = mContext.getContentResolver().query(
                WordsContract.WordsEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        assertEquals(0, cursor.getCount());
        cursor.close();
    }

    public void setUp(){
        deleteAllRecords();
    }

    public void testInsertReadProvider() {

        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        WordsDbHelper dbHelper = new WordsDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestDb.createBlackWordContentValues();

        long id;
        Uri insertUri = mContext.getContentResolver().insert(WordsContract.WordsEntry.CONTENT_URI, testValues);
        id = ContentUris.parseId(insertUri);

        // Verify we got a row back.
        assertTrue(id != -1);
        Log.d(LOG_TAG, "New row id: " + id);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        Cursor cursor = db.query(
                WordsContract.WordsEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );
        TestDb.validateCursor(cursor, testValues);

        cursor = mContext.getContentResolver().query(
                WordsContract.WordsEntry.buildHeadword(TestDb.TEST_WORD),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        TestDb.validateCursor(cursor, testValues);

        cursor = mContext.getContentResolver().query(
                WordsContract.WordsEntry.buildHeadwordWithOnlineId(TestDb.TEST_WORD,
                        TestDb.TEST_ID),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        TestDb.validateCursor(cursor, testValues);

        dbHelper.close();
        cursor.close();
    }

    public void testUpdateDb(){
        // Create a new map of values, where column names are the keys
        ContentValues values = TestDb.createBlackWordContentValues();

        Uri uri = mContext.getContentResolver().
                insert(WordsContract.WordsEntry.CONTENT_URI, values);
        long id = ContentUris.parseId(uri);

        // Verify we got a row back.
        assertTrue(id != -1);
        Log.d(LOG_TAG, "New row id: " + id);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(WordsContract.WordsEntry._ID, id);
        updatedValues.put(WordsContract.WordsEntry.COLUMN_HEADWORD, "irony");

        int count = mContext.getContentResolver().update(
                WordsContract.WordsEntry.CONTENT_URI, updatedValues, WordsContract.WordsEntry._ID + "= ?",
                new String[] { Long.toString(id)});

        assertEquals(count, 1);

        Cursor cursor = mContext.getContentResolver().query(
                WordsContract.WordsEntry.buildHeadword("irony"),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestDb.validateCursor(cursor, updatedValues);
    }

    public void testDeleteRecordsAtTheEnd(){
        deleteAllRecords();
    }

    public void testGetType() {
        // content://com.jscasu.yourwords/words/
        String type = mContext.getContentResolver().getType(WordsContract.WordsEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.jscasu.yourwords/words
        assertEquals(WordsContract.WordsEntry.CONTENT_TYPE, type);

        // content://com.jscasu.yourwords/words/black
        type = mContext.getContentResolver().getType(
                WordsContract.WordsEntry.buildHeadword(TestDb.TEST_WORD));
        // vnd.android.cursor.dir/com.jscasu.yourwords/words
        assertEquals(WordsContract.WordsEntry.CONTENT_TYPE, type);

        // content://com.jscasu.yourwords/words/black/cqAF0BSam6
        type = mContext.getContentResolver().getType(
                WordsContract.WordsEntry.buildHeadwordWithOnlineId(TestDb.TEST_WORD, TestDb.TEST_ID));
        // vnd.android.cursor.item/com.jscasu.yourwords/words
        assertEquals(WordsContract.WordsEntry.CONTENT_ITEM_TYPE, type);
    }
}
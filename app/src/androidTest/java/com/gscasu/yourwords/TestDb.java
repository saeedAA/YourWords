package com.gscasu.yourwords;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import com.gscasu.yourwords.data.WordsContract;
import com.gscasu.yourwords.data.WordsDbHelper;

import java.util.Map;
import java.util.Set;

/**
 * Created by Sa'eed Abdullah on 022, 22, 2, 2015.
 */
public class TestDb extends AndroidTestCase {

    static final String TEST_WORD = "black";
    static final String TEST_ID = "cqAF0BSam6";
    static final String TEST_POS = "adjective";
    static final String TEST_DEF = "having the darkest colour";
    static final String TEST_TRANS = "blæk";
    static final String TEST_EXAMPLE = "a black evening dress";

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    public void testCreateDb() throws Throwable {
        mContext.deleteDatabase(WordsDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new WordsDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }

    public void testInsertReadDb() {

        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        WordsDbHelper dbHelper = new WordsDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = createBlackWordContentValues();

        long locationRowId;
        locationRowId = db.insert(WordsContract.WordsEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row id: " + locationRowId);

        //Pulling inserted dummy data back
        Cursor cursor = db.query(
                WordsContract.WordsEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        validateCursor(cursor, testValues);

        dbHelper.close();
    }

    static void validateCursor(Cursor valueCursor, ContentValues expectedValues) {

        assertTrue(valueCursor.moveToFirst());

        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse(idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals(expectedValue, valueCursor.getString(idx));
        }
        valueCursor.close();
    }

    static ContentValues createBlackWordContentValues() {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(WordsContract.WordsEntry.COLUMN_ONLINE_ID, TEST_ID);
        testValues.put(WordsContract.WordsEntry.COLUMN_HEADWORD, TEST_WORD);
        testValues.put(WordsContract.WordsEntry.COLUMN_DEFINITION, TEST_DEF);
        testValues.put(WordsContract.WordsEntry.COLUMN_PART_OF_SPEECH, TEST_POS);
        testValues.put(WordsContract.WordsEntry.COLUMN_EXAMPLES, TEST_EXAMPLE);
        testValues.put(WordsContract.WordsEntry.COLUMN_TRANSCRIPTION, TEST_TRANS);

        return testValues;
    }
}
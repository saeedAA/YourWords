package com.gscasu.yourwords.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class WordsDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "words.db";


    public WordsDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a table to hold words.  A word consists of the string supplied in the
        // headword, definition, part of speech, transcription and examples. Examples and
        // transcription can be null
        final String SQL_CREATE_WORDS_TABLE = "CREATE TABLE " +
                WordsContract.WordsEntry.TABLE_NAME + " (" +
                WordsContract.WordsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                WordsContract.WordsEntry.COLUMN_ONLINE_ID + " TEXT UNIQUE NOT NULL, " +
                WordsContract.WordsEntry.COLUMN_HEADWORD + " TEXT NOT NULL, " +
                WordsContract.WordsEntry.COLUMN_PART_OF_SPEECH + " TEXT NOT NULL, " +
                WordsContract.WordsEntry.COLUMN_DEFINITION + " TEXT NOT NULL, " +
                WordsContract.WordsEntry.COLUMN_TRANSCRIPTION + " TEXT, " +
                WordsContract.WordsEntry.COLUMN_EXAMPLES + " TEXT, " +
                "UNIQUE (" + WordsContract.WordsEntry.COLUMN_ONLINE_ID +") ON CONFLICT IGNORE"+
                " );";

        final String SQL_CREATE_OFFLINE_TABLE = "CREATE TABLE " +
                WordsContract.OfflineEntry.TABLE_NAME + " (" +
                WordsContract.OfflineEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                WordsContract.OfflineEntry.COLUMN_OFFLINE_WORD + " TEXT UNIQUE NOT NULL, " +
                "UNIQUE (" + WordsContract.OfflineEntry.COLUMN_OFFLINE_WORD +") ON CONFLICT IGNORE"+
                " );";

        db.execSQL(SQL_CREATE_WORDS_TABLE);
        db.execSQL(SQL_CREATE_OFFLINE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + WordsContract.WordsEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + WordsContract.OfflineEntry.TABLE_NAME);
        onCreate(db);
    }
}

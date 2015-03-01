package com.gscasu.yourwords.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by Sa'eed Abdullah on 022, 22, 2, 2015.
 */
public class WordsProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private WordsDbHelper mOpenHelper;

    private static final int WORD = 100;
    private static final int WORD_WITH_HEADWORD = 101;
    private static final int WORD_WITH_HEADWORD_AND_ONLINE_ID = 102;
    private static final int OFFLINE = 103;
    private static final int OFFLINE_WORD = 104;

    private static final String sHeadwordSelection =
            WordsContract.WordsEntry.TABLE_NAME+
                    "." + WordsContract.WordsEntry.COLUMN_HEADWORD + " LIKE ? ";

    private static final String sHeadwordAndOnlineIdSelection =
            WordsContract.WordsEntry.TABLE_NAME +
                    "." + WordsContract.WordsEntry.COLUMN_HEADWORD + " = ? AND " +
                    WordsContract.WordsEntry.COLUMN_ONLINE_ID + " = ? ";

    private static final String sOfflineWordSelection =
            WordsContract.OfflineEntry.TABLE_NAME +
                    "." + WordsContract.OfflineEntry.COLUMN_OFFLINE_WORD + " = ? ";

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = WordsContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, WordsContract.PATH_WORDS, WORD);
        matcher.addURI(authority, WordsContract.PATH_WORDS + "/*", WORD_WITH_HEADWORD);
        matcher.addURI(authority, WordsContract.PATH_WORDS + "/*/*", WORD_WITH_HEADWORD_AND_ONLINE_ID);
        matcher.addURI(authority, WordsContract.PATH_OFFLINE, OFFLINE);
        matcher.addURI(authority, WordsContract.PATH_OFFLINE + "/*", OFFLINE_WORD);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new WordsDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case WORD: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        WordsContract.WordsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case WORD_WITH_HEADWORD: {

                String headWord = WordsContract.WordsEntry.getHeadwordFromUri(uri);

                retCursor = mOpenHelper.getReadableDatabase().query(
                        WordsContract.WordsEntry.TABLE_NAME,
                        projection,
                        sHeadwordSelection,
                        new String[] {headWord},
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case WORD_WITH_HEADWORD_AND_ONLINE_ID: {

                String headWord = WordsContract.WordsEntry.getHeadwordFromUri(uri);
                String onlineId = WordsContract.WordsEntry.getOnlineIdFromUri(uri);

                retCursor = mOpenHelper.getReadableDatabase().query(
                        WordsContract.WordsEntry.TABLE_NAME,
                        projection,
                        sHeadwordAndOnlineIdSelection,
                        new String[]{headWord, onlineId},
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case OFFLINE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        WordsContract.OfflineEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case OFFLINE_WORD: {
                String offlineWord = WordsContract.OfflineEntry.getOfflineWordFromUri(uri);

                retCursor = mOpenHelper.getReadableDatabase().query(
                        WordsContract.OfflineEntry.TABLE_NAME,
                        projection,
                        sOfflineWordSelection,
                        new String[]{offlineWord},
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case WORD:
                return WordsContract.WordsEntry.CONTENT_TYPE;
            case WORD_WITH_HEADWORD:
                return WordsContract.WordsEntry.CONTENT_TYPE;
            case WORD_WITH_HEADWORD_AND_ONLINE_ID:
                return WordsContract.WordsEntry.CONTENT_ITEM_TYPE;
            case OFFLINE:
                return WordsContract.OfflineEntry.CONTENT_TYPE;
            case OFFLINE_WORD:
                return WordsContract.OfflineEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        long _id;
        switch (match) {

            case WORD:
                _id = db.insert(WordsContract.WordsEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = WordsContract.WordsEntry.buildWordUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            case OFFLINE:
                _id = db.insert(WordsContract.OfflineEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = WordsContract.OfflineEntry.buildOfflineUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        switch (match) {
            case WORD:
                rowsDeleted = db.delete(
                        WordsContract.WordsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            case OFFLINE:
                rowsDeleted = db.delete(
                        WordsContract.OfflineEntry.TABLE_NAME, selection, selectionArgs);
                break;
        }

        if (selection == null || rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;
        switch (match) {
            case WORD:
                rowsUpdated = db.update(WordsContract.WordsEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case WORD:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(WordsContract.WordsEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

}

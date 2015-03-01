package com.gscasu.yourwords.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Sa'eed Abdullah on 021, 21, 2, 2015.
 */
public class WordsContract {
    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.gscasu.yourwords";
    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.example.android.sunshine.app/weather/ is a valid path for
    // looking at weather data. content://com.example.android.sunshine.app/givemeroot/ will fail,
    // as the ContentProvider hasn't been given any information on what to do with "givemeroot".
    // At least, let's hope not.  Don't be that dev, reader.  Don't be that dev.
    public static final String PATH_WORDS = "words";
    public static final String PATH_OFFLINE = "offline";

    public static final class WordsEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_WORDS).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_WORDS;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_WORDS;

        // Table name
        public static final String TABLE_NAME = "words";

        public static final String COLUMN_ONLINE_ID = "online_id";

        //word phrase is the word it self being defined
        public static final String COLUMN_HEADWORD = "headword";

        //part of speech ex: verb, noun or adj
        public static final String COLUMN_PART_OF_SPEECH = "part_of_speech";

        //definition is the explanation of the headword
        public static final String COLUMN_DEFINITION = "definition";

        //transcription column is the pronunciation text
        public static final String COLUMN_TRANSCRIPTION = "ipa";

        //examples column contains the examples available separated by \n
        public static final String COLUMN_EXAMPLES = "examples";

        public static Uri buildWordUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildHeadword(String headWord) {
            return CONTENT_URI.buildUpon().appendPath(headWord).build();
        }
        public static Uri buildHeadwordWithOnlineId(String headWord, String onlineId) {
            return CONTENT_URI.buildUpon().appendPath(headWord).appendPath(onlineId).build();
        }

        public static String getHeadwordFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static String getOnlineIdFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }

    }

    public static final class OfflineEntry implements BaseColumns{
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_OFFLINE).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_OFFLINE;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_OFFLINE;

        // Table name
        public static final String TABLE_NAME = "offline";

        //offline word column
        public static final String COLUMN_OFFLINE_WORD = "offline_word";

        public static Uri buildOfflineUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildOfflineWord(String OfflineWord) {
            return CONTENT_URI.buildUpon().appendPath(OfflineWord).build();
        }

        public static String getOfflineWordFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }

}

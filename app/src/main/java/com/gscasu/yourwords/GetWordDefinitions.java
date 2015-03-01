package com.gscasu.yourwords;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.gscasu.yourwords.data.WordsContract;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Sa'eed Abdullah on 022, 22, 2, 2015.
 */
public class GetWordDefinitions extends AsyncTask<String, Void, Void> {
    private final String LOG_TAG = GetWordDefinitions.class.getSimpleName();
    private Context mContext;

    public GetWordDefinitions(Context context){
        mContext = context;
    }

    @Override
    protected Void doInBackground(String... params) {
        HttpURLConnection urlConnection;
        BufferedReader reader = null;
        String responseStr = null;

        try{
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("https").authority("api.pearson.com").appendPath("v2")
                    .appendPath("dictionaries")
                    .appendPath("ldoce5")
                    .appendPath("entries")
                    .appendQueryParameter("headword",params[0]);


            URL url = new URL(builder.toString());
            Log.v(LOG_TAG, url.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            Log.v(LOG_TAG, "word sent..");

            //Receive into input stream
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            responseStr = buffer.toString();
            Log.v(LOG_TAG,responseStr);

            WordsParser wordsParser = new WordsParser();
            addWordsToDb(wordsParser.getWordsFromJson(responseStr));

            return null;

        } catch (MalformedURLException e) {
            Log.e(LOG_TAG,e.toString());
            e.printStackTrace();
        } catch (ProtocolException e) {
            Log.e(LOG_TAG,e.toString());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(LOG_TAG,e.toString());
            e.printStackTrace();
        } catch (JSONException e) {
            Log.e(LOG_TAG,e.toString());
            e.printStackTrace();
        }
        return null;
    }

    private void addWordsToDb(Word[] words){
        ArrayList<ContentValues> wordsList = new ArrayList<ContentValues>();
        for(int i=0; i<words.length; i++){
            ContentValues wordsValues = new ContentValues();

            wordsValues.put(WordsContract.WordsEntry.COLUMN_ONLINE_ID,
                    words[i].getOnlineId());
            wordsValues.put(WordsContract.WordsEntry.COLUMN_HEADWORD,
                    words[i].getHeadWord());
            wordsValues.put(WordsContract.WordsEntry.COLUMN_PART_OF_SPEECH,
                    words[i].getPartOfSpeech());
            wordsValues.put(WordsContract.WordsEntry.COLUMN_DEFINITION,
                    words[i].getDefinition());
            wordsValues.put(WordsContract.WordsEntry.COLUMN_TRANSCRIPTION,
                    words[i].getTranscription());
            wordsValues.put(WordsContract.WordsEntry.COLUMN_EXAMPLES,
                    words[i].getExamples());

            wordsList.add(wordsValues);
        }

        if(wordsList.size()>0){
            ContentValues[] cvArray = new ContentValues[wordsList.size()];
            wordsList.toArray(cvArray);
            mContext.getContentResolver()
                    .bulkInsert(WordsContract.WordsEntry.CONTENT_URI, cvArray);
        }

        Cursor cursor = mContext.getContentResolver().query(
                WordsContract.WordsEntry.buildHeadword("black"),
                null,
                null,
                null,
                null
        );

        Log.v(LOG_TAG, "inserted: " + cursor.getCount());
    }
}
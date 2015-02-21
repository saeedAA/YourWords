package com.gscasu.yourwords;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class WordsFragment extends Fragment {

    public WordsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_words, container, false);
        return rootView;
    }

    @Override
    public void onStart() {
        GetWordDefinitions getWordDefinitions = new GetWordDefinitions();
        getWordDefinitions.execute();
        super.onStart();
    }

    private class GetWordDefinitions extends AsyncTask<Void, Void, Void> {
        private final String LOG_TAG = GetWordDefinitions.class.getSimpleName();

        @Override
        protected Void doInBackground(Void... params) {
            HttpURLConnection urlConnection;
            BufferedReader reader = null;
            String responseStr = null;

            try{
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("https").authority("api.pearson.com").appendPath("v2")
                        .appendPath("dictionaries")
                        .appendPath("ldoce5")
                        .appendPath("entries")
                        .appendQueryParameter("headword","black");


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

                return null;

            } catch (MalformedURLException e) {
                Log.e(LOG_TAG,e.toString());
            } catch (ProtocolException e) {
                Log.e(LOG_TAG,e.toString());
            } catch (IOException e) {
                Log.e(LOG_TAG, e.toString());
            }
            return null;
        }

    }
}

package com.gscasu.yourwords;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Sa'eed Abdullah on 022, 22, 2, 2015.
 */
public class WordsParser {
    private final String LOG_TAG = WordsParser.class.getSimpleName();

    private final String LONGMAN_COUNT = "count";
    private final String LONGMAN_RESULTS = "results";
    private final String LONGMAN_HEADWORD = "headword";
    private final String LONGMAN_ONLINE_ID = "id";
    private final String LONGMAN_DEFINITION = "definition";
    private final String LONGMAN_POS = "part_of_speech";
    private final String LONGMAN_TRANSCRIPTION = "ipa";
    private final String LONGMAN_EXAMPLE = "examples";
    private final String LONGMAN_SENSES = "senses";
    private final String LONGMAN_PRONUNCIATIONS  = "pronunciations";
    private final String LONGMAN_EXAMPLE_TEXT = "text";

    public Word[] getWordsFromJson(String wordsJsonStr) throws JSONException {

        JSONObject receivedStrObject = new JSONObject(wordsJsonStr);
        int receivedCount = receivedStrObject.getInt(LONGMAN_COUNT);
        JSONArray resultsArray = receivedStrObject.getJSONArray(LONGMAN_RESULTS);
        Word[] words = new Word[receivedCount];

        for(int i=0; i<resultsArray.length(); i++){
            JSONObject result = resultsArray.getJSONObject(i);
            JSONArray senses = result.getJSONArray(LONGMAN_SENSES);
            JSONObject sens0 = senses.getJSONObject(0);

            String headWord = result.getString(LONGMAN_HEADWORD);
            String onlineId = result.getString(LONGMAN_ONLINE_ID);
            String definition0 = sens0.getJSONArray(LONGMAN_DEFINITION).getString(0);
            String partOfSpeech = "";

            if(result.has(LONGMAN_POS))
                partOfSpeech = result.getString(LONGMAN_POS);

            words[i] = new Word(onlineId, headWord, definition0, partOfSpeech);

            Log.v(LOG_TAG, words[i].getHeadWord() + ": " + words[i].getDefinition() + "; " + partOfSpeech);

            if(sens0.has(LONGMAN_EXAMPLE)) {
                JSONObject example0 = sens0.getJSONArray(LONGMAN_EXAMPLE).getJSONObject(0);
                String example0Text = example0.getString(LONGMAN_EXAMPLE_TEXT);
                words[i].setExamples(example0Text);
            }

            if(result.has(LONGMAN_PRONUNCIATIONS)) {
                JSONObject pronunciation0 = result.getJSONArray(LONGMAN_PRONUNCIATIONS).getJSONObject(0);
                String transcription = pronunciation0.getString(LONGMAN_TRANSCRIPTION);
                words[i].setTranscription(transcription);
            }
        }
        return words;
    }
}

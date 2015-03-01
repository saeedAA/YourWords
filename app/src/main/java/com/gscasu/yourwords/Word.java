package com.gscasu.yourwords;

/**
 * Created by Sa'eed Abdullah on 022, 22, 2, 2015.
 */
public class Word {
    private String mOnlineId;
    private String mHeadWord;
    private String mDefinition;
    private String mPartOfSpeech;
    private String mTranscription;
    private String mExamples;

    public Word(String onlineId, String headWord, String definition, String partOfSpeech){
        mOnlineId = onlineId;
        mHeadWord = headWord;
        mDefinition = definition;
        mPartOfSpeech = partOfSpeech;
        mTranscription = "";
        mExamples = "";
    }

    public void setExamples(String examples){ mExamples = examples; }

    public void setTranscription(String transcription){ mTranscription = transcription; }

    public String getOnlineId(){ return mOnlineId; }

    public String getHeadWord(){ return mHeadWord; }

    public String getDefinition(){ return mDefinition; }

    public String getPartOfSpeech(){ return mPartOfSpeech; }

    public String getTranscription(){ return mTranscription; }

    public String getExamples(){ return mExamples; }
}

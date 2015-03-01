package com.gscasu.yourwords;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.gscasu.yourwords.data.WordsContract;
import com.gscasu.yourwords.service.WordsService;

import java.util.ArrayList;

public class WordsFragment extends Fragment implements LoaderCallbacks<Cursor>{

    EditText mSearchText;
    Button mSearchButton;

    private final int WORDS_LOADER = 0;

    private final String[] WORDS_COLUMNS = {
            WordsContract.WordsEntry._ID,
            WordsContract.WordsEntry.COLUMN_ONLINE_ID,
            WordsContract.WordsEntry.COLUMN_HEADWORD,
            WordsContract.WordsEntry.COLUMN_PART_OF_SPEECH,
    };

    public static final int COL_ID = 0;
    public static final int COL_ONLINE_ID = 1;
    public static final int COL_HEADWORD = 2;
    public static final int COL_PORT_OF_SPEECH = 3;


    private static final String SCROLL_POSITION_KEY = "scroll_pos";

    private WordsAdapter mWordsAdapter = null;
    private ListView mWordsListView;
    private int mScrollPosition;

    public WordsFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(WORDS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(savedInstanceState != null && savedInstanceState.containsKey(SCROLL_POSITION_KEY)) {
            mScrollPosition = savedInstanceState.getInt(SCROLL_POSITION_KEY);
        }
        View rootView = inflater.inflate(R.layout.words_fragment, container, false);
        mWordsListView = (ListView) rootView.findViewById(R.id.words_listView);
        mSearchText = (EditText) rootView.findViewById(R.id.search_words_edit_text);
        mSearchButton = (Button) rootView.findViewById(R.id.search_words_btn);
        Button clear = (Button) rootView.findViewById(R.id.clear_words_btn);

        clear.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                getActivity().getContentResolver().delete(
                        WordsContract.WordsEntry.CONTENT_URI,
                        null,
                        null
                );
            }
        });

        mWordsAdapter = new WordsAdapter(getActivity(),null,0);
        mWordsListView.setAdapter(mWordsAdapter);

        mWordsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Cursor cursor = mWordsAdapter.getCursor();
                if(cursor != null) {
                    cursor.moveToPosition(position);
                    mScrollPosition = position;
                    //mScrollPosition = position;
                    ((Callback)getActivity()).onItemSelected(cursor.getString(COL_ONLINE_ID),
                            cursor.getString(COL_HEADWORD));
                }
            }
        });

        mSearchButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WordsService.class);
                intent.putExtra(DetailActivity.HEADWORD_KEY, mSearchText.getText().toString());
                getActivity().startService(intent);
            }
        });

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v("LoaderCursor", "updating..");
        return new CursorLoader(
                getActivity(),
                WordsContract.WordsEntry.buildHeadword(mSearchText.getText().toString()),
                WORDS_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v("LoaderCursor", "Count of: " + data.getCount());
        mWordsAdapter.swapCursor(data);
        mWordsListView.smoothScrollToPosition(mScrollPosition);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mWordsAdapter.swapCursor(null);
    }

    public interface Callback {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(String onlineId, String headword);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SCROLL_POSITION_KEY, mScrollPosition);
    }
}

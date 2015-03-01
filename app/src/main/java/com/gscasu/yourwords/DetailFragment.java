package com.gscasu.yourwords;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.support.v4.app.LoaderManager.LoaderCallbacks;

import com.gscasu.yourwords.data.WordsContract;

public class DetailFragment extends Fragment implements LoaderCallbacks<Cursor> {
    private TextView mHeadwordView;
    private TextView mPosView;
    private TextView mDefinitionView;
    private TextView mTranscriptionView;
    private TextView mExampleView;
    private String mWordShare;
    private ShareActionProvider mShareActionProvider;

    private final String LOG_TAG = DetailFragment.class.getSimpleName();

    public final int DETAILS_LOADER = 1;

    private final String[] WORDS_COLUMNS = {
            WordsContract.WordsEntry._ID,
            WordsContract.WordsEntry.COLUMN_HEADWORD,
            WordsContract.WordsEntry.COLUMN_PART_OF_SPEECH,
            WordsContract.WordsEntry.COLUMN_DEFINITION,
            WordsContract.WordsEntry.COLUMN_TRANSCRIPTION,
            WordsContract.WordsEntry.COLUMN_EXAMPLES
    };

    public static final int COL_ID = 0;
    public static final int COL_HEADWORD = 1;
    public static final int COL_POS = 2;
    public static final int COL_DEF = 3;
    public static final int COL_TRANS = 4;
    public static final int COL_EXAMPLE = 5;

    String mHeadword;
    String mOnlineId;
    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_detail, menu);
        MenuItem item = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareWordIntent());
        } else {
            Log.d(LOG_TAG, "Actionprovider equals null");
        }
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAILS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);


        mHeadwordView = (TextView) rootView.findViewById(R.id.detail_headword_view);
        mPosView = (TextView) rootView.findViewById(R.id.detail_pos_view);
        mDefinitionView = (TextView) rootView.findViewById(R.id.detail_definition_view);
        mTranscriptionView = (TextView) rootView.findViewById(R.id.detail_transcription_view);
        mExampleView = (TextView) rootView.findViewById(R.id.detail_example_view);

        Bundle args = getArguments();
        if(args != null && args.containsKey(DetailActivity.HEADWORD_KEY) && args.containsKey(DetailActivity.ONLINE_ID_KEY)){
            mHeadword = args.getString(DetailActivity.HEADWORD_KEY);
            mOnlineId = args.getString(DetailActivity.ONLINE_ID_KEY);
            Log.v(LOG_TAG, "Headword: " + mHeadword + " with online Id: " + mOnlineId);
        }


        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "getting word from database");
        return new CursorLoader(
                getActivity(),
                WordsContract.WordsEntry.buildHeadwordWithOnlineId(mHeadword,mOnlineId),
                WORDS_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data.moveToFirst()){
            mHeadwordView.setText(data.getString(COL_HEADWORD));
            mPosView.setText(data.getString(COL_POS));
            mDefinitionView.setText(data.getString(COL_DEF));
            mTranscriptionView.setText(data.getString(COL_TRANS));
            mExampleView.setText(data.getString(COL_EXAMPLE));
            mWordShare = data.getString(COL_HEADWORD) + "\n"
                    + data.getString(COL_POS) + "\n"
                    + data.getString(COL_DEF) + "\n"
                    + data.getString(COL_TRANS) + "\n"
                    + data.getString(COL_EXAMPLE) + "\n";
            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareWordIntent());
            } else {
                Log.d(LOG_TAG, "Actionprovider equals null");
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private Intent createShareWordIntent() {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.putExtra(Intent.EXTRA_TEXT, mWordShare + " #YourWords");
        shareIntent.setType("text/plain");
        return shareIntent;
    }
}

package com.gscasu.yourwords;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Sa'eed Abdullah on 024, 24, 2, 2015.
 */
public class WordsAdapter extends CursorAdapter {

    public WordsAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_words,
                parent,
                false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String headWordStr = cursor.getString(WordsFragment.COL_HEADWORD);
        String posStr = cursor.getString(WordsFragment.COL_PORT_OF_SPEECH);

        viewHolder.headwordView.setText(headWordStr);
        viewHolder.posView.setText(posStr);
    }

    public static class ViewHolder{
        public final TextView headwordView;
        public final TextView posView;

        public ViewHolder(View view){
            headwordView = (TextView) view.findViewById(R.id.list_item_headword_textview);
            posView = (TextView) view.findViewById(R.id.list_item_pos_textview);
        }
    }
}

package com.gscasu.yourwords;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;


public class DetailActivity extends ActionBarActivity {

    public static final String HEADWORD_KEY = "headword_key";
    public static final String ONLINE_ID_KEY = "online_id_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        if(intent != null && intent.hasExtra(HEADWORD_KEY) && intent.hasExtra(ONLINE_ID_KEY)) {
            String headword = intent.getExtras().getString(HEADWORD_KEY);
            String onlineId = intent.getExtras().getString(ONLINE_ID_KEY);
            Bundle args = new Bundle();
            args.putString(HEADWORD_KEY, headword);
            args.putString(ONLINE_ID_KEY, onlineId);
            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(args);

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, detailFragment)
                        .commit();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

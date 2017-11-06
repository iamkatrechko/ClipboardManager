package com.iamkatrechko.clipboardmanager.view.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.iamkatrechko.clipboardmanager.R;
import com.iamkatrechko.clipboardmanager.view.fragment.SearchFragment;

/**
 * Активность поиска заметок
 * @author iamkatrechko
 *         Date: 01.11.2016
 */
public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getFragments().isEmpty()) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, SearchFragment.Companion.newInstance())
                    .commit();
        }
    }
}

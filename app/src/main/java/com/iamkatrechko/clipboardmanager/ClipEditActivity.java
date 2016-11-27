package com.iamkatrechko.clipboardmanager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class ClipEditActivity extends AppCompatActivity {

    /**
     * Возвращает интент активности
     * @param clipUri Uri редактируемой записи
     */
    public static Intent newIntent(Context context, Uri clipUri){
        Intent intent = new Intent(context, ClipEditActivity.class);
        intent.putExtra("URI", clipUri);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clip_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Uri uri = getIntent().getParcelableExtra("URI");

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getFragments() == null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, ClipEditFragment.newInstance(uri))
                    .commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        ClipEditFragment clipsFragment = (ClipEditFragment) getSupportFragmentManager().findFragmentById(R.id.container);
        clipsFragment.backButtonWasPressed();
    }
}

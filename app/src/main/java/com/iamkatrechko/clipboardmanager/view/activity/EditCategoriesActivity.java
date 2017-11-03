package com.iamkatrechko.clipboardmanager.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.iamkatrechko.clipboardmanager.R;
import com.iamkatrechko.clipboardmanager.view.fragment.CategoriesListFragment;

/**
 * Активность редактирования категории
 * @author iamkatrechko
 *         Date: 01.11.2016
 */
public class EditCategoriesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_categories);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CategoriesListFragment clipsFragment =
                        (CategoriesListFragment) getSupportFragmentManager().getFragments().get(0);
                clipsFragment.showDialogAdd();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}

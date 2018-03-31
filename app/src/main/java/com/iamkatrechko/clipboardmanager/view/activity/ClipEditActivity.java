package com.iamkatrechko.clipboardmanager.view.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.iamkatrechko.clipboardmanager.R;
import com.iamkatrechko.clipboardmanager.databinding.ActivityClipEditBinding;
import com.iamkatrechko.clipboardmanager.view.fragment.ClipEditFragment;

/**
 * Активность экрана редактирования заметки
 * @author iamkatrechko
 *         Date: 01.11.2016
 */
public class ClipEditActivity extends AppCompatActivity {

    /** Ключ аргумента. Uri заметки */
    private static final String KEY_URI = "KEY_URI";

    /**
     * Возвращает интент активности
     * @param clipUri URI редактируемой записи
     */
    public static Intent newIntent(Context context, @Nullable Long clipUri) {
        Intent intent = new Intent(context, ClipEditActivity.class);
        intent.putExtra(KEY_URI, clipUri);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityClipEditBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_clip_edit);
        setSupportActionBar(binding.toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Long uri = getIntent().getLongExtra(KEY_URI, -1);
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getFragments().isEmpty()) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, ClipEditFragment.Companion.newInstance(uri))
                    .commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        ClipEditFragment clipsFragment = (ClipEditFragment) getSupportFragmentManager().findFragmentById(R.id.container);
        clipsFragment.onBackPressed();
    }
}

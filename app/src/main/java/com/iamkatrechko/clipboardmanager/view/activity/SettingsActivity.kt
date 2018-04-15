package com.iamkatrechko.clipboardmanager.view.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.iamkatrechko.clipboardmanager.view.fragment.SettingsFragment

/**
 *
 * @author iamkatrechko
 *         Date: 15.04.2018
 */
class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fragmentManager.beginTransaction()
                .replace(android.R.id.content, SettingsFragment())
                .commit()
    }
}
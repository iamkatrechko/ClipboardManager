package com.iamkatrechko.clipboardmanager.view.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.iamkatrechko.clipboardmanager.R
import com.iamkatrechko.clipboardmanager.view.fragment.SearchFragment

/**
 * Активность поиска заметок
 * @author iamkatrechko
 *         Date: 01.11.2016
 */
class SearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setSupportActionBar(findViewById(R.id.toolbar))

        val fragmentManager = supportFragmentManager
        if (fragmentManager.fragments.isEmpty()) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, SearchFragment.newInstance())
                    .commit()
        }
    }
}

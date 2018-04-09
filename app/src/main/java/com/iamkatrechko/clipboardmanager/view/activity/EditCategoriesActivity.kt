package com.iamkatrechko.clipboardmanager.view.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.iamkatrechko.clipboardmanager.R
import com.iamkatrechko.clipboardmanager.view.fragment.CategoriesListFragment

/**
 * Активность редактирования категории
 * @author iamkatrechko
 *         Date: 01.11.2016
 */
class EditCategoriesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_categories)
        setSupportActionBar(findViewById(R.id.toolbar))

        findViewById<View>(R.id.fab).setOnClickListener {
            val clipsFragment = supportFragmentManager.fragments[0] as CategoriesListFragment
            clipsFragment.showDialogAdd()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
    }
}

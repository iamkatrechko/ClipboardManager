package com.iamkatrechko.clipboardmanager.view.activity

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.iamkatrechko.clipboardmanager.R
import com.iamkatrechko.clipboardmanager.databinding.ActivityClipEditBinding
import com.iamkatrechko.clipboardmanager.view.fragment.ClipEditFragment

/**
 * Активность экрана редактирования заметки
 * @author iamkatrechko
 *         Date: 01.11.2016
 */
class ClipEditActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityClipEditBinding>(this, R.layout.activity_clip_edit)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val uri = intent.getLongExtra(KEY_URI, -1)
        val action = intent.getIntExtra(KEY_ACTION, -1)
        val fragmentManager = supportFragmentManager
        if (fragmentManager.fragments.isEmpty()) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, ClipEditFragment.newInstance(uri, action))
                    .commit()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        val clipsFragment = supportFragmentManager.findFragmentById(R.id.container) as ClipEditFragment
        clipsFragment.onBackPressed()
    }

    companion object {

        /** Ключ аргумента. Uri заметки */
        private const val KEY_URI = "KEY_URI"
        /** Ключ аргумента. Действие при открытии (просмотр/редактирование) */
        private const val KEY_ACTION = "KEY_ACTION"

        /**
         * Возвращает интент активности
         * @param clipUri URI редактируемой записи
         */
        fun newIntent(context: Context, clipUri: Long?, action: Int? = null) =
                Intent(context, ClipEditActivity::class.java).apply {
                    putExtra(KEY_URI, clipUri)
                    putExtra(KEY_ACTION, action)
                }
    }
}

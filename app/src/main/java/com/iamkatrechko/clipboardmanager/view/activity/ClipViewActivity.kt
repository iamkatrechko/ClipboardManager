package com.iamkatrechko.clipboardmanager.view.activity

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.iamkatrechko.clipboardmanager.R
import com.iamkatrechko.clipboardmanager.databinding.ActivityClipViewBinding
import com.iamkatrechko.clipboardmanager.view.fragment.ClipViewFragment

/**
 * Активность просмотра записи
 * @author iamkatrechko
 *         Date: 04.05.2018
 */
class ClipViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityClipViewBinding>(this, R.layout.activity_clip_view)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val clipId = intent.getLongExtra(KEY_CLIP_ID, -1)
        val fragmentManager = supportFragmentManager
        if (fragmentManager.fragments.isEmpty()) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, ClipViewFragment.newInstance(clipId))
                    .commit()
        }
    }

    companion object {

        /** Ключ аргумента. Id записи */
        private const val KEY_CLIP_ID = "KEY_CLIP_ID"

        /**
         * Возвращает интент активности
         * @param [clipId] идентификатор просматриваемой записи
         */
        fun newIntent(context: Context, clipId: Long) =
                Intent(context, ClipViewActivity::class.java).apply {
                    putExtra(KEY_CLIP_ID, clipId)
                }
    }
}
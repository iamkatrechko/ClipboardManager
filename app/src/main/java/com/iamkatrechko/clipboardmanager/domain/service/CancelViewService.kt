package com.iamkatrechko.clipboardmanager.domain.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.PixelFormat
import android.graphics.Point
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.core.content.systemService
import androidx.core.os.postDelayed
import com.iamkatrechko.clipboardmanager.R
import com.iamkatrechko.clipboardmanager.data.repository.ClipboardRepository
import com.iamkatrechko.clipboardmanager.databinding.FloatCancelViewBinding
import com.iamkatrechko.clipboardmanager.view.extension.TAG
import com.iamkatrechko.clipboardmanager.view.extension.showToast

/**
 * Сервис для отображения кнопки "отмена сохранения записи"
 * @author iamkatrechko
 *         Date: 09.11.2016
 */
class CancelViewService : Service() {

    /** Менеджер экрана */
    private val windowManager by lazy { systemService<WindowManager>() }
    /** Репозиторий записей */
    private var repository: ClipboardRepository = ClipboardRepository.getInstance()
    /** Биндинг разметки */
    private lateinit var binding: FloatCancelViewBinding

    override fun onCreate() {
        Log.d(TAG, "onCreate")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")
        val deleteClipId = intent.getLongExtra(KEY_CLIP_ID, -1L)
        if (deleteClipId == -1L) {
            stopSelf()
            return super.onStartCommand(intent, flags, startId)
        }
        binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.float_cancel_view, null, false)

        binding.linearCancel.setOnClickListener {
            windowManager.removeView(binding.root)
            repository.deleteClip(this, deleteClipId)
            showToast(getString(R.string.deleted) + deleteClipId)
            stopSelf()
        }

        showCancelView()
        return Service.START_NOT_STICKY
    }

    /** Отображает кнопку отмены сохранения записи */
    private fun showCancelView() {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val myParams = WindowManager.LayoutParams(
                Math.round(340 * displayMetrics.density),
                Math.round(48 * displayMetrics.density),
                WindowManager.LayoutParams.TYPE_TOAST,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT)

        val size = Point()
        windowManager.defaultDisplay.getSize(size)
        myParams.x = 0
        myParams.y = size.y / 3
        windowManager.addView(binding.root, myParams)
        binding.root.animate()
                .alphaBy(1.0f)
                .alpha(0.0f)
                .setDuration(ANIMATE_DURATION_TIME)
                .setStartDelay(ANIMATE_DELAY_TIME)

        Handler().postDelayed(ANIMATE_DELAY_TO_STOP_TIME) {
            try {
                windowManager.removeView(binding.root)
                stopSelf()
            } catch (_: Exception) {
            }
        }
    }

    override fun onBind(intent: Intent) = null

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy")
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        super.onTaskRemoved(rootIntent)
        Log.i(TAG, "onTaskRemoved")
    }

    companion object {

        /** Ключ идентификатора заметки для удаления */
        private const val KEY_CLIP_ID = "KEY_CLIP_ID"

        /** Время проигрывания анимации исчезновения */
        private const val ANIMATE_DURATION_TIME = 1000L
        /** Пауза перед началом анимации */
        private const val ANIMATE_DELAY_TIME = 2000L
        /** Время, через которое закрывается диалог */
        private const val ANIMATE_DELAY_TO_STOP_TIME = ANIMATE_DURATION_TIME + ANIMATE_DELAY_TIME

        /**
         * Возвращает интент сервиса
         * @param clipId  идентификатор заметки в БД
         */
        fun newIntent(context: Context, clipId: String): Intent {
            return Intent(context, CancelViewService::class.java).apply {
                putExtra(KEY_CLIP_ID, java.lang.Long.valueOf(clipId))
            }
        }
    }
}

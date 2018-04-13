package com.iamkatrechko.clipboardmanager.domain.service

import android.app.Service
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.systemService
import com.iamkatrechko.clipboardmanager.R
import com.iamkatrechko.clipboardmanager.data.repository.ClipboardRepository
import com.iamkatrechko.clipboardmanager.domain.util.ClipUtils
import com.iamkatrechko.clipboardmanager.domain.util.SettingsValues
import com.iamkatrechko.clipboardmanager.view.extension.TAG
import com.iamkatrechko.clipboardmanager.domain.util.UtilPreferences
import com.iamkatrechko.clipboardmanager.view.NotificationManager
import com.iamkatrechko.clipboardmanager.view.extension.showToast

/**
 * Сервис для прослушивания буфера обмена
 * @author iamkatrechko
 *         Date: 07.11.2016
 */
class ClipboardService : Service() {

    /** Репозиторий с записями в базе данных */
    private val repository = ClipboardRepository.getInstance()
    /** Менеджер буфера обмена  */
    private var clipboardManager: ClipboardManager? = null
    /** Хранилище настроек программы */
    private val settings = SettingsValues.getInstance()
    /** Слушатель новых записей в буфере обмена */
    private val clipListener = ClipboardManager.OnPrimaryClipChangedListener {
        Log.d(TAG, "Обнаружена новая запись в буфере обмена")
        val (text, label) = ClipUtils.getClip(this)

        if (label == ClipUtils.CLIP_LABEL || label == ClipUtils.CLIP_LABEL_ACCESSIBILITY) {
            Log.d(TAG, "Отмена: копирование из приложения")
            showToast("Отмена: копирование из приложения")
        } else if (text.isEmpty()) {
            Log.d(TAG, "Отмена: текст записи пуст")
            showToast(R.string.empty_record)
        } else if (repository.alreadyExists(this, text)) {
            Log.d(TAG, "Отмена: текущая запись уже существует")
            showToast(R.string.record_already_exists)
            //TODO Сделать настройку "Если уже существует: ничего не делать || изменить дату на новую
        } else {
            addClipAndShowCancel(text)
        }
        // Обновляем уведомление
        ClipboardService.startMyService(this)
    }

    override fun onCreate() {
        Log.d(TAG, "onCreate")
        clipboardManager = systemService<ClipboardManager>()
        clipboardManager?.addPrimaryClipChangedListener(clipListener)
        // TODO Отображать уведомление при запуске
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")
        when (intent?.action) {
            ACTION_SHOW_ONLY_FAVORITE -> showOnlyFavorite()
            ACTION_COPY_TO_CLIPBOARD -> copyToClipboard(intent.getLongExtra("id", -1))
            else -> startForegroundService()
        }
        return Service.START_NOT_STICKY
    }

    override fun onBind(intent: Intent) = null

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
        clipboardManager?.removePrimaryClipChangedListener(clipListener)
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        Log.d(TAG, "onTaskRemoved")
    }

    /** Запускает фоновый сервис */
    private fun startForegroundService() {
        startForeground(NOTIFICATION_ID, NotificationManager().getNotification(this))
        if (!settings.notificationShow) {
            startService(HideNotificationService.newIntent(this, NOTIFICATION_ID))
        }
    }

    /** Включает режим отображения избранных записей */
    private fun showOnlyFavorite() {
        val show = UtilPreferences.isShowOnlyFavoriteInNotification(this)
        UtilPreferences.setShowOnlyFavoriteInNotification(this, !show)
        startMyService(this)
    }

    /** Копирует запись с указанным [id] в буфер обмена */
    private fun copyToClipboard(id: Long) {
        repository.getClip(this, id)?.let {
            ClipUtils.copyToClipboard(this, it.text)
        }
        startMyService(this)
    }

    /** Добавление новой записи с текстом [text] в базу данных */
    private fun addClipAndShowCancel(text: String) {
        val newClipUri = repository.insertClip(this, text)
        if (newClipUri != null) {
            startService(CancelViewService.newIntent(this, newClipUri.lastPathSegment))
        }
    }

    companion object {

        /** Команда отображения избранных заметок */
        const val ACTION_SHOW_ONLY_FAVORITE = "ACTION_SHOW_ONLY_FAVORITE"
        /** Команда копирования заметки в буфер */
        const val ACTION_COPY_TO_CLIPBOARD = "ACTION_COPY_TO_CLIPBOARD"
        /** Идентификатор уведомления сервиса */
        private const val NOTIFICATION_ID = 98431

        /** Запускает сервис */
        fun startMyService(context: Context) {
            context.startService(Intent(context, ClipboardService::class.java))
        }
    }
}

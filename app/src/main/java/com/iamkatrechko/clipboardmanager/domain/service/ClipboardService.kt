package com.iamkatrechko.clipboardmanager.domain.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.util.Log
import com.iamkatrechko.clipboardmanager.App
import com.iamkatrechko.clipboardmanager.data.model.SimpleClip
import com.iamkatrechko.clipboardmanager.data.repository.ClipboardRepository
import com.iamkatrechko.clipboardmanager.domain.repository.SystemsClipsWatcher
import com.iamkatrechko.clipboardmanager.domain.request.InsertClipRequest
import com.iamkatrechko.clipboardmanager.domain.util.ClipUtils
import com.iamkatrechko.clipboardmanager.domain.util.PrefsManager
import com.iamkatrechko.clipboardmanager.domain.util.SettingsValues
import com.iamkatrechko.clipboardmanager.view.NotificationManager
import com.iamkatrechko.clipboardmanager.view.extension.TAG
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

/**
 * Сервис для прослушивания буфера обмена
 * @author iamkatrechko
 *         Date: 07.11.2016
 */
class ClipboardService : Service() {

    /** Репозиторий с записями в базе данных */
    private val repository = ClipboardRepository.getInstance()
    /** Хранилище настроек программы */
    private val settings = SettingsValues.getInstance()
    /** Общий список rx-подписчиков */
    private val disposables = CompositeDisposable()

    override fun onCreate() {
        Log.d(TAG, "onCreate")
        val clipManager = SystemsClipsWatcher(App.clipManager, this.applicationContext)
        clipManager.observe()
                .subscribe(::prepareClip)
                .addTo(disposables)
        // TODO Отображать уведомление при запуске
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")
        when (intent?.action) {
            ACTION_SHOW_ONLY_FAVORITE -> showOnlyFavorite()
            ACTION_COPY_TO_CLIPBOARD -> copyToClipboard(intent.getLongExtra("id", -1))
            else -> startForegroundService()
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent) =
            null

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
        disposables.dispose()
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        Log.d(TAG, "onTaskRemoved")
    }

    /** Обработчик найденных записей буфера */
    private fun prepareClip(clip: SimpleClip) {
        Log.d(TAG, "Обнаружена новая запись в буфере обмена")
        val (text, label) = clip

        if (label == ClipUtils.CLIP_LABEL || label == ClipUtils.CLIP_LABEL_ACCESSIBILITY) {
            Log.d(TAG, "Отмена: копирование из приложения")
        } else if (text.isEmpty()) {
            Log.d(TAG, "Отмена: текст записи пуст")
        } else if (repository.alreadyExists(text)) {
            Log.d(TAG, "Отмена: текущая запись уже существует")
            //TODO Сделать настройку "Если уже существует: ничего не делать || изменить дату на новую
        } else {
            addClipAndShowCancel(text)
        }
        // Обновляем уведомление
        startMyService(this)
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
        val prefs = PrefsManager.getInstance()
        prefs.isShowOnlyFavoriteInNotification = !prefs.isShowOnlyFavoriteInNotification
        startMyService(this)
    }

    /** Копирует запись с указанным [id] в буфер обмена */
    private fun copyToClipboard(id: Long) {
        repository.getClip(id)?.let {
            App.clipManager.toClipboard(it.text)
        }
        startMyService(this)
    }

    /** Добавление новой записи с текстом [content] в базу данных */
    private fun addClipAndShowCancel(content: String) {
        val clipId = repository.insertClip(InsertClipRequest.withContent(content))
        if (clipId != null) {
            startService(CancelViewService.newIntent(this, clipId.toString()))
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

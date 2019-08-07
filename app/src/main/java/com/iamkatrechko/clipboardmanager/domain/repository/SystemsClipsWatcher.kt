package com.iamkatrechko.clipboardmanager.domain.repository

import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import com.iamkatrechko.clipboardmanager.data.model.SimpleClip
import com.iamkatrechko.clipboardmanager.domain.IClipManager
import io.reactivex.Observable

/**
 * Источник системных изменений в буфере обмена
 * @author iamkatrechko
 *         Date: 07.08.2019
 */
class SystemsClipsWatcher(
        private val clipManager: IClipManager,
        ctx: Context
) {

    /** Системный сервис буфера обмена */
    private val clipsService = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    /** Возвращает наблюдателя за буфером обмена */
    fun observe(): Observable<SimpleClip> {
        return Observable.create { emitter ->
            Log.d(TAG, "Подпись на данные")
            val listener = ClipboardManager.OnPrimaryClipChangedListener {
                Log.d(TAG, "Новая заметка")
                emitter.onNext(clipManager.getClip())
            }
            clipsService.addPrimaryClipChangedListener(listener)
            emitter.setCancellable {
                Log.d(TAG, "Отписка от данных")
                clipsService.removePrimaryClipChangedListener(listener)
            }
        }
    }

    companion object {

        /** Тег для логирования */
        private val TAG = SystemsClipsWatcher::class.java.simpleName
    }
}

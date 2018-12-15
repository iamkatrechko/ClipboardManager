package com.iamkatrechko.clipboardmanager.domain.service.experiment

import android.content.Context
import android.support.v4.content.CursorLoader
import com.iamkatrechko.clipboardmanager.data.database.DatabaseDescription.ClipsTable
import com.iamkatrechko.clipboardmanager.data.database.wrapper.ClipCursor
import com.iamkatrechko.clipboardmanager.data.mapper.CursorToClipMapper
import com.iamkatrechko.clipboardmanager.data.model.Clip
import com.iamkatrechko.clipboardmanager.data.repository.common.Provider
import io.reactivex.Observable
import io.reactivex.annotations.Experimental
import io.reactivex.subjects.BehaviorSubject
import java.util.*


/**
 * Репозиторий записей на Subject'ах
 * @author iamkatrechko
 *         Date: 13.04.2018
 */
@Experimental
class CursorClipsRepo private constructor() {

    /** Вещатель изменений списка записей */
    private var clipsSubject = BehaviorSubject.create<List<Clip>>()
    /** Cursor записей, на который происходит подписка. В отдельном поле, чтобы не уничтожился */
    private var cursor: CursorLoader? = null

    /** Возвращает список записей */
    fun getClips(context: Context): Observable<List<Clip>> {
        if (cursor == null) {
            cursor = CursorLoader(context,
                    ClipsTable.CONTENT_URI,
                    null,
                    null,
                    null,
                    ClipsTable._ID + " DESC")
            cursor?.registerListener(362623) { loader, clipsCursor ->
                clipsSubject.onNext(CursorToClipMapper.toClips(ClipCursor(clipsCursor)))
            }
            cursor?.registerOnLoadCanceledListener {
                if (clipsSubject.hasObservers()) {
                    clipsSubject.onError(Exception("Остановка"))
                }
            }
            cursor?.startLoading()
        }
        return clipsSubject.share()
    }

    /** Возвращает запись по ее [clipId] */
    fun getClip(context: Context, clipId: Long): Observable<Clip> {
        val clipUri = ClipsTable.buildClipUri(clipId)
        return Observable.create<Clip> { emitter ->
            // TODO При успехе перевести в функцию расширения CursorLoader.toDisposable()
            // TODO Сейвить в мапу или в лист, если лоадер будет сам останавливаться
            val cursorLoader = CursorLoader(context, clipUri, null, null, null, null)
            cursorLoader.registerListener(Random().nextInt()) { _, clipsCursor ->
                if (clipsCursor?.moveToFirst() == true) {
                    emitter.onNext(CursorToClipMapper.toClip(ClipCursor(clipsCursor)))
                } else {
                    emitter.onError(Exception("Запись не найдена"))
                }
            }
            cursorLoader.registerOnLoadCanceledListener {
                emitter.onError(Exception("Остановка"))
            }
            emitter.setCancellable { cursorLoader.stopLoading() }
            cursorLoader.startLoading()
        }
    }

    companion object : Provider<CursorClipsRepo>() {

        override fun createInstance() = CursorClipsRepo()
    }
}
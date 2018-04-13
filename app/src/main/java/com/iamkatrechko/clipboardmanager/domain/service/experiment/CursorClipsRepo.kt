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
                clipsSubject.onNext(CursorToClipMapper().toClips(ClipCursor(clipsCursor)))
            }
            cursor?.registerOnLoadCanceledListener {
                clipsSubject.onError(Exception("Остановка"))
            }
            cursor?.startLoading()
        }
        return clipsSubject.share()
    }

    companion object : Provider<CursorClipsRepo>() {

        override fun createInstance() = CursorClipsRepo()
    }
}
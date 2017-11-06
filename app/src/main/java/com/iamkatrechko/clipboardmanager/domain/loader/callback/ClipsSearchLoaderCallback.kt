package com.iamkatrechko.clipboardmanager.domain.loader.callback

import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import com.iamkatrechko.clipboardmanager.data.database.DatabaseDescription
import com.iamkatrechko.clipboardmanager.data.database.wrapper.ClipCursor
import com.iamkatrechko.clipboardmanager.data.mapper.CursorToClipMapper
import com.iamkatrechko.clipboardmanager.data.model.Clip

/**
 * Загрузчик списка записей по поиску
 * @author iamkatrechko
 *         Date: 06.11.2017
 */
class ClipsSearchLoaderCallback(
        /** Контекст */
        private val context: Context,
        /** Слушатель готовности данных к использованию */
        private val listener: OnDataPreparedListener
) : LoaderManager.LoaderCallbacks<Cursor> {

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor>? {
        when (id) {
            SEARCH_CLIPS_LOADER -> {
                //TODO Добавить реализацию пустого результата (View)
                val query = args?.getString(KEY_LOADER_QUERY_TEXT)
                return CursorLoader(context,
                        DatabaseDescription.Clip.CONTENT_URI, null,
                        DatabaseDescription.Clip.COLUMN_TITLE + " LIKE '%" + query + "%' OR " +
                                DatabaseDescription.Clip.COLUMN_CONTENT + " LIKE '%" + query + "%'", null,
                        DatabaseDescription.Clip._ID + " DESC")
            }
            else -> return null
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>?) {
    }

    override fun onLoadFinished(loader: Loader<Cursor>?, data: Cursor?) {
        listener.onPrepared(CursorToClipMapper().toClips(ClipCursor(data)))
    }

    companion object {

        /** Идентификатор загрузчика найденых заметок  */
        const val SEARCH_CLIPS_LOADER = 1

        /** Ключ параметра загрузчика. Идентификатор категории */
        const val KEY_LOADER_QUERY_TEXT: String = "KEY_LOADER_QUERY_TEXT"
    }

    /** Слушатель готовности данных к использованию */
    interface OnDataPreparedListener {

        /**
         * Данные готовы к использованию
         * @param [clipsList] список записей
         */
        fun onPrepared(clipsList: List<Clip>)
    }
}
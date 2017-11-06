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
import com.iamkatrechko.clipboardmanager.domain.param.ClipParam

/**
 * Загрузчик списка записей по поиску
 * @author iamkatrechko
 *         Date: 06.11.2017
 */
class ClipsSearchLoaderCallback(
        /** Контекст */
        private val context: Context,
        /** Слушатель готовности данных к использованию */
        private val preparedAction: (list: List<Clip>) -> Unit
) : LoaderManager.LoaderCallbacks<Cursor> {

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor>? {
        val param = args?.getParcelable<ClipParam>(KEY_LOADER_PARAMS) ?:
                throw IllegalArgumentException("Не заданы параметры запроса")
        when (id) {
            SEARCH_CLIPS_LOADER -> {
                val queryText = param.queryText ?: ""
                val queryOrder = param.order.query
                return CursorLoader(context,
                        DatabaseDescription.ClipsTable.CONTENT_URI,
                        null,
                        DatabaseDescription.ClipsTable.COLUMN_TITLE + " LIKE '%" + queryText + "%' OR " +
                                DatabaseDescription.ClipsTable.COLUMN_CONTENT + " LIKE '%" + queryText + "%'",
                        null,
                        queryOrder)
            }
            else -> return null
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>?) {
    }

    override fun onLoadFinished(loader: Loader<Cursor>?, data: Cursor?) {
        preparedAction(CursorToClipMapper().toClips(ClipCursor(data)))
    }

    companion object {

        /** Идентификатор загрузчика найденых заметок  */
        const val SEARCH_CLIPS_LOADER = 1

        /** Ключ параметра загрузчика. Параметры запроса */
        const val KEY_LOADER_PARAMS = "KEY_LOADER_PARAMS"
    }
}
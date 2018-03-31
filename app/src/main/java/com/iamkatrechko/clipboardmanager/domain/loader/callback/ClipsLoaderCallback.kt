package com.iamkatrechko.clipboardmanager.domain.loader.callback

import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.util.Log
import com.iamkatrechko.clipboardmanager.data.database.DatabaseDescription.ClipsTable
import com.iamkatrechko.clipboardmanager.data.database.wrapper.ClipCursor
import com.iamkatrechko.clipboardmanager.data.mapper.CursorToClipMapper
import com.iamkatrechko.clipboardmanager.data.model.Clip
import com.iamkatrechko.clipboardmanager.domain.param.ClipParam
import com.iamkatrechko.clipboardmanager.domain.param.extension.createQuery

/**
 * Загрузчик списка записей
 * @author iamkatrechko
 *         Date: 04.11.2017
 */
class ClipsLoaderCallback(
        /** Контекст */
        private val context: Context,
        /** Слушатель готовности данных к использованию */
        private val preparedAction: (list: List<Clip>) -> Unit
) : LoaderManager.LoaderCallbacks<Cursor> {

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        Log.d(TAG, "onCreateLoader")
        val param = args?.getParcelable<ClipParam>(KEY_LOADER_PARAMS) ?:
                throw IllegalArgumentException("Не заданы параметры запроса")

        when (id) {
            MAIN_CLIPS_LOADER -> {
                return CursorLoader(context,
                        ClipsTable.CONTENT_URI,
                        null,
                        param.createQuery(),
                        null,
                        param.order.query)
            }
            else -> error("Неизвестный тип загрузчика записей")
        }
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        Log.d(TAG, "onLoadFinished")
        preparedAction(CursorToClipMapper().toClips(ClipCursor(data)))
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        Log.d(TAG, "onLoaderReset")
    }

    companion object {

        /** Тег для логирования */
        private val TAG = ClipsLoaderCallback::class.java.simpleName

        /** Идентификатор загрузчика заметок по категории  */
        const val MAIN_CLIPS_LOADER = 1

        /** Ключ параметра загрузчика. Параметры запроса */
        const val KEY_LOADER_PARAMS = "KEY_LOADER_PARAMS"
    }
}
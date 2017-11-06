package com.iamkatrechko.clipboardmanager.domain.loader.callback

import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.util.Log
import com.iamkatrechko.clipboardmanager.data.database.DatabaseDescription
import com.iamkatrechko.clipboardmanager.data.database.wrapper.ClipCursor
import com.iamkatrechko.clipboardmanager.data.mapper.CursorToClipMapper
import com.iamkatrechko.clipboardmanager.data.model.Clip
import com.iamkatrechko.clipboardmanager.domain.param.values.OrderType
import com.iamkatrechko.clipboardmanager.domain.param.values.OrderType.*

/**
 * Загрузчик списка записей
 * @author iamkatrechko
 *         Date: 04.11.2017
 */
class ClipsLoaderCallback(
        /** Контекст */
        private val context: Context,
        /** Слушатель готовности данных к использованию */
        private val listener: OnDataPreparedListener
) : LoaderManager.LoaderCallbacks<Cursor> {

    companion object {

        /** Тег для логирования */
        private val TAG = ClipsLoaderCallback::class.java.simpleName

        /** Идентификатор загрузчика заметок по категории  */
        const val CLIPS_BY_CATEGORY_LOADER = 1

        /** Ключ параметра загрузчика. Идентификатор категории */
        const val KEY_LOADER_CATEGORY_ID: String = "KEY_LOADER_CATEGORY_ID"
        /** Ключ параметра загрузчика. Отображение только избранных */
        const val KEY_LOADER_ONLY_FAVORITE: String = "KEY_LOADER_ONLY_FAVORITE"
        /** Ключ параметра загрузчика. Порядок сортировки */
        const val KEY_LOADER_ORDER_TYPE: String = "KEY_LOADER_ORDER_TYPE"
    }

    override fun onCreateLoader(id: Int, args: Bundle): Loader<Cursor>? {
        Log.d(TAG, "onCreateLoader")
        val categoryId = args.getLong(KEY_LOADER_CATEGORY_ID)
        val isOnlyFavoriteShow = args.getBoolean(KEY_LOADER_ONLY_FAVORITE)
        val orderType = values()[args.getInt(KEY_LOADER_ORDER_TYPE)]

        when (id) {
            CLIPS_BY_CATEGORY_LOADER -> {
                val queryCatId = if (categoryId == -1L) null else DatabaseDescription.ClipsTable.COLUMN_CATEGORY_ID + " = $categoryId "
                val queryOnlyFav = if (!isOnlyFavoriteShow) null else DatabaseDescription.ClipsTable.COLUMN_IS_FAVORITE + " = 1 "

                val whereQuery = listOf(queryCatId, queryOnlyFav).filterNotNull().joinToString(" and ")
                return CursorLoader(context,
                        DatabaseDescription.ClipsTable.CONTENT_URI, null,
                        whereQuery,
                        null,
                        getOrderQuery(orderType))
            }
            else -> throw IllegalArgumentException("Неизвестный тип загрузчика записей")
        }
    }

    override fun onLoadFinished(loader: Loader<Cursor>?, data: Cursor?) {
        Log.d(TAG, "onLoadFinished")
        listener.onPrepared(CursorToClipMapper().toClips(ClipCursor(data)))
    }

    override fun onLoaderReset(loader: Loader<Cursor>?) {
        Log.d(TAG, "onLoaderReset")
    }

    /** Возвращает параметр запроса сортировки по типу сортировки */
    private fun getOrderQuery(orderType: OrderType) = when (orderType) {
        BY_DATE_ASC -> DatabaseDescription.ClipsTable.COLUMN_DATE + " DESC"
        BY_DATE_DESC -> DatabaseDescription.ClipsTable.COLUMN_DATE
        BY_TITLE_ASC -> DatabaseDescription.ClipsTable.COLUMN_TITLE
        BY_TITLE_DESC -> DatabaseDescription.ClipsTable.COLUMN_TITLE + " DESC"
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
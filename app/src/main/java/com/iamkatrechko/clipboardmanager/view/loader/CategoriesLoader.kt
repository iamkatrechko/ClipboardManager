package com.iamkatrechko.clipboardmanager.view.loader

import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.util.Log
import com.iamkatrechko.clipboardmanager.data.database.DatabaseDescription
import com.iamkatrechko.clipboardmanager.data.database.wrapper.CategoryCursor
import com.iamkatrechko.clipboardmanager.data.mapper.CursorToCategoryMapper
import com.iamkatrechko.clipboardmanager.data.model.Category
import com.iamkatrechko.clipboardmanager.view.activity.MainActivity

/**
 * Загрузчик списка категорий
 * @author iamkatrechko
 *         Date: 04.11.2017
 */
class CategoriesLoader(
        /** Контекст */
        private val context: Context,
        /** Слушатель готовности данных к использованию */
        private val listener: OnDataPreparedListener
) : LoaderManager.LoaderCallbacks<Cursor> {

    companion object {

        /** Тег для логирования */
        private val TAG = CategoriesLoader::class.java.simpleName
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor>? {
        Log.d(TAG, "onCreateLoader")
        when (id) {
            MainActivity.CATEGORIES_LOADER -> {
                return CursorLoader(context,
                        DatabaseDescription.Category.CONTENT_URI,
                        null,
                        null,
                        null,
                        null)
            }
            else -> return null
        }
    }

    override fun onLoadFinished(loader: Loader<Cursor>?, data: Cursor) {
        listener.onPrepared(CursorToCategoryMapper().toCategories(CategoryCursor(data)))
    }

    override fun onLoaderReset(loader: Loader<Cursor>?) {
    }

    /** Слушатель готовности данных к использованию */
    interface OnDataPreparedListener {

        /**
         * Данные готовы к использованию
         * @param [data] список категорий
         */
        fun onPrepared(data: List<Category>)
    }
}
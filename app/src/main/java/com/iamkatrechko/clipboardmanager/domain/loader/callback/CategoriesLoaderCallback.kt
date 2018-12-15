package com.iamkatrechko.clipboardmanager.domain.loader.callback

import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.util.Log
import com.iamkatrechko.clipboardmanager.view.extension.TAG
import com.iamkatrechko.clipboardmanager.data.database.DatabaseDescription
import com.iamkatrechko.clipboardmanager.data.database.wrapper.CategoryCursor
import com.iamkatrechko.clipboardmanager.data.mapper.CursorToCategoryMapper
import com.iamkatrechko.clipboardmanager.data.model.Category

/**
 * Загрузчик списка категорий
 * @author iamkatrechko
 *         Date: 04.11.2017
 */
class CategoriesLoaderCallback(
        /** Контекст */
        private val context: Context,
        /** Слушатель готовности данных к использованию */
        private val onDataPrepared: (List<Category>) -> Unit
) : LoaderManager.LoaderCallbacks<Cursor> {

    companion object {

        /** Идентификатор загрузчика категорий  */
        const val CATEGORIES_LOADER = 0
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        Log.d(TAG, "onCreateLoader")
        when (id) {
            CATEGORIES_LOADER -> {
                return CursorLoader(context,
                        DatabaseDescription.CategoryTable.CONTENT_URI,
                        null,
                        null,
                        null,
                        null)
            }
            else -> error("Неизвестный загрузчик")
        }
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor) {
        onDataPrepared(CursorToCategoryMapper.toCategories(CategoryCursor(data)))
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
    }
}
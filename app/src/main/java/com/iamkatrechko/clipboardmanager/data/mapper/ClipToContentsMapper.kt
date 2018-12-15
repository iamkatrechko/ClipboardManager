package com.iamkatrechko.clipboardmanager.data.mapper

import android.content.ContentValues
import com.iamkatrechko.clipboardmanager.data.database.DatabaseDescription.ClipsTable
import com.iamkatrechko.clipboardmanager.data.model.Clip

/**
 * Маппер заметки в контент для записи в БД
 * @author iamkatrechko
 *         Date: 15.12.2018
 */
object ClipToContentsMapper {

    fun map(clip: Clip): ContentValues =
            ContentValues().apply {
                put(ClipsTable.COLUMN_TITLE, clip.title)
                put(ClipsTable.COLUMN_CONTENT, clip.text)
                put(ClipsTable.COLUMN_CATEGORY_ID, clip.categoryId)
                put(ClipsTable.COLUMN_DATE, clip.dateTime)
                put(ClipsTable.COLUMN_IS_FAVORITE, clip.isFavorite)
                put(ClipsTable.COLUMN_IS_DELETED, clip.isDeleted)
                put(ClipsTable.COLUMN_POSITION, clip.position)
            }
}
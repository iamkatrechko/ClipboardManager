package com.iamkatrechko.clipboardmanager.data.repository

import android.content.ContentValues
import android.content.Context
import com.iamkatrechko.clipboardmanager.data.database.DatabaseDescription.ClipsTable
import com.iamkatrechko.clipboardmanager.data.database.wrapper.ClipCursor
import com.iamkatrechko.clipboardmanager.data.mapper.ClipToContentsMapper
import com.iamkatrechko.clipboardmanager.data.mapper.CursorToClipMapper
import com.iamkatrechko.clipboardmanager.data.model.Clip
import com.iamkatrechko.clipboardmanager.data.repository.common.Provider
import com.iamkatrechko.clipboardmanager.domain.repository.IClipRepository
import com.iamkatrechko.clipboardmanager.domain.request.InsertClipRequest
import com.iamkatrechko.clipboardmanager.domain.util.isNullOrEmpty

/**
 * Репозиторий записей
 * @author iamkatrechko
 *         Date: 03.11.17
 *
 * @param ctx контекст приложения
 */
class ClipboardRepository private constructor(
        private val ctx: Context
) : IClipRepository {

    override fun insertClip(request: InsertClipRequest): Int? {
        val contentValues = ContentValues().apply {
            put(ClipsTable.COLUMN_TITLE, request.title)
            put(ClipsTable.COLUMN_CONTENT, request.content)
            put(ClipsTable.COLUMN_CATEGORY_ID, request.categoryId.toLong())
            put(ClipsTable.COLUMN_DATE, request.date)
            put(ClipsTable.COLUMN_IS_FAVORITE, request.isFavorite)
            put(ClipsTable.COLUMN_IS_DELETED, 0)
            put(ClipsTable.COLUMN_POSITION, request.position)
        }

        return ctx.contentResolver.insert(ClipsTable.CONTENT_URI, contentValues).lastPathSegment.toInt()
    }

    override fun alreadyExists(content: String): Boolean {
        val cursor = ctx.contentResolver.query(ClipsTable.CONTENT_URI,
                null,
                ClipsTable.COLUMN_CONTENT + " = ?",
                arrayOf(content),
                null)
        cursor.use {
            return it != null && it.count > 0
        }
    }

    override fun getClip(id: Long): Clip? {
        val clipUri = ClipsTable.buildClipUri(id)
        val cursor = ClipCursor(ctx.contentResolver.query(clipUri, null, null, null, null))
        if (!cursor.isNullOrEmpty()) {
            cursor.moveToFirst()
            return CursorToClipMapper.toClip(cursor)
        }
        return null
    }

    override fun getClips(ids: Collection<Long>): List<Clip> =
            ids.mapNotNull(::getClip)

    override fun setFavorite(clipId: Long, isFavorite: Boolean) {
        val clip = getClip(clipId)?.copy(isFavorite = isFavorite)
        clip ?: return
        updateClip(clipId, clip)
    }

    override fun changeCategory(clipId: Long, categoryId: Long) {
        val clip = getClip(clipId)?.copy(categoryId = categoryId)
        clip ?: return
        updateClip(clipId, clip)
    }

    override fun deleteClip(id: Long) {
        ctx.contentResolver.delete(ClipsTable.buildClipUri(id), null, null)
    }

    override fun deleteClips(ids: Collection<Long>) {
        ids.forEach(::deleteClip)
    }

    override fun updateClip(clipId: Long, clip: Clip): Boolean {
        val clipUri = ClipsTable.buildClipUri(clipId)
        val values = ClipToContentsMapper.map(clip)
        return ctx.contentResolver.update(clipUri, values, null, null) > 0
    }

    companion object : Provider<ClipboardRepository>() {

        /** Приватный экземпляр класса */
        private var INSTANCE: ClipboardRepository? = null

        /** Инициализирует компонент */
        fun init(context: Context) {
            INSTANCE = ClipboardRepository(context)
        }

        override fun createInstance() = INSTANCE ?: error("Компонент не инициализирован")
    }
}
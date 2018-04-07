package com.iamkatrechko.clipboardmanager.domain.use_case

import android.content.ContentValues
import android.content.Context
import com.iamkatrechko.clipboardmanager.data.database.DatabaseDescription
import com.iamkatrechko.clipboardmanager.domain.use_case.common.CompletableUseCase
import io.reactivex.Completable

/**
 * Сценарий переноса списка записей из одной категории в другую
 * @author iamkatrechko
 *         Date: 07.04.2018
 */
class MoveClipsUseCase : CompletableUseCase<MoveClipsUseCase.Params>() {

    override fun buildUseCaseObservable(params: Params?): Completable {
        params ?: error("Не заданы параметры сценария")
        return Completable.fromAction {
            val uriMove = DatabaseDescription.ClipsTable.CONTENT_URI
            val contentValues = ContentValues()
            contentValues.put(DatabaseDescription.ClipsTable.COLUMN_CATEGORY_ID, params.toCategoryId)

            // Перемещение записей из удаляемой категории в новую
            params.context.contentResolver.update(uriMove,
                    contentValues,
                    DatabaseDescription.ClipsTable.COLUMN_CATEGORY_ID + "=" + params.fromCategoryId, null)

            // Удаление категории (теперь уже пустой)
            val uriDelete = DatabaseDescription.CategoryTable.buildClipUri(params.fromCategoryId)
            params.context.contentResolver.delete(uriDelete, null, null)
        }
    }

    /** Параметры сценария */
    data class Params(
            /** Контекст */
            val context: Context,
            /** Идентификатор исходной категории */
            val fromCategoryId: Long,
            /** Иденификатор конечной категории */
            val toCategoryId: Long
    )
}
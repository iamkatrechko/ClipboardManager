package com.iamkatrechko.clipboardmanager.domain.use_case

import android.content.ContentValues
import android.content.Context
import com.iamkatrechko.clipboardmanager.data.database.DatabaseDescription.ClipsTable
import com.iamkatrechko.clipboardmanager.data.repository.CategoryRepository
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
            val uriClips = ClipsTable.CONTENT_URI
            val contentValues = ContentValues().apply {
                put(ClipsTable.COLUMN_CATEGORY_ID, params.toCategoryId)
            }

            // Перемещение записей из удаляемой категории в новую
            params.context.contentResolver.update(uriClips, contentValues,
                    ClipsTable.COLUMN_CATEGORY_ID + "=" + params.fromCategoryId, null)

            // Удаление категории (теперь уже пустой)
            CategoryRepository.getInstance().removeCategory(params.fromCategoryId.toInt())
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
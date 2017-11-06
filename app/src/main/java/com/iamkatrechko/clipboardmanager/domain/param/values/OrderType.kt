package com.iamkatrechko.clipboardmanager.domain.param.values

import android.support.annotation.StringRes
import com.iamkatrechko.clipboardmanager.R
import com.iamkatrechko.clipboardmanager.data.database.DatabaseDescription
import com.iamkatrechko.clipboardmanager.data.database.DatabaseDescription.ClipsTable

/**
 * Перечисление с типами сортировки
 * @author iamkatrechko
 *         Date: 04.11.2017
 */
enum class OrderType(
        /** Идентификатор заголовка */
        @StringRes val nameResId: Int,
        /** Текст запроса */
        val query: String
) {

    /** По дате (сначала новые) */
    BY_DATE_ASC(R.string.order_type_date_asc, ClipsTable.COLUMN_DATE + " DESC"),
    /** По дате (сначала старые) */
    BY_DATE_DESC(R.string.order_type_date_desc, DatabaseDescription.ClipsTable.COLUMN_DATE),
    /** По заголовку (А-Я) */
    BY_TITLE_ASC(R.string.order_type_title_asc, DatabaseDescription.ClipsTable.COLUMN_TITLE),
    /** По заголовку (Я-А) */
    BY_TITLE_DESC(R.string.order_type_title_desc, DatabaseDescription.ClipsTable.COLUMN_TITLE + " DESC")
}
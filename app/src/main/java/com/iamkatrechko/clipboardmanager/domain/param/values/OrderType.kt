package com.iamkatrechko.clipboardmanager.domain.param.values

import android.support.annotation.StringRes
import com.iamkatrechko.clipboardmanager.R

/**
 * Перечисление с типами сортировки
 * @author iamkatrechko
 *         Date: 04.11.2017
 */
enum class OrderType(
        /** Идентификатор заголовка */
        @StringRes val nameResId: Int
) {
    BY_DATE_ASC(R.string.order_type_date_asc),
    BY_DATE_DESC(R.string.order_type_date_desc)
}
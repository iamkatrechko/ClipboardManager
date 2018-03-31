package com.iamkatrechko.clipboardmanager.domain.param.extension

import com.iamkatrechko.clipboardmanager.data.database.DatabaseDescription.ClipsTable
import com.iamkatrechko.clipboardmanager.domain.param.ClipParam

/**
 * Функции расширения для [ClipParam]
 * @author iamkatrechko
 *         Date: 07.11.17
 **************************************************************************************************************************************************************/

fun ClipParam.createQuery(): String {
    return listOfNotNull(
            if (categoryId == null) null else "${ClipsTable.COLUMN_CATEGORY_ID} = $categoryId",
            if (!onlyFav) null else "${ClipsTable.COLUMN_IS_FAVORITE} = 1",
            if (queryText.isBlank()) null else "(${ClipsTable.COLUMN_TITLE} LIKE '%$queryText%' OR ${ClipsTable.COLUMN_CONTENT} LIKE '%$queryText%')")
            .joinToString(" AND ")
}

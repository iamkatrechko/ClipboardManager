package com.iamkatrechko.clipboardmanager.data.model

/**
 * Сущность категории в базе данных
 * @author iamkatrechko
 *         Date: 04.11.2017
 */
class Category(
        /** Идентификатор */
        val id: Long,
        /** Заголовок */
        val title: String
) {

    companion object {

        /** Идентификатор категории по-умолчанию (неудаляемая) */
        const val DEFAULT_CATEGORY_ID = 1
    }
}

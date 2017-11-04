package com.iamkatrechko.clipboardmanager.view.adapter.navigation

import com.iamkatrechko.clipboardmanager.R

/**
 * Список элементов бокового меню
 * @author iamkatrechko
 *         Date: 04.11.2017
 */
enum class NavGroups(
        /** Идентификатор ресурса с заголовком */
        val nameResId: Int,
        /** Идентификатор ресурса с иконкой */
        val iconResId: Int
) {

    /** Все записи */
    ALL_CLIPS(R.string.group_all_clips, R.drawable.ic_view_list_black),
    /** Категории */
    CATEGORIES(R.string.group_categories, R.drawable.ic_label),
    /** Настройки */
    SETTINGS(R.string.group_settings, R.drawable.ic_settings),
    /** О программе */
    INFO(R.string.group_info, R.drawable.ic_info_outline_black);

}
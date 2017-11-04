package com.iamkatrechko.clipboardmanager.util.extensions

import android.view.View

/**
 * Функции расширения для класса View
 * @author iamkatrechko
 *         Date: 02.11.2017
 **************************************************************************************************************************************************************/

/**
 * Скрытие виджета
 * @param [isGone] флаг скрытия
 */
fun View.setGone(isGone: Boolean) {
    visibility = if (isGone) View.GONE else View.VISIBLE
}

/**
 * Скрытие виджета
 * @param [isInvisible] флаг скрытия
 */
fun View.setInvisible(isInvisible: Boolean) {
    visibility = if (isInvisible) View.INVISIBLE else View.VISIBLE
}
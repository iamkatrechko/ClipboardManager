package com.iamkatrechko.clipboardmanager.view.extension

import android.content.Context
import android.support.annotation.StringRes
import android.widget.Toast

/**
 * Функции расширения для класса Context
 * @author iamkatrechko
 *         Date: 19.10.2017
 **************************************************************************************************************************************************************/

/**
 * Отображает всплывающее сообщение
 * @param [text]     текст сообщения
 * @param [duration] задержка ([Toast.LENGTH_SHORT] или [Toast.LENGTH_LONG])
 */
fun Context.showToast(text: String, duration: Int = Toast.LENGTH_SHORT) =
        Toast.makeText(this, text, duration).show()

/**
 * Отображает всплывающее сообщение
 * @param [textId]   идентификатор текста сообщения
 * @param [duration] задержка ([Toast.LENGTH_SHORT] или [Toast.LENGTH_LONG])
 */
fun Context.showToast(@StringRes textId: Int, duration: Int = Toast.LENGTH_SHORT) =
        showToast(getString(textId), duration)

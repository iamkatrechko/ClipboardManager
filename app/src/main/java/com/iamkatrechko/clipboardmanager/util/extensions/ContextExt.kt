package com.iamkatrechko.clipboardmanager.util.extensions

import android.content.Context
import android.widget.Toast

/**
 * Функции расширения для класса Context
 * @author ivanov_m
 *         Date: 19.10.2017
 **************************************************************************************************************************************************************/

/**
 * Отображает всплывающее сообщение
 * @param text     текст сообщения
 * @param duration задержка ([Toast.LENGTH_SHORT] или [Toast.LENGTH_LONG])
 */
fun Context.showToast(text: String, duration: Int = Toast.LENGTH_SHORT) =
        Toast.makeText(this, text, duration).show()
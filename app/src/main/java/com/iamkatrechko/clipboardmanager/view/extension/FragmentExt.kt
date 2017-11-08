package com.iamkatrechko.clipboardmanager.view.extension

import android.support.v4.app.Fragment
import android.widget.Toast

/**
 * Функции расширения фрагмента
 * @author iamkatrechko
 *         Date: 08.11.17
 **************************************************************************************************************************************************************/

/**
 * Отображает всплывающее сообщение
 * @param text     текст сообщения
 * @param duration задержка ([Toast.LENGTH_SHORT] или [Toast.LENGTH_LONG])
 */
fun Fragment.showToast(text: String, duration: Int = Toast.LENGTH_SHORT) =
        context.showToast(text, duration)

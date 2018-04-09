package com.iamkatrechko.clipboardmanager.view.extension

import android.app.Activity
import android.app.Service
import android.content.Intent
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.widget.Toast

/**
 * Функции расширения фрагмента
 * @author iamkatrechko
 *         Date: 10.11.17
 **************************************************************************************************************************************************************/

/**
 * Отображает всплывающее сообщение
 * @param [text]     текст сообщения
 * @param [duration] задержка ([Toast.LENGTH_SHORT] или [Toast.LENGTH_LONG])
 */
fun Fragment.showToast(text: String, duration: Int = Toast.LENGTH_SHORT) =
        context?.showToast(text, duration)


/**
 * Отображает всплывающее сообщение
 * @param [textId]   идентификатор текста сообщения
 * @param [duration] задержка ([Toast.LENGTH_SHORT] или [Toast.LENGTH_LONG])
 */
fun Fragment.showToast(@StringRes textId: Int, duration: Int = Toast.LENGTH_SHORT) =
        context?.showToast(textId, duration)

/**
 * Запускает активность
 * @param [preparer] функция обработки интента перед запуском
 * @property [T] класс запускаемой активности
 */
inline fun <reified T : Activity> Fragment.startActivity(preparer: Intent.() -> Unit = {}) {
    context?.startActivity<T>(preparer)
}

/**
 * Запускает сервис
 * @param [preparer] функция обработки интента перед запуском
 * @property [T] класс запускаемого сервиса
 */
inline fun <reified T : Service> Fragment.startService(preparer: Intent.() -> Unit = {}) {
    context?.startService<T>(preparer)
}
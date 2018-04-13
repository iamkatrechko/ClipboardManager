package com.iamkatrechko.clipboardmanager.view.extension

import android.app.Activity
import android.content.Intent
import android.support.v4.app.DialogFragment

/**
 * Функции расширения для DialogFragment
 * @author iamkatrechko
 *         Date: 13.04.2018
 **************************************************************************************************************************************************************/

/**
 * Возвращает результат в целевой фрагмент
 * @param [resultCode] результат ответа (пр. [Activity.RESULT_OK])
 * @param [preparer]   обработчик интента перед отдачей во фрагмент
 */
fun DialogFragment.onActivityResult(resultCode: Int, preparer: Intent.() -> Unit = {}) {
    targetFragment?.onActivityResult(targetRequestCode, resultCode, Intent().apply(preparer))
}

/**
 * Возвращает успешный результат в целевой фрагмент
 * @param [preparer] обработчик интента перед отдачей во фрагмент
 */
fun DialogFragment.onActivityResultOk(preparer: Intent.() -> Unit = {}) {
    onActivityResult(Activity.RESULT_OK, preparer)
}

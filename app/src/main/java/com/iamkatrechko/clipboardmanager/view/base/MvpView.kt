package com.iamkatrechko.clipboardmanager.view.base

import android.arch.lifecycle.LifecycleOwner
import android.support.annotation.StringRes

/**
 * Интерфейс базовой view
 * @author iamkatrechko
 *         Date: 16.12.2018
 */
interface MvpView : LifecycleOwner {

    /** Отображает пользователю сообщение с текстом [message] */
    fun showMessage(message: String)

    /** Отображает пользователю сообщение по строковому идентификатору [resId] */
    fun showMessage(@StringRes resId: Int)

    /** Отображает пользователю ошибку с текстом [message] */
    fun showError(message: String)

    /** Отображает пользователю ошибку по строковому идентификатору [resId] */
    fun showError(@StringRes resId: Int)
}
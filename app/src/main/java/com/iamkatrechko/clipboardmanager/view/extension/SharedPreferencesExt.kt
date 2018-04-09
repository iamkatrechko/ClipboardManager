package com.iamkatrechko.clipboardmanager.view.extension

import android.content.SharedPreferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Функции расширения для SharedPreferences
 * @author iamkatrechko
 *         Date: 09.04.2018
 **************************************************************************************************************************************************************/

/**
 * Делегат строкового поля для хранилища.
 * Перенапрявляет get и set методы на прямую работу с хранилищем
 * @param [default] значение по умолчанию
 * @param [key]     ключ параметра для хранения
 */
fun SharedPreferences.delegateString(default: String = "", key: String? = null) =
        delegate(default, key, SharedPreferences::getString, SharedPreferences.Editor::putString)

/**
 * Делегат числового поля для хранилища.
 * Перенапрявляет get и set методы на прямую работу с хранилищем
 * @param [default] значение по умолчанию
 * @param [key]     ключ параметра для хранения
 */
fun SharedPreferences.delegateInt(default: Int = 0, key: String? = null) =
        delegate(default, key, SharedPreferences::getInt, SharedPreferences.Editor::putInt)

/**
 * Делегат 64-х битного числового поля для хранилища.
 * Перенапрявляет get и set методы на прямую работу с хранилищем
 * @param [default] значение по умолчанию
 * @param [key]     ключ параметра для хранения
 */
fun SharedPreferences.delegateLong(default: Long = 0L, key: String? = null) =
        delegate(default, key, SharedPreferences::getLong, SharedPreferences.Editor::putLong)

/**
 * Делегат логического поля для хранилища.
 * Перенапрявляет get и set методы на прямую работу с хранилищем
 * @param [default] значение по умолчанию
 * @param [key]     ключ параметра для хранения
 */
fun SharedPreferences.delegateBoolean(default: Boolean = false, key: String? = null) =
        delegate(default, key, SharedPreferences::getBoolean, SharedPreferences.Editor::putBoolean)

/**
 * Абстрактный делегат для работы с хранилищем напрямую через property
 * Перенапрявляет get и set методы property на прямую работу с хранилищем
 * @param [defaultValue] значение по умолчанию
 * @param [key]          ключ параметра для хранения
 * @param [getter]       функция получения значения
 * @param [getter]       функция сохранения значения
 */
private inline fun <T> SharedPreferences.delegate(
        defaultValue: T,
        key: String?,
        crossinline getter: SharedPreferences.(String, T) -> T,
        crossinline setter: SharedPreferences.Editor.(String, T) -> SharedPreferences.Editor
): ReadWriteProperty<Any, T> {
    return object : ReadWriteProperty<Any, T> {

        override fun getValue(thisRef: Any, property: KProperty<*>) =
                getter(key ?: property.name, defaultValue)

        override fun setValue(thisRef: Any, property: KProperty<*>, value: T) =
                edit().setter(key ?: property.name, value).apply()
    }
}
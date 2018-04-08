package com.iamkatrechko.clipboardmanager.data.repository.common

/**
 * Абстрактный провайдер синглтона
 * @author iamkatrechko
 *         Date: 08.04.2018
 *
 * @property [T] тип синглтона
 */
abstract class Provider<out T> {

    /** Ссылка на экземпляр текущего синглтона */
    private var INSTANCE: T? = null

    /** Функция создания экземпляра синглтона */
    protected abstract fun createInstance(): T

    /** Возвращает единственый экземпляр класса */
    fun getInstance(): T = INSTANCE ?: synchronized(this) {
        INSTANCE ?: createInstance().also { createdInstance ->
            INSTANCE = createdInstance
        }
    }
}
package com.iamkatrechko.clipboardmanager.view.extension

/**
 * Функции расширения для всех объектов
 * @author iamkatrechko
 *         Date: 26.12.17
 **************************************************************************************************************************************************************/

/** Тег для логирования */
val Any.TAG: String
    get() {
        val clazz = this::class.java
        return if (clazz.isAnonymousClass) {
            clazz.enclosingClass
        } else {
            clazz
        }.simpleName
    }

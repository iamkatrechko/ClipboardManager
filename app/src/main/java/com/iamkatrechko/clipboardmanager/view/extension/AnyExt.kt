package com.iamkatrechko.clipboardmanager.view.extension

/**
 * Функции расширения для всех объектов
 * @author iamkatrechko
 *         Date: 26.12.17
 **************************************************************************************************************************************************************/

/** Тег для логирования */
val Any.TAG: String
    get() {
        return this::class.java.run {
            if (isAnonymousClass) {
                enclosingClass
            } else {
                this
            }
        }.simpleName
    }

package com.iamkatrechko.clipboardmanager.view.extension

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Функции расширения для ViewGroup
 * @author iamkatrechko
 *         Date: 20.11.17
 **************************************************************************************************************************************************************/

/**
 * Встраивает разметку интерфейса в текущий ViewGroup
 * @param [layoutId]     идентификатор разметки
 * @param [attachToRoot] следует ли привязать иерархию к корневому элементу?
 */
fun ViewGroup.inflate(layoutId: Int, attachToRoot: Boolean = false): View = LayoutInflater.from(context).inflate(layoutId, this, attachToRoot)
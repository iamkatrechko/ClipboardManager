package com.iamkatrechko.clipboardmanager.view.extension

import android.databinding.BindingAdapter
import android.view.View
import android.view.View.*

/**
 * Функции расширения для класса View
 * @author iamkatrechko
 *         Date: 02.11.2017
 **************************************************************************************************************************************************************/

/**
 * Видимость элемента.
 * true - элемент видим, false - элемент полностью отсутствует
 */
@set:BindingAdapter("visibleOrGone")
var View.visibleOrGone
    get() = visibility == VISIBLE
    set(value) {
        visibility = if (value) VISIBLE else GONE
    }

/**
 * Видимость элемента.
 * true - элемент видим, false - элемент невидим
 */
@set:BindingAdapter("visible")
var View.visible
    get() = visibility == VISIBLE
    set(value) {
        visibility = if (value) VISIBLE else INVISIBLE
    }

/**
 * Видимость элемента.
 * true - элемент невидим, false - элемент видим
 */
@set:BindingAdapter("invisible")
var View.invisible
    get() = visibility == INVISIBLE
    set(value) {
        visibility = if (value) INVISIBLE else VISIBLE
    }

/**
 * Видимость элемента.
 * true - элемент полностью отсутствует, false - элемент видим
 */
@set:BindingAdapter("gone")
var View.gone
    get() = visibility == GONE
    set(value) {
        visibility = if (value) GONE else VISIBLE
    }

/** Устанавливает слушатель нажатий */
fun View.onClick(action: () -> Unit) {
    setOnClickListener { action() }
}
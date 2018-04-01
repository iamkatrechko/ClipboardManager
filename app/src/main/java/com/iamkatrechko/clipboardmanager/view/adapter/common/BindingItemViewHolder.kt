package com.iamkatrechko.clipboardmanager.view.adapter.common

import android.databinding.ViewDataBinding
import android.view.View

/**
 * ViewHolder на биндинге с bind методом элемента списка
 * @author iamkatrechko
 *         Date: 01.04.2018
 *
 * @property [I] тип элемента списка
 * @property [B] тип биндинга разметки
 * @param [view] разметка holder'а
 */
abstract class BindingItemViewHolder<in I, out B : ViewDataBinding>(
        view: View
) : BindingViewHolder<B>(view) {

    /**
     * Привязывает данные элемента списка к виджету
     * @param [item] элемент списка
     */
    abstract fun bindView(item: I)
}
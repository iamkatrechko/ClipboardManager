package com.iamkatrechko.clipboardmanager.view.adapter.common

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * ViewHolder с биндингом разметки
 * @author iamkatrechko
 *         Date: 01.04.2018
 *
 * @property [B] тип биндинга разметки
 * @param [view] разметка holder'а
 */
open class BindingViewHolder<out B : ViewDataBinding>(
        view: View
) : RecyclerView.ViewHolder(view) {

    /** Биндинг разметки текущего холдера */
    protected val binding: B = DataBindingUtil.bind(view) ?: error("Ошибка инициализации разметки")
}
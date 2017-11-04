package com.iamkatrechko.clipboardmanager.view.adapter

import android.database.Cursor
import android.graphics.Typeface
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bignerdranch.android.multiselector.MultiSelector
import com.bignerdranch.android.multiselector.SwappingHolder
import com.iamkatrechko.clipboardmanager.R
import com.iamkatrechko.clipboardmanager.data.database.wrapper.ClipCursor
import com.iamkatrechko.clipboardmanager.domain.ClipsHelper
import com.iamkatrechko.clipboardmanager.domain.util.ClipUtils
import com.iamkatrechko.clipboardmanager.domain.util.Util
import com.iamkatrechko.clipboardmanager.domain.util.UtilPreferences
import com.iamkatrechko.clipboardmanager.view.extensions.setGone

/**
 * Адаптер списка заметок
 * @author iamkatrechko
 *         Date: 01.11.2016
 */
internal class ClipsCursorAdapter constructor(
        /** Слушатель нажатий */
        private val clickListener: ClipClickListener?
) : RecyclerView.Adapter<ClipsCursorAdapter.ViewHolder>() {

    /** Список заметок  */
    private var clipCursor: ClipCursor? = null
    /** Виджет списка  */
    private var attachedRecyclerView: RecyclerView? = null
    /** Помощник множественного выделения  */
    private val multiSelector: MultiSelector = MultiSelector()
    /** Виджет пустого списка  */
    private var emptyView: View? = null

    init {
        //TODO Добавить вывод view при пустом адаптере
        /*showEmpty(mQuery.size() == 0);*/
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        super.onAttachedToRecyclerView(recyclerView)
        attachedRecyclerView = recyclerView
        attachedRecyclerView!!.addItemDecoration(ItemDivider(recyclerView!!.context))
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView?) {
        super.onDetachedFromRecyclerView(recyclerView)
        attachedRecyclerView = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.rv_list_item_clip, parent, false))
    }

    override fun onBindViewHolder(vHolder: ClipsCursorAdapter.ViewHolder, position: Int) {
        clipCursor!!.moveToPosition(position)
        vHolder.bindView(clipCursor!!)
    }

    override fun getItemCount(): Int {
        val count = clipCursor?.wrappedCursor?.count ?: 0
        showEmptyView(count == 0)
        return count
    }

    /**
     * Устанавливает виджет пустого списка
     * @param view виджет пустого списка
     */
    fun setEmptyView(view: View) {
        emptyView = view
    }

    /**
     * Отображает виджет пустого списка
     * @param isShow отображается ли виджет
     */
    private fun showEmptyView(isShow: Boolean) {
        emptyView?.setGone(!isShow)
        attachedRecyclerView?.setGone(isShow)
    }

    /**
     * Перепривязывает данные в адаптере
     * @param cursor новые данные
     */
    fun setCursor(cursor: Cursor?) {
        resetSelectMode()
        clipCursor = ClipCursor(cursor)
        notifyDataSetChanged()
        // Вызов для обновление emptyView
        itemCount
    }

    /** Выключает режим множественного выделения и сбрасывает выделения элементов  */
    fun resetSelectMode() {
        multiSelector.isSelectable = false
        multiSelector.clearSelections()
        clickListener?.onSelectedChange(false, 0)
    }

    /**
     * Возвращает список выделенных записей
     * @return список выделенных записей
     */
    fun getSelectedIds(): List<Long> {
        val result = ArrayList<Long>()
        multiSelector.selectedPositions.forEach { pos ->
            clipCursor?.let {
                it.moveToPosition(pos)
                result.add(it.id)
            }
        }
        return result
    }

    /** Интерфейс слушателя нажатий  */
    interface ClipClickListener {

        /**
         * Нажатие на элемент списка
         * @param clipId идентификатор заметки
         */
        fun onClick(clipId: Long)

        /**
         * Включение выключение режима множественного выделения
         * @param isSelectedMode включено ли множественное выделение
         * @param selectedCount  количество выделений
         */
        fun onSelectedChange(isSelectedMode: Boolean, selectedCount: Int)
    }

    /** Холдер основного элемента списка  */
    internal inner class ViewHolder(
            /** Виджет элемента списка */
            view: View
    ) : SwappingHolder(view, multiSelector) {

        /** Идентификатор заметки */
        private var clipId: Long = 0
        /** Идентификатор заметки */
        private val tvId = view.findViewById(R.id.tvId) as TextView
        /** Заголовок заметки */
        private val tvTitle = view.findViewById(R.id.tvTitle) as TextView
        /** Содержимое заметки */
        private val tvContent = view.findViewById(R.id.tvContent) as TextView
        /** Дата заметки */
        private val tvDate = view.findViewById(R.id.tvDate) as TextView
        /** Категория заметки */
        private val tvCategoryId = view.findViewById(R.id.tvCategoryId) as TextView
        /** Признак удаленной записи */
        private val tvIsDeleted = view.findViewById(R.id.tvIsDeleted) as TextView
        /** Иконка "скопировать" */
        private val ivCopy = view.findViewById(R.id.ivCopy) as ImageView
        /** Иконка избранности */
        private val ivFavorite = view.findViewById(R.id.ivFavorite) as ImageView

        init {
            val context = view.context
            selectionModeBackgroundDrawable = ContextCompat.getDrawable(context, R.drawable.selection_drawable)

            val showMeta = UtilPreferences.isShowMetaInAdapter(context)
            tvId.setGone(!showMeta)
            tvCategoryId.setGone(!showMeta)
            tvIsDeleted.setGone(!showMeta)

            view.setOnClickListener {
                if (multiSelector.tapSelection(this@ViewHolder)) {
                    // Если множественное выделение активно
                    if (getSelectedIds().isEmpty()) {
                        resetSelectMode()
                    } else {
                        clickListener?.onSelectedChange(true, multiSelector.selectedPositions.size)
                    }
                } else {
                    // Если множественное выделение неактивно, жмем
                    clickListener?.onClick(clipId)
                }
            }

            view.setOnLongClickListener(View.OnLongClickListener {
                if (!multiSelector.isSelectable) {
                    multiSelector.isSelectable = true
                    multiSelector.setSelected(this@ViewHolder, true)
                    clickListener?.onSelectedChange(true, 1)
                    return@OnLongClickListener true
                }
                false
            })

            ivCopy.setOnClickListener {
                ClipUtils.copyToClipboard(context, tvContent.text.toString())
                notifyDataSetChanged()
            }

            ivFavorite.setOnClickListener {
                clipCursor!!.moveToPosition(adapterPosition)
                ClipsHelper.setFavorite(context, clipId, !clipCursor!!.isFavorite)
            }
        }

        /**
         * Привязывает данные к виджету списка
         * @param cursor данные
         */
        internal fun bindView(cursor: ClipCursor) {
            clipId = cursor.id
            tvId.text = cursor.id.toString()
            tvTitle.text = cursor.title
            tvContent.text = cursor.content
            tvDate.text = Util.getTimeInString(cursor.date)
            tvCategoryId.text = cursor.categoryId.toString()
            tvIsDeleted.text = cursor.isDeleted.toString()

            ivFavorite.setImageResource(if (cursor.isFavorite) R.drawable.ic_star else R.drawable.ic_star_border)

            val clipInClipboard = cursor.content == ClipUtils.getClipboardText(tvCategoryId.context)
            tvContent.setTypeface(null, if (clipInClipboard) Typeface.BOLD else Typeface.NORMAL)
        }
    }
}


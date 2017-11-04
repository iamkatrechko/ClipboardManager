package com.iamkatrechko.clipboardmanager.view.adapter

import android.database.Cursor
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.iamkatrechko.clipboardmanager.R
import com.iamkatrechko.clipboardmanager.data.database.wrapper.CategoryCursor
import com.iamkatrechko.clipboardmanager.view.extensions.setGone

/**
 * Адаптер списка категорий
 * @author iamkatrechko
 *         Date: 01.11.2016
 */
class CategoriesCursorAdapter(
        /** Слушатель нажатий  */
        private val mMyClickListener: MyClickListener
) : RecyclerView.Adapter<CategoriesCursorAdapter.ViewHolder>() {

    /** Список категорий  */
    private var categories: CategoryCursor? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.addItemDecoration(ItemDivider(recyclerView.context))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.rv_list_item_category, parent, false))
    }

    override fun onBindViewHolder(vHolder: ViewHolder, position: Int) {
        categories?.let {
            it.moveToPosition(position)
            vHolder.bindView(it)
        }
    }

    override fun getItemCount(): Int {
        return categories?.count ?: 0
    }

    /**
     * Устанавливает данные в адаптер
     * @param categoryCursor список категорий
     */
    fun setCursor(categoryCursor: Cursor) {
        this.categories = CategoryCursor(categoryCursor)
    }

    /** Холдер основного элемента списка  */
    inner class ViewHolder(
            /** Виджет элемента списка */
            itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        /** Идентификатор категории  */
        private var _id: Long = 0
        /** Заголовок  */
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle) as TextView
        /** Кнопка "редактировать"  */
        private val ivEdit: ImageView = itemView.findViewById(R.id.ivEdit) as ImageView
        /** Кнопка "удалить"  */
        private val ivDelete: ImageView = itemView.findViewById(R.id.ivDelete) as ImageView

        init {
            ivEdit.setOnClickListener { mMyClickListener.onEditClick(_id) }
            ivDelete.setOnClickListener { mMyClickListener.onDeleteClick(_id) }
        }

        /**
         * Привязывает данные к виджету списка
         * @param cursor данные
         */
        fun bindView(cursor: CategoryCursor) {
            _id = cursor.id
            tvTitle.text = cursor.title
            ivDelete.setGone(adapterPosition == 0)
        }
    }

    /** Слушатель нажатий  */
    interface MyClickListener {

        /**
         * Редактирование категории
         * @param categoryId идентификатор категории
         */
        fun onEditClick(categoryId: Long)

        /**
         * Удаление категории
         * @param categoryId идентификатор категории
         */
        fun onDeleteClick(categoryId: Long)
    }
}

package com.iamkatrechko.clipboardmanager.view.adapter

import android.database.Cursor
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.iamkatrechko.clipboardmanager.R
import com.iamkatrechko.clipboardmanager.data.database.wrapper.CategoryCursor
import com.iamkatrechko.clipboardmanager.databinding.RvListItemCategoryBinding
import com.iamkatrechko.clipboardmanager.view.extension.inflate
import com.iamkatrechko.clipboardmanager.view.extension.setGone

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(DataBindingUtil.bind(parent.inflate(R.layout.rv_list_item_category))!!)


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
            private val binding: RvListItemCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        /** Идентификатор категории  */
        private var _id: Long = 0

        init {
            binding.ivEdit.setOnClickListener { mMyClickListener.onEditClick(_id) }
            binding.ivDelete.setOnClickListener { mMyClickListener.onDeleteClick(_id) }
        }

        /**
         * Привязывает данные к виджету списка
         * @param cursor данные
         */
        fun bindView(cursor: CategoryCursor) {
            _id = cursor.id
            binding.tvTitle.text = cursor.title
            binding.ivDelete.setGone(adapterPosition == 0)
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

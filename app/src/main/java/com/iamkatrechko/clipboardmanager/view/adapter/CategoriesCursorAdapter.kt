package com.iamkatrechko.clipboardmanager.view.adapter

import android.database.Cursor
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.iamkatrechko.clipboardmanager.R
import com.iamkatrechko.clipboardmanager.data.database.wrapper.CategoryCursor
import com.iamkatrechko.clipboardmanager.databinding.RvListItemCategoryBinding
import com.iamkatrechko.clipboardmanager.view.adapter.common.BindingItemViewHolder
import com.iamkatrechko.clipboardmanager.view.extension.inflate
import com.iamkatrechko.clipboardmanager.view.extension.setGone

/**
 * Адаптер списка категорий
 * @author iamkatrechko
 *         Date: 01.11.2016
 */
class CategoriesCursorAdapter(
        /** Слушатель нажатий  */
        private val clickListener: MyClickListener
) : RecyclerView.Adapter<CategoriesCursorAdapter.ViewHolderCategory>() {

    /** Список категорий  */
    private var categories: CategoryCursor? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.addItemDecoration(ItemDivider(recyclerView.context))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderCategory =
            ViewHolderCategory(parent.inflate(R.layout.rv_list_item_category))

    override fun onBindViewHolder(vHolder: ViewHolderCategory, position: Int) {
        categories?.let {
            it.moveToPosition(position)
            vHolder.bindView(it)
        }
    }

    override fun getItemCount(): Int =
            categories?.count ?: 0

    /**
     * Устанавливает данные в адаптер
     * @param categoryCursor список категорий
     */
    fun setCursor(categoryCursor: Cursor) {
        categories = CategoryCursor(categoryCursor)
    }

    inner class ViewHolderCategory(view: View) : BindingItemViewHolder<CategoryCursor, RvListItemCategoryBinding>(view) {

        /** Идентификатор категории */
        private var _id: Long = 0

        init {
            binding.ivEdit.setOnClickListener { clickListener.onEditClick(_id) }
            binding.ivDelete.setOnClickListener { clickListener.onDeleteClick(_id) }
        }

        override fun bindView(item: CategoryCursor) {
            _id = item.id
            binding.tvTitle.text = item.title
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

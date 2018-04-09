package com.iamkatrechko.clipboardmanager.view.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.iamkatrechko.clipboardmanager.R
import com.iamkatrechko.clipboardmanager.data.model.Category
import com.iamkatrechko.clipboardmanager.databinding.RvListItemCategoryBinding
import com.iamkatrechko.clipboardmanager.view.adapter.common.BindingItemViewHolder
import com.iamkatrechko.clipboardmanager.view.extension.gone
import com.iamkatrechko.clipboardmanager.view.extension.inflate

/**
 * Адаптер списка категорий
 * @author iamkatrechko
 *         Date: 01.11.2016
 */
class CategoriesCursorAdapter(
        /** Слушатель нажатий  */
        private val clickListener: MyClickListener
) : RecyclerView.Adapter<CategoriesCursorAdapter.ViewHolderCategory>() {

    /** Список категорий */
    private val categoriesList = ArrayList<Category>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderCategory =
            ViewHolderCategory(parent.inflate(R.layout.rv_list_item_category))

    override fun onBindViewHolder(vHolder: ViewHolderCategory, position: Int) =
            vHolder.bindView(categoriesList[position])

    override fun getItemCount(): Int =
            categoriesList.size

    /**
     * Устанавливает данные в адаптер
     * @param categories список категорий
     */
    fun setCursor(categories: List<Category>) {
        categoriesList.clear()
        categoriesList.addAll(categories)
        notifyDataSetChanged()
    }

    inner class ViewHolderCategory(view: View) : BindingItemViewHolder<Category, RvListItemCategoryBinding>(view) {

        /** Идентификатор категории */
        private var _id: Long = -1

        init {
            binding.ivEdit.setOnClickListener { clickListener.onEditClick(_id) }
            binding.ivDelete.setOnClickListener { clickListener.onDeleteClick(_id) }
        }

        override fun bindView(item: Category) {
            _id = item.id
            binding.tvTitle.text = item.title
            // Не даем удалить основную категорию
            binding.ivDelete.gone = adapterPosition == 0
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

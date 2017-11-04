package com.iamkatrechko.clipboardmanager.view.adapter

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bignerdranch.android.multiselector.MultiSelector
import com.bignerdranch.android.multiselector.SwappingHolder
import com.iamkatrechko.clipboardmanager.R
import com.iamkatrechko.clipboardmanager.data.database.ClipboardDatabaseHelper.ClipCursor
import com.iamkatrechko.clipboardmanager.data.database.DatabaseDescription
import com.iamkatrechko.clipboardmanager.data.database.DatabaseDescription.Clip
import com.iamkatrechko.clipboardmanager.domain.util.ClipUtils
import com.iamkatrechko.clipboardmanager.domain.util.Util
import com.iamkatrechko.clipboardmanager.domain.util.UtilPreferences
import com.iamkatrechko.clipboardmanager.view.extensions.setGone
import java.util.*

/**
 * Адаптер списка заметок
 * @author iamkatrechko
 *         Date: 01.11.2016
 */
internal class ClipsCursorAdapter(
        /** Слушатель нажатий  */
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
        itemCount
    }

    /**
     * Изменяет значение "isFavorite" записи в базе данных
     * @param id         идентификатор записи
     * @param isFavorite флаг избранности
     */
    private fun setIsFavorite(context: Context, id: Long, isFavorite: Boolean) {
        val contentValues = ContentValues()
        contentValues.put(Clip.COLUMN_IS_FAVORITE, isFavorite)

        context.contentResolver.update(Clip.buildClipUri(id), contentValues, null, null)
    }

    /** Выключает режим множественного выделения и сбрасывает выделения элементов  */
    fun resetSelectMode() {
        multiSelector.isSelectable = false
        multiSelector.clearSelections()

        clickListener!!.onSelectedChange(false, 0)
    }

    /** Удаляет из базы данных выбранные множественным выделением записи  */
    fun deleteSelectedItems(context: Context) {
        selectedIds()
                .map { DatabaseDescription.Clip.buildClipUri(it) }
                .forEach { context.contentResolver.delete(it, null, null) }
        resetSelectMode()
    }

    /** Список выделенных записей */
    private fun selectedIds(): List<Long> {
        val result = ArrayList<Long>()
        for (pos in multiSelector.selectedPositions) {
            clipCursor!!.moveToPosition(pos)
            result.add(clipCursor!!.id)
        }
        return result
    }

    /**
     * Объединяет содержимое выделенных записей в одну строку
     * @param splitChar Строка-разделитель между записями
     * @param deleteOld Требуется ли удалить объединенные записи
     * @return Объединенная строка
     */
    private fun getSplitItemsText(context: Context, splitChar: String, deleteOld: Boolean): String {
        var newClipText = ""

        for (id in selectedIds()) {
            val uri = DatabaseDescription.Clip.buildClipUri(id)

            val cursor = ClipCursor(context.contentResolver.query(uri, null, null, null, null))
            if (cursor.moveToFirst()) {
                if (newClipText == "") {
                    newClipText += cursor.content
                } else {
                    newClipText += splitChar + cursor.content
                }
            }
            if (deleteOld) {
                context.contentResolver.delete(uri, null, null)
            }
        }

        return newClipText
    }

    /**
     * Объединяет содержимое выделенных записей в одну строку и создает на ее основе новую запись
     * @param splitChar Строка-разделитель между записями
     * @param deleteOld Требуется ли удалить объединенные записи
     */
    fun splitItems(context: Context, splitChar: String, deleteOld: Boolean) {
        if (multiSelector.selectedPositions.size > 1) {
            val newClipText = getSplitItemsText(context, splitChar, deleteOld)

            val uriInsert = Clip.CONTENT_URI
            val contentValues = Clip.getDefaultContentValues()
            contentValues.put(Clip.COLUMN_CONTENT, newClipText)
            context.contentResolver.insert(uriInsert, contentValues)

            resetSelectMode()
            Toast.makeText(context, R.string.splited, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, R.string.select_cancel, Toast.LENGTH_SHORT).show()
        }
    }

    /** Объединяет содержимое выделенных записей и отравляет в письме  */
    fun shareItems(context: Context) {
        val shareText = getSplitItemsText(context, UtilPreferences.getSplitChar(context), false)
        Util.shareText(context, shareText)
    }

    /**
     * Изменяет категорию выделенных записей
     * @param categoryId идентификатор категории
     */
    fun changeCategory(context: Context, categoryId: Long) {
        for (id in selectedIds()) {
            val uri = DatabaseDescription.Clip.buildClipUri(id)

            val contentValues = ContentValues()
            contentValues.put(Clip.COLUMN_CATEGORY_ID, categoryId)
            context.contentResolver.update(uri, contentValues, null, null)
        }
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
            itemView: View
    ) : SwappingHolder(itemView, multiSelector) {

        /** Идентификатор заметки  */
        private var _id: Long = 0
        /** Идентификатор заметки  */
        private val tvId: TextView
        /** Заголовок заметки  */
        private val tvTitle: TextView
        /** Содержимое заметки  */
        private val tvContent: TextView
        /** Дата заметки  */
        private val tvDate: TextView
        /** Категория заметки  */
        private val tvCategoryId: TextView
        /** Признак удаленной записи  */
        private val tvIsDeleted: TextView
        /** Иконка "скопировать"  */
        private val ivCopy: ImageView
        /** Иконка избранности  */
        private val ivFavorite: ImageView

        init {
            val context = itemView.context
            selectionModeBackgroundDrawable = context.resources.getDrawable(R.drawable.selection_drawable)

            tvId = itemView.findViewById(R.id.tvId) as TextView
            tvTitle = itemView.findViewById(R.id.tvTitle) as TextView
            tvContent = itemView.findViewById(R.id.tvContent) as TextView
            tvDate = itemView.findViewById(R.id.tvDate) as TextView
            tvCategoryId = itemView.findViewById(R.id.tvCategoryId) as TextView
            tvIsDeleted = itemView.findViewById(R.id.tvIsDeleted) as TextView
            ivCopy = itemView.findViewById(R.id.ivCopy) as ImageView
            ivFavorite = itemView.findViewById(R.id.ivFavorite) as ImageView

            if (UtilPreferences.isShowMetaInAdapter(context)) {
                tvId.visibility = View.VISIBLE
                tvCategoryId.visibility = View.VISIBLE
                tvIsDeleted.visibility = View.VISIBLE
            } else {
                tvId.visibility = View.GONE
                tvCategoryId.visibility = View.GONE
                tvIsDeleted.visibility = View.GONE
            }

            itemView.setOnClickListener {
                // Если множественное выделение неактивно
                if (!multiSelector.tapSelection(this@ViewHolder)) {
                    clickListener?.onClick(_id)
                } else {
                    if (multiSelector.selectedPositions.size == 0) {
                        resetSelectMode()
                    } else {
                        clickListener!!.onSelectedChange(true, multiSelector.selectedPositions.size)
                    }
                }
            }

            itemView.setOnLongClickListener(View.OnLongClickListener {
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
                setIsFavorite(context, _id, !clipCursor!!.isFavorite)
            }
        }

        /**
         * Привязывает данные к виджету списка
         * @param cursor данные
         */
        internal fun bindView(cursor: ClipCursor) {
            _id = cursor.id
            tvId.text = cursor.id.toString()
            tvTitle.text = cursor.title
            tvContent.text = cursor.content
            //tvDate.setText(cursor.getDate());
            tvDate.text = Util.getTimeInString(cursor.date)
            tvCategoryId.text = cursor.categoryId.toString()
            tvIsDeleted.text = cursor.isDeleted.toString()

            ivFavorite.setImageResource(if (cursor.isFavorite) R.drawable.ic_star else R.drawable.ic_star_border)

            val clipInClipboard = cursor.content == ClipUtils.getClipboardText(tvCategoryId.context)
            tvContent.setTypeface(null, if (clipInClipboard) Typeface.BOLD else Typeface.NORMAL)
        }
    }
}


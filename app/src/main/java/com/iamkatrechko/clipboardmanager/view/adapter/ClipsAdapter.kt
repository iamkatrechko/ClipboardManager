package com.iamkatrechko.clipboardmanager.view.adapter

import android.database.Cursor
import android.databinding.DataBindingUtil
import android.graphics.Typeface
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.bignerdranch.android.multiselector.MultiSelector
import com.bignerdranch.android.multiselector.SwappingHolder
import com.iamkatrechko.clipboardmanager.App
import com.iamkatrechko.clipboardmanager.R
import com.iamkatrechko.clipboardmanager.data.database.wrapper.ClipCursor
import com.iamkatrechko.clipboardmanager.data.mapper.CursorToClipMapper
import com.iamkatrechko.clipboardmanager.data.model.Clip
import com.iamkatrechko.clipboardmanager.databinding.RvListItemClipBinding
import com.iamkatrechko.clipboardmanager.domain.ClipsHelper
import com.iamkatrechko.clipboardmanager.domain.util.ClipUtils
import com.iamkatrechko.clipboardmanager.domain.util.DateFormatUtils
import com.iamkatrechko.clipboardmanager.domain.util.PrefsManager
import com.iamkatrechko.clipboardmanager.view.adapter.common.ItemDivider
import com.iamkatrechko.clipboardmanager.view.extension.gone
import com.iamkatrechko.clipboardmanager.view.extension.inflate

/**
 * Адаптер списка заметок
 * @author iamkatrechko
 *         Date: 01.11.2016
 */
internal class ClipsAdapter constructor(
        /** Слушатель нажатий */
        private val clickListener: ClipClickListener? = null
) : RecyclerView.Adapter<ClipsAdapter.ViewHolder>() {

    /** Слушатель нажатий */
    var onClickListener: (clipId: Long) -> Unit = {}
    /** Слушатель нажатий на кнопку опций */
    var onMoreClickListener: (view: View, pos: Int, clipId: Long) -> Unit = { _, _, _ -> }

    /** Помощник множественного выделения  */
    private val selector = MultiSelector()
    /** Список записей */
    private val clips = ArrayList<Clip>()

    /** Виджет списка  */
    private var attachedRecyclerView: RecyclerView? = null
    /** Виджет пустого списка  */
    private var emptyView: View? = null

    init {
        //TODO Добавить вывод view при пустом адаптере
        showEmptyView(true)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        attachedRecyclerView = recyclerView
        recyclerView.addItemDecoration(ItemDivider(recyclerView.context))
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        attachedRecyclerView = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(DataBindingUtil.bind(parent.inflate(R.layout.rv_list_item_clip))!!)
    }

    override fun onBindViewHolder(vHolder: ClipsAdapter.ViewHolder, position: Int) {
        vHolder.bindView(clips[position])
    }

    override fun getItemCount(): Int {
        showEmptyView(clips.size == 0)
        return clips.size
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
        emptyView?.gone = !isShow
        attachedRecyclerView?.gone = isShow
    }

    /**
     * Перепривязывает данные в адаптере
     * @param cursor новые данные
     */
    fun setCursor(cursor: Cursor?) {
        if (cursor == null) {
            return
        }
        setClips(CursorToClipMapper.toClips(ClipCursor(cursor)))
    }

    /**
     * Устанавливает список записей в адаптер
     * @param [clipsList] список записей
     */
    fun setClips(clipsList: List<Clip>) {
        resetSelectMode()
        clips.clear()
        clips.addAll(clipsList)
        notifyDataSetChanged()
        // Вызов для обновление emptyView
        itemCount
    }

    /** Выключает режим множественного выделения и сбрасывает выделения элементов  */
    fun resetSelectMode() {
        selector.isSelectable = false
        selector.clearSelections()
        clickListener?.onSelectedChange(false, 0)
    }

    /**
     * Возвращает список выделенных записей
     * @return список выделенных записей
     */
    fun getSelectedIds(): List<Long> {
        return selector.selectedPositions.mapTo(ArrayList()) { pos ->
            clips[pos].id
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
            private val binding: RvListItemClipBinding
    ) : SwappingHolder(binding.root, selector) {

        /** Идентификатор заметки */
        private var clipId: Long = 0

        init {
            val context = binding.root.context
            selectionModeBackgroundDrawable = ContextCompat.getDrawable(context, R.drawable.selection_drawable)

            val showMeta = PrefsManager.getInstance().devShowMetaInClipsList
            binding.tvId.gone = !showMeta
            binding.tvCategoryId.gone = !showMeta
            binding.tvIsDeleted.gone = !showMeta

            binding.root.setOnClickListener {
                if (selector.tapSelection(this@ViewHolder)) {
                    // Если множественное выделение активно
                    if (getSelectedIds().isEmpty()) {
                        resetSelectMode()
                    } else {
                        clickListener?.onSelectedChange(true, selector.selectedPositions.size)
                    }
                } else {
                    // Если множественное выделение неактивно, жмем
                    onClickListener(clipId)
                    clickListener?.onClick(clipId)
                }
            }

            binding.root.setOnLongClickListener(View.OnLongClickListener {
                if (!selector.isSelectable) {
                    selector.isSelectable = true
                    selector.setSelected(this@ViewHolder, true)
                    clickListener?.onSelectedChange(true, 1)
                    return@OnLongClickListener true
                }
                false
            })

            binding.buttonMore.setOnClickListener { onMoreClickListener(it, adapterPosition, clipId) }

            binding.ivCopy.setOnClickListener {
                App.clipManager.toClipboard(binding.tvContent.text.toString())
                notifyDataSetChanged()
            }

            binding.ivFavorite.setOnClickListener {
                val clip = clips[adapterPosition]
                ClipsHelper.setFavorite(clipId, !clip.isFavorite)
            }
        }

        /**
         * Привязывает запись к виджету элемента списка
         * @param [clip] запись
         */
        internal fun bindView(clip: Clip) {
            clipId = clip.id
            binding.tvId.text = clip.id.toString()
            binding.tvTitle.text = clip.title
            binding.tvContent.text = clip.text
            binding.tvDate.text = DateFormatUtils.getTimeInString(clip.dateTime)
            binding.tvCategoryId.text = clip.categoryId.toString()
            binding.tvIsDeleted.text = clip.isDeleted.toString()
            binding.ivFavorite.setImageResource(if (clip.isFavorite) R.drawable.ic_star else R.drawable.ic_star_border)

            val clipInClipboard = clip.text == App.clipManager.getClipboardText()
            binding.tvContent.setTypeface(null, if (clipInClipboard) Typeface.BOLD else Typeface.NORMAL)
        }
    }
}

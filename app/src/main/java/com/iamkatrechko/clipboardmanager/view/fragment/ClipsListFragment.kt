package com.iamkatrechko.clipboardmanager.view.fragment

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.Toast
import com.iamkatrechko.clipboardmanager.R
import com.iamkatrechko.clipboardmanager.data.database.DatabaseDescription.Clip
import com.iamkatrechko.clipboardmanager.data.repository.ClipboardRepository
import com.iamkatrechko.clipboardmanager.domain.ClipsHelper
import com.iamkatrechko.clipboardmanager.domain.param.values.OrderType
import com.iamkatrechko.clipboardmanager.domain.util.Util
import com.iamkatrechko.clipboardmanager.domain.util.UtilPreferences
import com.iamkatrechko.clipboardmanager.view.DialogManager
import com.iamkatrechko.clipboardmanager.view.activity.ClipEditActivity
import com.iamkatrechko.clipboardmanager.view.activity.DeveloperActivity
import com.iamkatrechko.clipboardmanager.view.activity.SearchActivity
import com.iamkatrechko.clipboardmanager.view.adapter.ClipsCursorAdapter
import com.iamkatrechko.clipboardmanager.view.loader.ClipsListLoader

/**
 * Основной фрагмент экрана со списком заметок
 * @author iamkatrechko
 *         Date: 01.11.2016
 */
class ClipsListFragment : Fragment() {

    /** Виджет списка заметок  */
    private lateinit var recyclerView: RecyclerView

    /** Текущая выбранная категория  */
    private var currentCategoryId: Long = -1
    /** Включен ли режим выделения  */
    private var isContextMenu: Boolean = false
    /** Количество выделенных элементов  */
    private var selectedCount = 0
    /** Репозиторий для работы с базой записей  */
    private var repository = ClipboardRepository()
    /** Слушатель для адаптера списка */
    private var listener = object : ClipsCursorAdapter.ClipClickListener {

        override fun onClick(clipId: Long) {
            startActivity(ClipEditActivity.newIntent(activity, Clip.buildClipUri(clipId)))
        }

        override fun onSelectedChange(isSelectedMode: Boolean, selectedCount: Int) {
            isContextMenu = isSelectedMode
            activity.invalidateOptionsMenu()
            this@ClipsListFragment.selectedCount = selectedCount
        }
    }
    /** Адаптер списка заметок  */
    private var clipsCursorAdapter: ClipsCursorAdapter = ClipsCursorAdapter(listener)

    companion object {

        /** Ключ аргумента. Идентификатор текущей категории */
        private const val KEY_ARGUMENT_CATEGORY_ID = "CATEGORY_ID"
        /** Идентификатор загрузчика заметок по категории  */
        const val CLIPS_BY_CATEGORY_LOADER = 1

        /**
         * Возвращает новый экземпляр фрагмента
         * @param [categoryId] идентификатор категории
         * @return новый экземпляр фрагмента
         */
        fun newInstance(categoryId: Long? = null): ClipsListFragment {
            val fragment = ClipsListFragment()

            val bundle = Bundle()
            bundle.putLong(KEY_ARGUMENT_CATEGORY_ID, categoryId ?: -1)
            fragment.arguments = bundle

            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        currentCategoryId = arguments.getLong(KEY_ARGUMENT_CATEGORY_ID)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        recyclerView = view.findViewById(R.id.recyclerView) as RecyclerView
        clipsCursorAdapter.setEmptyView(view.findViewById(R.id.linearEmpty))

        recyclerView.layoutManager = LinearLayoutManager(activity.baseContext)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = clipsCursorAdapter

        showClipsByCategoryId(currentCategoryId)
        return view
    }

    /**
     * Отображает список заметок по категории
     * @param categoryId идентификатор категории
     */
    fun showClipsByCategoryId(categoryId: Long) {
        currentCategoryId = categoryId
        val bundle = Bundle()
        clipsCursorAdapter.resetSelectMode()
        bundle.putLong(ClipsListLoader.KEY_LOADER_CATEGORY_ID, categoryId)
        bundle.putBoolean(ClipsListLoader.KEY_LOADER_ONLY_FAVORITE, UtilPreferences.isShowOnlyFavorite(context))
        bundle.putInt(ClipsListLoader.KEY_LOADER_ORDER_TYPE, UtilPreferences.getOrderType(context).ordinal)
        loaderManager.restartLoader(CLIPS_BY_CATEGORY_LOADER, bundle, ClipsListLoader(context, object : ClipsListLoader.OnDataPreparedListener {
            override fun onPrepared(data: Cursor?) {
                clipsCursorAdapter.setCursor(data)
            }
        }))
    }

    /** Открывает экран создания новой заметки  */
    fun addNewClip() {
        val i = ClipEditActivity.newIntent(context, null)
        startActivity(i)
    }

    /**
     * Меняет значок ToolBar"а (звездочку) в зависимости от того,
     * отображаются ли только избранные записи, или нет
     * @param itemStar           Элемент ToolBar'а
     * @param isOnlyFavoriteShow Отображаются только избранные записи
     */
    private fun changeToolbarItemIcon(itemStar: MenuItem?, isOnlyFavoriteShow: Boolean) {
        if (isContextMenu) return
        if (isOnlyFavoriteShow) {
            itemStar?.setIcon(R.drawable.ic_star_white)
        } else {
            itemStar?.setIcon(R.drawable.ic_star_border_white)
        }
    }

    /** Нажатие кнопки "назад"  */
    fun onBackPressed() {
        if (isContextMenu) {
            clipsCursorAdapter.resetSelectMode()
        } else {
            activity.finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater!!.inflate(if (!isContextMenu) R.menu.menu_main else R.menu.menu_main_context, menu)
        if (selectedCount == 0) {
            activity.setTitle(R.string.app_name)
        } else {
            activity.title = "" + selectedCount
            menu!!.findItem(R.id.action_split).isVisible = selectedCount > 1
        }

        val showOnlyFavorite = UtilPreferences.isShowOnlyFavorite(activity)
        val itemStar = menu?.findItem(R.id.action_show_favorites)
        changeToolbarItemIcon(itemStar, showOnlyFavorite)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item!!.itemId
        when (id) {
        // Окно поиска
            R.id.action_search -> activity.startActivity(Intent(activity, SearchActivity::class.java))
        // Настройка сортировки
            R.id.action_set_order -> DialogManager.showDialogSetOrderType(this)
        // Показывать только избранные
            R.id.action_show_favorites -> {
                val isOnly = UtilPreferences.isShowOnlyFavorite(activity)
                changeToolbarItemIcon(item, !isOnly)
                UtilPreferences.setShowOnlyFavorite(activity, !isOnly)
                showClipsByCategoryId(currentCategoryId)
            }
        // Меню разработчика
            R.id.action_developer_menu -> {
                val i = Intent(activity, DeveloperActivity::class.java)
                activity.startActivity(i)
            }
        // Удалить выделенные записи
            R.id.action_delete -> DialogManager.showDialogDeleteConfirm(this)
        // Соединить выделенные записи
            R.id.action_split -> DialogManager.showDialogSplitClips(this)
        // Поделиться выделенными записями
            R.id.action_share -> {
                val shareText = ClipsHelper.joinToString(context,
                        clipsCursorAdapter.getSelectedIds(),
                        UtilPreferences.getSeparator(context))
                Util.shareText(context, shareText)
            }
        // Сменить категорию выделенных записей
            R.id.action_change_category -> DialogManager.showDialogChangeCategory(this)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data == null) {
            return
        }
        if (resultCode == Activity.RESULT_OK && requestCode == DialogManager.DIALOG_SPLIT_CLIPS) {
            val splitChar = data.getStringExtra("splitChar")
            if (clipsCursorAdapter.getSelectedIds().isNotEmpty()) {
                val deleteOldClips = data.getBooleanExtra("deleteOldClips", false)
                ClipsHelper.joinAndDelete(context, clipsCursorAdapter.getSelectedIds(), splitChar, deleteOldClips)
                clipsCursorAdapter.resetSelectMode()
                Toast.makeText(context, R.string.splited, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, R.string.select_cancel, Toast.LENGTH_SHORT).show()
            }
        }
        if (resultCode == Activity.RESULT_OK && requestCode == DialogManager.DIALOG_CHANGE_CATEGORY) {
            val categoryId = data.getLongExtra("categoryId", 0)
            ClipsHelper.changeCategory(context, clipsCursorAdapter.getSelectedIds(), categoryId)
        }
        if (resultCode == Activity.RESULT_OK && requestCode == DialogManager.DIALOG_DELETE_CONFIRM) {
            val delete = data.getBooleanExtra("delete", false)
            if (delete) {
                repository.deleteClips(context, clipsCursorAdapter.getSelectedIds())
                clipsCursorAdapter.resetSelectMode()
            }
        }
        if (resultCode == Activity.RESULT_OK && requestCode == DialogManager.DIALOG_SET_ORDER_TYPE) {
            val pos = data.getIntExtra("orderType", 0)
            UtilPreferences.setOrderType(activity, OrderType.values()[pos])
            showClipsByCategoryId(currentCategoryId)
        }
    }
}
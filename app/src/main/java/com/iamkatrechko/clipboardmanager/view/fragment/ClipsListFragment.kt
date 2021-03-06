package com.iamkatrechko.clipboardmanager.view.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.Toast
import com.iamkatrechko.clipboardmanager.App
import com.iamkatrechko.clipboardmanager.R
import com.iamkatrechko.clipboardmanager.data.repository.ClipboardRepository
import com.iamkatrechko.clipboardmanager.databinding.FragmentMainBinding
import com.iamkatrechko.clipboardmanager.domain.ClipsHelper
import com.iamkatrechko.clipboardmanager.domain.loader.callback.ClipsLoaderCallback
import com.iamkatrechko.clipboardmanager.domain.param.ClipParam
import com.iamkatrechko.clipboardmanager.domain.param.values.OrderType
import com.iamkatrechko.clipboardmanager.domain.util.IntentUtils
import com.iamkatrechko.clipboardmanager.domain.util.PrefsManager
import com.iamkatrechko.clipboardmanager.view.DialogManager
import com.iamkatrechko.clipboardmanager.view.activity.ClipEditActivity
import com.iamkatrechko.clipboardmanager.view.activity.ClipViewActivity
import com.iamkatrechko.clipboardmanager.view.activity.DeveloperActivity
import com.iamkatrechko.clipboardmanager.view.activity.SearchActivity
import com.iamkatrechko.clipboardmanager.view.adapter.ClipsAdapter
import com.iamkatrechko.clipboardmanager.view.base.BaseFragment
import com.iamkatrechko.clipboardmanager.view.dialog.DialogChangeCategory
import com.iamkatrechko.clipboardmanager.view.dialog.DialogDeleteConfirm
import com.iamkatrechko.clipboardmanager.view.dialog.DialogSplitClips
import com.iamkatrechko.clipboardmanager.view.extension.showToast
import com.iamkatrechko.clipboardmanager.view.presenter.ClipsListPresenter
import com.iamkatrechko.clipboardmanager.view.presenter.ClipsListView

/**
 * Основной фрагмент экрана со списком заметок
 * @author iamkatrechko
 *         Date: 01.11.2016
 */
class ClipsListFragment : BaseFragment<FragmentMainBinding>(), ClipsListView {

    override val layoutId: Int
        get() = R.layout.fragment_main

    /** Презентер экрана */
    private lateinit var presenter: ClipsListPresenter
    /** Виджет списка заметок  */
    private lateinit var recyclerView: RecyclerView

    /** Менеджер буфера обмена */
    private val clipManager = App.clipManager
    /** Текущая выбранная категория  */
    private var currentCategoryId: Long? = -1
    /** Включен ли режим выделения  */
    private var isContextMenu: Boolean = false
    /** Количество выделенных элементов  */
    private var selectedCount = 0
    /** Репозиторий для работы с базой записей  */
    private var repository = ClipboardRepository.getInstance()
    /** Слушатель для адаптера списка */
    private var listener = object : ClipsAdapter.ClipClickListener {

        override fun onClick(clipId: Long) {
            startActivity(ClipEditActivity.newIntent(requireContext(), clipId))
        }

        override fun onSelectedChange(isSelectedMode: Boolean, selectedCount: Int) {
            isContextMenu = isSelectedMode
            this@ClipsListFragment.selectedCount = selectedCount
            activity?.invalidateOptionsMenu()
        }
    }
    /** Адаптер списка заметок  */
    private var clipsAdapter: ClipsAdapter = ClipsAdapter(listener)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        currentCategoryId = arguments?.getLong(KEY_ARGUMENT_CATEGORY_ID, -1L)
        currentCategoryId = if (currentCategoryId == -1L) null else currentCategoryId
        presenter = ClipsListPresenter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        recyclerView = binding.recyclerView
        clipsAdapter.setEmptyView(binding.linearEmpty)

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = clipsAdapter

        clipsAdapter.onMoreClickListener = ::showPopupMenu
        showClipsByCategoryId(currentCategoryId)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.onAttach(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDetach()
    }

    override fun closeScreen() {
        activity?.finish()
    }

    /**
     * Отображает список заметок по категории
     * @param categoryId идентификатор категории
     */
    fun showClipsByCategoryId(categoryId: Long?) {
        currentCategoryId = categoryId
        val order = PrefsManager.getInstance().clipsOrderType
        val onlyFav = PrefsManager.getInstance().isShowOnlyFavorite
        clipsAdapter.resetSelectMode()
        val bundle = Bundle().apply {
            putParcelable(ClipsLoaderCallback.KEY_LOADER_PARAMS, ClipParam(categoryId = categoryId, order = order, onlyFav = onlyFav))
        }
        loaderManager.restartLoader(ClipsLoaderCallback.MAIN_CLIPS_LOADER, bundle,
                ClipsLoaderCallback(requireContext(), clipsAdapter::setClips))
    }

    override fun openClipViewScreen(clipId: Int) {
        startActivity(ClipViewActivity.newIntent(requireContext(), clipId.toLong()))
    }

    override fun openClipEditScreen(clipId: Int) {
        startActivity(ClipEditActivity.newIntent(requireContext(), clipId.toLong()))
    }

    private fun showPopupMenu(view: View, pos: Int, clipId: Long) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.inflate(R.menu.menu_popup_clip_options)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_copy -> {
                    clipManager.toClipboard(repository.getClip(clipId)?.text.orEmpty())
                    clipsAdapter.notifyDataSetChanged()
                }
                R.id.action_copy_close -> presenter.onCopyAndCloseClicked(clipId.toInt())
                R.id.action_view -> presenter.onClipViewClicked(clipId.toInt())
                R.id.action_edit -> presenter.onClipEditClicked(clipId.toInt())
                R.id.action_delete -> {
                    repository.deleteClip(clipId)
                    clipsAdapter.notifyItemRemoved(pos)
                }
                R.id.action_share -> {
                    IntentUtils.sendMail(requireContext(), repository.getClip(clipId)?.text.orEmpty())
                }
            }
            return@setOnMenuItemClickListener true
        }
        popupMenu.show()
    }

    /**
     * Меняет значок ToolBar"а (звездочку) в зависимости от того,
     * отображаются ли только избранные записи, или нет
     * @param itemStar           Элемент ToolBar'а
     * @param isOnlyFavoriteShow Отображаются только избранные записи
     */
    private fun changeToolbarItemIcon(itemStar: MenuItem?, isOnlyFavoriteShow: Boolean) {
        if (isContextMenu) return
        itemStar?.setIcon(if (isOnlyFavoriteShow) R.drawable.ic_star_white else R.drawable.ic_star_border_white)
    }

    /** Нажатие кнопки "назад"  */
    fun onBackPressed() {
        if (isContextMenu) {
            clipsAdapter.resetSelectMode()
        } else {
            activity!!.finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(if (!isContextMenu) R.menu.menu_main else R.menu.menu_main_context, menu)
        if (selectedCount == 0) {
            activity?.setTitle(R.string.app_name)
        } else {
            activity?.title = getString(R.string.selected_count, selectedCount.toString())
            menu.findItem(R.id.action_split)?.isVisible = selectedCount > 1
        }

        val showOnlyFavorite = PrefsManager.getInstance().isShowOnlyFavorite
        val itemStar = menu.findItem(R.id.action_show_favorites)
        changeToolbarItemIcon(itemStar, showOnlyFavorite)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // Окно поиска
            R.id.action_search -> startActivity(Intent(activity, SearchActivity::class.java))
            // Настройка сортировки
            R.id.action_set_order -> DialogManager.showDialogSetOrderType(this)
            // Показывать только избранные
            R.id.action_show_favorites -> {
                val isOnly = PrefsManager.getInstance().isShowOnlyFavorite
                changeToolbarItemIcon(item, !isOnly)
                PrefsManager.getInstance().isShowOnlyFavorite = !isOnly
                showClipsByCategoryId(currentCategoryId)
            }
            // Меню разработчика
            R.id.action_developer_menu -> {
                startActivity(Intent(requireContext(), DeveloperActivity::class.java))
            }
            // Удалить выделенные записи
            R.id.action_delete -> DialogManager.showDialogDeleteConfirm(this)
            // Соединить выделенные записи
            R.id.action_split -> DialogManager.showDialogSplitClips(this)
            // Поделиться выделенными записями
            R.id.action_share -> {
                val shareText = ClipsHelper.joinToString(
                        clipsAdapter.getSelectedIds(),
                        PrefsManager.getInstance().clipSplitChar)
                IntentUtils.sendMail(requireContext(), shareText)
            }
            // Сменить категорию выделенных записей
            R.id.action_change_category -> DialogManager.showDialogChangeCategory(this)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK || data == null) {
            return
        }
        if (requestCode == DialogManager.DIALOG_SPLIT_CLIPS) {
            val splitChar = data.getStringExtra(DialogSplitClips.KEY_SPLIT_CHAR)
            if (clipsAdapter.getSelectedIds().isNotEmpty()) {
                val deleteOldClips = data.getBooleanExtra(DialogSplitClips.KEY_IS_DELETE_OLD_CLIPS, false)
                ClipsHelper.joinAndDelete(clipsAdapter.getSelectedIds(), splitChar, deleteOldClips)
                clipsAdapter.resetSelectMode()
                showToast(R.string.splited)
            } else {
                showToast(R.string.select_cancel)
            }
        }
        if (requestCode == DialogManager.DIALOG_CHANGE_CATEGORY) {
            val categoryId = data.getLongExtra(DialogChangeCategory.KEY_CATEGORY_ID, 0)
            ClipsHelper.changeCategory(clipsAdapter.getSelectedIds(), categoryId)
        }
        if (requestCode == DialogManager.DIALOG_DELETE_CONFIRM) {
            val delete = data.getBooleanExtra(DialogDeleteConfirm.KEY_IS_DELETE, false)
            if (delete) {
                repository.deleteClips(clipsAdapter.getSelectedIds())
                clipsAdapter.resetSelectMode()
            }
        }
        if (requestCode == DialogManager.DIALOG_SET_ORDER_TYPE) {
            val pos = data.getIntExtra("orderType", 0)
            PrefsManager.getInstance().clipsOrderType = OrderType.values()[pos]
            showClipsByCategoryId(currentCategoryId)
        }
    }

    companion object {

        /** Ключ аргумента. Идентификатор текущей категории */
        private const val KEY_ARGUMENT_CATEGORY_ID = "CATEGORY_ID"

        /**
         * Возвращает новый экземпляр фрагмента
         * @param [categoryId] идентификатор категории
         * @return новый экземпляр фрагмента
         */
        fun newInstance(categoryId: Long? = null): ClipsListFragment {
            val fragment = ClipsListFragment()

            fragment.arguments = Bundle().apply {
                putLong(KEY_ARGUMENT_CATEGORY_ID, categoryId ?: -1)
            }

            return fragment
        }
    }
}

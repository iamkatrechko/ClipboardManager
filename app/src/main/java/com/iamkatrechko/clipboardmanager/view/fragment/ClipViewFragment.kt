package com.iamkatrechko.clipboardmanager.view.fragment

import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.*
import androidx.core.os.bundleOf
import com.iamkatrechko.clipboardmanager.R
import com.iamkatrechko.clipboardmanager.data.model.Clip
import com.iamkatrechko.clipboardmanager.data.repository.CategoryRepository
import com.iamkatrechko.clipboardmanager.data.repository.ClipboardRepository
import com.iamkatrechko.clipboardmanager.databinding.FragmentClipViewBinding
import com.iamkatrechko.clipboardmanager.domain.ClipsHelper
import com.iamkatrechko.clipboardmanager.domain.util.ClipUtils
import com.iamkatrechko.clipboardmanager.domain.util.DateFormatUtils
import com.iamkatrechko.clipboardmanager.domain.util.IntentUtils
import com.iamkatrechko.clipboardmanager.view.DialogManager
import com.iamkatrechko.clipboardmanager.view.activity.ClipEditActivity
import com.iamkatrechko.clipboardmanager.view.dialog.DialogChangeCategory
import com.iamkatrechko.clipboardmanager.view.extension.TAG

/**
 * Фрагмент экрана просмотра записи
 * @author iamkatrechko
 *         Date: 01.11.2016
 */
class ClipViewFragment : Fragment() {

    /** Идентификатор текущей записи */
    private var clipId: Long? = null
    /** Идентификатор текущей категории  */
    private var categoryId: Long = -1
    /** Флаг принадлежности к избранным  */
    private var isFavorite = false

    /** Биндинг разметки */
    private lateinit var binding: FragmentClipViewBinding

    /** Репозиторий записей */
    private val clipRepository = ClipboardRepository.getInstance()
    /** Репозиторий категорий */
    private val catRepository = CategoryRepository.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        clipId = arguments!!.getLong(KEY_CLIP_ID)
        Log.i(TAG, "Просмотр заметки [id=$clipId]")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_clip_view, container, false)

        binding.linearCategory.setOnClickListener { DialogManager.showDialogChangeCategory(this) }
        binding.ivIsFavorite.setOnClickListener { setIsFavorite(!isFavorite) }
        binding.ivCopy.setOnClickListener { ClipUtils.copyToClipboard(context!!, binding.textViewContent.text.toString()) }
        binding.ivShare.setOnClickListener { IntentUtils.sendMail(context!!, binding.textViewTitle.text.toString()) }

        loadClip(clipId!!)

        binding.fab.setOnClickListener { startActivity(ClipEditActivity.newIntent(context!!, clipId)) }
        return binding.root
    }

    /** Удаляет текущую заметку */
    private fun deleteClip() {
        clipRepository.deleteClip(context!!, clipId!!)
        activity?.finish()
    }

    /**
     * Изменяет принадлежность заметки к избранным
     * @param [fav] принадлежность заметки к избранным
     */
    private fun setIsFavorite(fav: Boolean) {
        isFavorite = fav
        setFavIcon(isFavorite)
        ClipsHelper.setFavorite(context!!, clipId!!, fav)
    }

    /**
     * Загружает редактируемую запись и обновляет интерфейс
     * @param [clipId] идентификатор записи
     */
    private fun loadClip(clipId: Long) {
        val clip = clipRepository.getClip(context!!, clipId)!!
        initViews(clip)
        loadCategory(clip.categoryId)
    }

    /**
     * Загружает категорию записи и обновляет интерфейс
     * @param [categoryId] идентификатор категории
     */
    private fun loadCategory(categoryId: Long) {
        val category = catRepository.getCategory(context!!, categoryId)!!
        binding.tvCategory.text = category.title
    }

    /**
     * Инициализирует экран по записи
     * @param [clip] запись
     */
    private fun initViews(clip: Clip) {
        binding.textViewTitle.text = clip.title
        binding.textViewContent.text = clip.text
        binding.tvDate.text = DateFormatUtils.getTimeInString(clip.dateTime)
        categoryId = clip.categoryId
        isFavorite = clip.isFavorite
        setFavIcon(isFavorite)
    }

    /** Обновляет кнопку звездочки (избранность) */
    private fun setFavIcon(isFav: Boolean) {
        binding.ivIsFavorite.setImageResource(if (isFav) R.drawable.ic_star else R.drawable.ic_star_border)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK || data == null) {
            return
        }
        if (DialogManager.DIALOG_CHANGE_CATEGORY == requestCode) {
            // TODO Сохранять измененную категорию
            categoryId = data.getLongExtra(DialogChangeCategory.KEY_CATEGORY_ID, 0)
            loadCategory(categoryId)
            return
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_edit_clip, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> activity?.onBackPressed()
            R.id.action_delete -> deleteClip()
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {

        /** Ключ аргумента. Id записи */
        private const val KEY_CLIP_ID = "KEY_CLIP_ID"

        /** Возвращает новый экземпляр фрагмента */
        fun newInstance(clipId: Long) = ClipViewFragment().apply {
            arguments = bundleOf(
                    KEY_CLIP_ID to clipId
            )
        }
    }
}

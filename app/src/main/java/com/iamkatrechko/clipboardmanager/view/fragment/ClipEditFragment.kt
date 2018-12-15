package com.iamkatrechko.clipboardmanager.view.fragment

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.*
import androidx.core.os.bundleOf
import com.iamkatrechko.clipboardmanager.R
import com.iamkatrechko.clipboardmanager.data.database.DatabaseDescription.ClipsTable
import com.iamkatrechko.clipboardmanager.data.model.Clip
import com.iamkatrechko.clipboardmanager.data.repository.CategoryRepository
import com.iamkatrechko.clipboardmanager.data.repository.ClipboardRepository
import com.iamkatrechko.clipboardmanager.databinding.FragmentClipEditBinding
import com.iamkatrechko.clipboardmanager.domain.request.InsertClipRequest
import com.iamkatrechko.clipboardmanager.domain.util.ClipUtils
import com.iamkatrechko.clipboardmanager.domain.util.DateFormatUtils
import com.iamkatrechko.clipboardmanager.domain.util.IntentUtils
import com.iamkatrechko.clipboardmanager.domain.util.UtilPreferences
import com.iamkatrechko.clipboardmanager.view.DialogManager
import com.iamkatrechko.clipboardmanager.view.dialog.DialogChangeCategory
import com.iamkatrechko.clipboardmanager.view.dialog.DialogSaveClip
import com.iamkatrechko.clipboardmanager.view.extension.TAG
import com.iamkatrechko.clipboardmanager.view.extension.showToast
import com.jakewharton.rxbinding2.widget.RxTextView

/**
 * Фрагмент экрана редактирования заметки
 * @author iamkatrechko
 *         Date: 01.11.2016
 */
class ClipEditFragment : Fragment(), View.OnClickListener {

    /** Создание новой заметки  */
    private val isNewClip: Boolean
        get() = clipId == null || clipId == -1L
    /** Режим редактирования  */
    private var isEditMode: Boolean = false
    /** Необходимость сохранения  */
    private var saveNeed: Boolean = false

    /** Идентификатор текущей записи */
    private var clipId: Long? = null
    /** Идентификатор текущей категории  */
    private var categoryId: Long = -1
    /** Флаг принадлежности к избранным  */
    private var isFavorite = false

    /** Биндинг разметки */
    private lateinit var binding: FragmentClipEditBinding

    /** Репозиторий записей */
    private val clipRepository = ClipboardRepository.getInstance()
    /** Репозиторий категорий */
    private val catRepository = CategoryRepository.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        val action = arguments?.getInt(KEY_ACTION, -1)
        isEditMode = action != 1
        clipId = arguments?.getLong(KEY_CLIP_ID)

        Log.i(TAG, "Редактирование заметки [id=$clipId]")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_clip_edit, container, true)

        binding.linearCategory.setOnClickListener(this)
        binding.ivIsFavorite.setOnClickListener(this)
        binding.ivCopy.setOnClickListener(this)
        binding.ivShare.setOnClickListener(this)

        if (isNewClip) {
            isEditMode = true
            categoryId = 1

            loadCategory(categoryId)
            binding.etContent.requestFocus()
        } else {
            loadClip(clipId!!)
        }

        if (isEditMode) {
            binding.etTitle.isEnabled = true
            binding.etContent.isEnabled = true
            toEditMode()
        }
        if (savedInstanceState != null) {
            categoryId = savedInstanceState.getLong("categoryId")
        }
        binding.fab.setOnClickListener({
            if (!isEditMode) {
                // Если стоит режим просмотра
                toEditMode()
                binding.etContent.requestFocus()
            } else {
                saveClip()
            }
        })
        return view
    }

    /** Переключает в режим редактирования  */
    private fun toEditMode() {
        isEditMode = true
        binding.etTitle.isEnabled = true
        binding.etContent.isEnabled = true
        binding.fab.setImageResource(R.drawable.ic_done_24dp)
    }

    /** Сохраняет заметку  */
    private fun saveClip() {
        // Создание объекта ContentValues с парами "ключ—значение"
        if (binding.etContent.text.isEmpty()) {
            showToast(getString(R.string.enter_clip_text))
            return
        }

        val isSuccess: Boolean
        if (isNewClip) {
            if (binding.etTitle.text.isEmpty()) {
                val titleLength = Math.min(25, binding.etContent.text.length)
                binding.etTitle.setText(binding.etContent.text.toString().substring(0, titleLength))
            }
            val request = InsertClipRequest(
                    binding.etTitle.text.toString(),
                    binding.etContent.text.toString(),
                    categoryId.toInt(),
                    isFavorite
            )
            isSuccess = clipRepository.insertClip(request) != null
        } else {
            val clip = Clip(
                    clipId!!,
                    binding.etTitle.text.toString(),
                    binding.etContent.text.toString(),
                    isFavorite,
                    categoryId = categoryId
            )
            isSuccess = clipRepository.updateClip(clipId!!, clip)
        }
        showToast(if (isSuccess) R.string.saved else R.string.error_save)
        activity?.finish()
    }

    /** Удаляет текущую заметку */
    private fun deleteClip() {
        if (!isNewClip) {
            clipRepository.deleteClip(clipId!!)
        }
        activity?.finish()
    }

    /**
     * Изменяет принадлежность заметки к избранным
     * @param [fav] принадлежность заметки к избранным
     */
    private fun setIsFavorite(fav: Boolean) {
        saveNeed = true
        isFavorite = fav
        setFavIcon(isFavorite)

        if (!isEditMode && !isNewClip) {
            clipRepository.setFavorite(clipId!!, fav)
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.ivShare -> IntentUtils.sendMail(context!!, binding.etTitle.text.toString())
            R.id.ivIsFavorite -> setIsFavorite(!isFavorite)
            R.id.ivCopy -> ClipUtils.copyToClipboard(context!!, binding.etContent.text.toString())
            R.id.linearCategory -> DialogManager.showDialogChangeCategory(this)
        }
    }

    /**
     * Загружает редактируемую запись и обновляет интерфейс
     * @param [clipId] идентификатор записи
     */
    private fun loadClip(clipId: Long) {
        val clip = clipRepository.getClip(clipId)!!
        initViews(clip)
        loadCategory(clip.categoryId)
    }

    /**
     * Загружает категорию записи и обновляет интерфейс
     * @param [categoryId] идентификатор категории
     */
    private fun loadCategory(categoryId: Long) {
        val category = catRepository.getCategory(categoryId)!!
        binding.tvCategory.text = category.title
    }

    /**
     * Инициализирует экран по записи
     * @param [clip] запись
     */
    private fun initViews(clip: Clip) {
        binding.etTitle.setText(clip.title)
        binding.etContent.setText(clip.text)
        binding.tvDate.text = DateFormatUtils.getTimeInString(clip.dateTime)
        categoryId = clip.categoryId
        isFavorite = clip.isFavorite
        setFavIcon(isFavorite)

        RxTextView.textChanges(binding.etTitle).skipInitialValue()
                .mergeWith(RxTextView.textChanges(binding.etContent).skipInitialValue())
                .subscribe { saveNeed = true }
    }

    /**
     * Обновляет кнопку звездочки (избранность)
     * @param [isFav] принадлежность к избранным
     */
    private fun setFavIcon(isFav: Boolean) {
        if (isFav) {
            binding.ivIsFavorite.setImageResource(R.drawable.ic_star)
        } else {
            binding.ivIsFavorite.setImageResource(R.drawable.ic_star_border)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK || data == null) {
            return
        }
        if (DialogManager.DIALOG_CHANGE_CATEGORY == requestCode) {
            categoryId = data.getLongExtra(DialogChangeCategory.KEY_CATEGORY_ID, 0)
            loadCategory(categoryId)
            saveNeed = true
            return
        }
        if (DialogManager.DIALOG_CANCEL_CHANGES == requestCode) {
            val save = data.getBooleanExtra(DialogSaveClip.KEY_IS_SAVE, true)
            if (!save) {
                activity?.finish()
            } else {
                saveClip()
            }
            return
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong("categoryId", categoryId)
        // TODO Сохранить всю информацию при перевороте
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_edit_clip, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete -> {
                deleteClip()
                onBackPressed()
            }
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    /** Нажатие на кнопку назад */
    fun onBackPressed() {
        if (isEditMode && saveNeed) {
            if (UtilPreferences.getShowSaveDialogBeforeExit(context)) {
                DialogManager.showDialogCancel(this)
            } else {
                saveClip()
                activity?.finish()
            }
        } else {
            activity?.finish()
        }
    }

    companion object {

        /** Ключ аргумента. URI заметки */
        private const val KEY_CLIP_ID = "KEY_CLIP_ID"
        /** Ключ аргумента. Действие при открытии (просмотр/редактирование) */
        private const val KEY_ACTION = "KEY_ACTION"

        /**
         * Возвращает новый экземпляр фрагмента
         * @param clipId id редактируемой записи
         */
        fun newInstance(clipId: Long?, action: Int): ClipEditFragment {
            val fragment = ClipEditFragment()
            fragment.arguments = bundleOf(
                    KEY_CLIP_ID to clipId,
                    KEY_ACTION to action
            )
            return fragment
        }
    }
}

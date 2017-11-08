package com.iamkatrechko.clipboardmanager.view.fragment

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.iamkatrechko.clipboardmanager.R
import com.iamkatrechko.clipboardmanager.data.database.DatabaseDescription.ClipsTable
import com.iamkatrechko.clipboardmanager.data.model.Clip
import com.iamkatrechko.clipboardmanager.data.repository.CategoryRepository
import com.iamkatrechko.clipboardmanager.data.repository.ClipboardRepository
import com.iamkatrechko.clipboardmanager.domain.util.ClipUtils
import com.iamkatrechko.clipboardmanager.domain.util.DateFormatUtils
import com.iamkatrechko.clipboardmanager.domain.util.IntentUtils
import com.iamkatrechko.clipboardmanager.domain.util.UtilPreferences
import com.iamkatrechko.clipboardmanager.view.DialogManager
import com.iamkatrechko.clipboardmanager.view.extension.showToast
import com.jakewharton.rxbinding2.widget.RxTextView

/**
 * Фрагмент экрана редактирования заметки
 * @author iamkatrechko
 *         Date: 01.11.2016
 */
class ClipEditFragment : Fragment(), View.OnClickListener {

    /** Создание новой заметки  */
    private var isNewClip: Boolean = false
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

    /** Текстовое поле с заголовком  */
    private lateinit var etTitle: EditText
    /** Текстовое поле с содержимым  */
    private lateinit var etContent: EditText
    /** Дата создания записи  */
    private lateinit var tvDate: TextView
    /** Название категории  */
    private lateinit var tvCategoryName: TextView
    /** Иконка принадлежности к избранным  */
    private lateinit var ivIsFavorite: ImageView
    /** Кнопка копирования в буфер  */
    private lateinit var ivCopy: ImageView
    /** Кнопка "поделиться"  */
    private lateinit var ivShare: ImageView
    /** Лэйаут с выбором категории  */
    private lateinit var linearCategory: LinearLayout
    /** Иконка сохранения/редкатирования  */
    private lateinit var fab: FloatingActionButton

    /** Слушатель редактирования полей заголовка и содержимого */
    private val listener: (s: CharSequence) -> Unit = { saveNeed = true }
    /** Репозиторий записей */
    private val clipRepository = ClipboardRepository()
    /** Репозиторий категорий */
    private val catRepository = CategoryRepository.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        clipId = arguments.getLong(KEY_CLIP_ID)
        if (clipId == -1L) clipId = null else clipId
        if (clipId == null) {
            isNewClip = true
        }

        Log.d(TAG, clipId.toString())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_clip_edit, container, false)

        etTitle = view.findViewById(R.id.etTitle) as EditText
        etContent = view.findViewById(R.id.etContent) as EditText
        tvDate = view.findViewById(R.id.tvDate) as TextView
        tvCategoryName = view.findViewById(R.id.tvCategory) as TextView
        ivIsFavorite = view.findViewById(R.id.ivIsFavorite) as ImageView
        ivCopy = view.findViewById(R.id.ivCopy) as ImageView
        ivShare = view.findViewById(R.id.ivShare) as ImageView
        linearCategory = view.findViewById(R.id.linearCategory) as LinearLayout
        fab = view.findViewById(R.id.fab) as FloatingActionButton

        linearCategory.setOnClickListener(this)
        ivIsFavorite.setOnClickListener(this)
        ivCopy.setOnClickListener(this)
        ivShare.setOnClickListener(this)

        if (isNewClip) {
            isEditMode = true
            categoryId = 1

            loadCategory(categoryId)
            etContent.requestFocus()
        } else {
            loadClip(clipId!!)
        }

        isEditMode = true
        if (isEditMode) {
            etTitle.isEnabled = true
            etContent.isEnabled = true
            toEditMode()
        }
        if (savedInstanceState != null) {
            categoryId = savedInstanceState.getLong("categoryId")
        }
        fab.setOnClickListener({
            if (!isEditMode) {
                // Если стоит режим просмотра
                toEditMode()
                etContent.requestFocus()
            } else {
                saveClip()
            }
        })
        return view
    }

    /** Переключает в режим редактирования  */
    private fun toEditMode() {
        isEditMode = true
        etTitle.isEnabled = true
        etContent.isEnabled = true
        fab.setImageResource(R.drawable.ic_done_24dp)
    }

    /** Сохраняет заметку  */
    private fun saveClip() {
        // Создание объекта ContentValues с парами "ключ—значение"
        if (etContent.text.isEmpty()) {
            showToast(getString(R.string.enter_clip_text))
            return
        }
        val contentValues = ClipsTable.getDefaultContentValues().apply {
            put(ClipsTable.COLUMN_TITLE, etTitle.text.toString())
            put(ClipsTable.COLUMN_CONTENT, etContent.text.toString())
            put(ClipsTable.COLUMN_IS_FAVORITE, isFavorite)
            put(ClipsTable.COLUMN_CATEGORY_ID, categoryId)
        }

        val isSuccess: Boolean
        if (isNewClip) {
            if (etTitle.text.isEmpty()) {
                val titleLength = Math.min(25, etContent.text.length)
                contentValues.put(ClipsTable.COLUMN_TITLE, etContent.text.toString().substring(0, titleLength))
            }
            isSuccess = clipRepository.insertClip(context, contentValues) != null
        } else {
            isSuccess = clipRepository.updateClip(context, clipId!!, contentValues) > 0
        }
        context.showToast(if (isSuccess) getString(R.string.saved) else getString(R.string.error_save))
        activity.finish()
    }

    /** Удаляет текущую заметку */
    private fun deleteClip() {
        if (!isNewClip) {
            clipRepository.deleteClip(context, clipId!!)
        }
        activity.finish()
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
            val contentValues = ContentValues().apply {
                put(ClipsTable.COLUMN_IS_FAVORITE, fav)
            }
            clipRepository.updateClip(context, clipId!!, contentValues)
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.ivShare -> IntentUtils.sendMail(context, etTitle.text.toString())
            R.id.ivIsFavorite -> setIsFavorite(!isFavorite)
            R.id.ivCopy -> ClipUtils.copyToClipboard(context, etContent.text.toString())
            R.id.linearCategory -> DialogManager.showDialogChangeCategory(this)
        }
    }

    /**
     * Загружает редактируемую запись и обновляет интерфейс
     * @param [clipId] идентификатор записи
     */
    private fun loadClip(clipId: Long) {
        val clip = clipRepository.getClip(context, clipId)!!
        initViews(clip)
        loadCategory(clip.categoryId)
    }

    /**
     * Загружает категорию записи и обновляет интерфейс
     * @param [categoryId] идентификатор категории
     */
    private fun loadCategory(categoryId: Long) {
        val category = catRepository.getCategory(context, categoryId)!!
        tvCategoryName.text = category.title
    }

    /**
     * Инициализирует экран по записи
     * @param [clip] запись
     */
    private fun initViews(clip: Clip) {
        etTitle.setText(clip.title)
        etContent.setText(clip.text)
        tvDate.text = DateFormatUtils.getTimeInString(clip.dateTime)
        categoryId = clip.categoryId
        isFavorite = clip.isFavorite
        setFavIcon(isFavorite)

        RxTextView.textChanges(etTitle).skip(1).subscribe(listener)
        RxTextView.textChanges(etContent).skip(1).subscribe(listener)
    }

    /**
     * Обновляет кнопку звездочки (избранность)
     * @param [isFav] принадлежность к избранным
     */
    private fun setFavIcon(isFav: Boolean) {
        if (isFav) {
            ivIsFavorite.setImageResource(R.drawable.ic_star)
        } else {
            ivIsFavorite.setImageResource(R.drawable.ic_star_border)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK || data == null) {
            return
        }
        if (DialogManager.DIALOG_CHANGE_CATEGORY == requestCode) {
            categoryId = data.getLongExtra("categoryId", 0)
            loadCategory(categoryId)
            saveNeed = true
            return
        }
        if (DialogManager.DIALOG_CANCEL_CHANGES == requestCode) {
            val save = data.getBooleanExtra("save", true)
            if (!save) {
                activity.finish()
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

    /** Нажатие на кнопку назад  */
    fun onBackPressed() {
        if (isEditMode && saveNeed) {
            if (UtilPreferences.getShowSaveDialogBeforeExit(context)) {
                DialogManager.showDialogCancel(this)
            } else {
                saveClip()
                activity.finish()
            }
        } else {
            activity.finish()
        }
    }

    companion object {

        /** Тег для логирования  */
        private val TAG = ClipEditFragment::class.java.simpleName

        /** Ключ аргумента. URI заметки  */
        private val KEY_CLIP_ID = "KEY_CLIP_ID"

        /**
         * Возвращает новый экземпляр фрагмента
         * @param uri URI редактируемой заметки
         * @return новый экземпляр фрагмента
         */
        fun newInstance(uri: Long?): ClipEditFragment {
            val fragment = ClipEditFragment()

            fragment.arguments = Bundle().apply {
                putLong(KEY_CLIP_ID, uri ?: -1)
            }

            return fragment
        }
    }
}

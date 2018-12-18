package com.iamkatrechko.clipboardmanager.view.presenter

import android.util.Log
import com.iamkatrechko.clipboardmanager.R
import com.iamkatrechko.clipboardmanager.data.model.Category
import com.iamkatrechko.clipboardmanager.data.repository.CategoryRepository
import com.iamkatrechko.clipboardmanager.data.repository.ClipboardRepository
import com.iamkatrechko.clipboardmanager.domain.request.InsertClipRequest
import com.iamkatrechko.clipboardmanager.view.base.BasePresenter
import com.iamkatrechko.clipboardmanager.view.extension.TAG

/**
 * Презентер экрана создания/редактирования заметки
 * @author iamkatrechko
 *         Date: 16.12.2018
 *
 * @param clipId идентификатор редактируемой заметки. Либо null, если происходит создание заметки
 */
class ClipEditPresenter(
        private val clipId: Long?
) : BasePresenter<ClipEditView>() {

    /** Создание новой заметки или редактирование */
    private val isNewClip: Boolean
        get() = clipId == null

    /** Репозиторий записей */
    private val clipRepository = ClipboardRepository.getInstance()
    /** Репозиторий категорий */
    private val catRepository = CategoryRepository.getInstance()
    /** Идентификатор выбранной категории */
    private var selectedCategoryId = Category.DEFAULT_CATEGORY_ID.toLong()

    override fun onAttach(view: ClipEditView) {
        super.onAttach(view)
        loggedMode()

        loadClip()
    }

    fun onCategoryClick() {
        view?.showCategoryDialog()
    }

    fun onCategorySelected(categoryId: Long) {
        selectedCategoryId = categoryId
        val category = catRepository.getCategory(categoryId)!!
        view?.showCategory(category.title)
    }

    fun onBackPressed() {
        val clipViewModel = view?.getClipViewModel()
        clipViewModel ?: return
        if (isNewClip) {
            view?.showDialogSave()
        } else {
            val clip = clipRepository.getClip(clipId!!)!!
            if (clip.categoryId != selectedCategoryId ||
                    clip.title != clipViewModel.title ||
                    clip.text != clipViewModel.content) {
                view?.showDialogSave()
            } else {
                view?.closeScreen()
            }
        }
    }

    fun onCancelResult(isSave: Boolean) {
        if (isSave) {
            onSaveClick()
        } else {
            view?.closeScreen()
        }
    }

    fun onSaveClick() {
        val clipViewModel = view?.getClipViewModel()
        clipViewModel ?: return
        if (clipViewModel.content.isEmpty()) {
            view?.showMessage(R.string.enter_clip_text)
            return
        }
        if (isNewClip) {
            val request = InsertClipRequest(
                    clipViewModel.title,
                    clipViewModel.content,
                    selectedCategoryId.toInt()
            )
            clipRepository.insertClip(request)
        } else {
            val clip = clipRepository.getClip(clipId!!)!!
                    .copy(
                            title = clipViewModel.title,
                            text = clipViewModel.content,
                            categoryId = selectedCategoryId
                    )
            clipRepository.updateClip(clipId, clip)
        }
        view?.showMessage(R.string.saved)
        view?.closeScreen()
    }

    private fun loadClip() {
        if (isNewClip) {
            val category = catRepository.getCategory(selectedCategoryId)!!
            view?.showCategory(category.title)
            return
        }
        val clip = clipRepository.getClip(clipId!!)!!
        val category = catRepository.getCategory(clip.categoryId)!!
        selectedCategoryId = clip.categoryId
        view?.showClip(clip)
        view?.showCategory(category.title)
    }

    private fun loggedMode() {
        if (clipId != null) {
            Log.i(TAG, "Редактирование заметки [id=$clipId]")
        } else {
            Log.i(TAG, "Создание новой заметки")
        }
    }
}
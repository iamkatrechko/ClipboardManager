package com.iamkatrechko.clipboardmanager.view.presenter

import android.util.Log
import com.iamkatrechko.clipboardmanager.App
import com.iamkatrechko.clipboardmanager.data.model.Clip
import com.iamkatrechko.clipboardmanager.data.repository.CategoryRepository
import com.iamkatrechko.clipboardmanager.data.repository.ClipboardRepository
import com.iamkatrechko.clipboardmanager.domain.ClipsHelper
import com.iamkatrechko.clipboardmanager.domain.repository.ICategoryRepository
import com.iamkatrechko.clipboardmanager.domain.service.experiment.CursorClipsRepo
import com.iamkatrechko.clipboardmanager.view.base.BasePresenter
import com.iamkatrechko.clipboardmanager.view.extension.TAG
import io.reactivex.disposables.Disposable
import java.util.*

/**
 * Презентер экрана просматриваемой заметки
 * @author iamkatrechko
 *         Date: 16.12.2018
 *
 * @param clipId идентификатор текущей заметки
 */
class ClipViewPresenter(
        private val clipId: Long
) : BasePresenter<ClipViewView>() {

    /** Репозиторий записей */
    private val clipRepository = ClipboardRepository.getInstance()
    /** Репозиторий категорий */
    private val catRepository: ICategoryRepository = CategoryRepository.getInstance()
    /** Репозиторий заметок */
    private var clipDisposable: Disposable? = null
    /** Текущая заметка */
    private var currentClip: Clip? = null
    /** Менеджер буфера обмена */
    private val clipManager = App.clipManager

    override fun onAttach(view: ClipViewView) {
        super.onAttach(view)
        loadClip(clipId)
    }

    fun onCategoryClick() {
        view?.showCategoryChangeDiaolog()
    }

    fun onCopyToClipboardClick() {
        currentClip?.let { clipManager.toClipboard(it.text) }
    }

    fun onShareClick() {
        currentClip?.let { view?.sendEmail(it.text) }
    }

    fun onEditClick() {
        view?.openEditView(clipId)
    }

    fun onFavoriteClick() {
        currentClip?.let { ClipsHelper.setFavorite(clipId, !it.isFavorite) }
    }

    fun onDeleteClick() {
        // ToDo: отображать диалог подтверждения
        clipDisposable?.dispose()
        clipRepository.deleteClip(clipId)
        view?.finish()
    }

    fun onCategoryChanged(categoryId: Long) {
        ClipsHelper.changeCategory(Collections.singletonList(clipId), categoryId)
    }

    private fun loadClip(clipId: Long) {
        CursorClipsRepo.getInstance()
                .getClip(clipId)
                .map { it to catRepository.getCategory(it.categoryId)!! }
                .subscribe({ (clip, category) ->
                    currentClip = clip
                    view?.showMessage("onNext")
                    view?.showData(clip, category)
                }, {
                    Log.e(TAG, "Ошибка получения заметки", it)
                    // TODO Диалог
                    view?.showError("onError ${it.message}")
                }, {
                    view?.showMessage("onComplete")
                })
                .also { clipDisposable = it }
                .addToPresenter()
    }
}
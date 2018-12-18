package com.iamkatrechko.clipboardmanager.view.presenter

import com.iamkatrechko.clipboardmanager.data.model.Clip
import com.iamkatrechko.clipboardmanager.view.base.MvpView

/**
 * View экрана создания/редактирования заметки
 * @author iamkatrechko
 *         Date: 16.12.2018
 */
interface ClipEditView : MvpView {

    /** Отображает заметку */
    fun showClip(clip: Clip)

    /** Отображает категорию */
    fun showCategory(categoryTitle: String)

    /** Отображает диалог выбора категории */
    fun showCategoryDialog()

    /** Возвращает UI-модель заметки */
    fun getClipViewModel(): ClipViewModel

    /** Отображает диалог сохранения заметки перед выходом */
    fun showDialogSave()

    /** Закрывает экран */
    fun closeScreen()

    /**
     * UI-модель заметки
     * @param title   заголовок
     * @param content контент
     */
    class ClipViewModel(
            val title: String,
            val content: String
    )
}
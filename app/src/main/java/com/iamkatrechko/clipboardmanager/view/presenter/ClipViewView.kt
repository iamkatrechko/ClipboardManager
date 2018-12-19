package com.iamkatrechko.clipboardmanager.view.presenter

import com.iamkatrechko.clipboardmanager.data.model.Category
import com.iamkatrechko.clipboardmanager.data.model.Clip
import com.iamkatrechko.clipboardmanager.view.base.MvpView

/**
 *
 * @author iamkatrechko
 *         Date: 16.12.2018
 */
interface ClipViewView : MvpView {

    fun showData(clip: Clip, category: Category)

    fun openEditView(clipId: Long)

    fun showCategoryChangeDiaolog()

    fun sendEmail(content: String)

    fun finish()
}
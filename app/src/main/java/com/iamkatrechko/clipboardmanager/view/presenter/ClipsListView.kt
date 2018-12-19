package com.iamkatrechko.clipboardmanager.view.presenter

import com.iamkatrechko.clipboardmanager.view.base.MvpView

/**
 *
 * @author iamkatrechko
 *         Date: 19.12.18
 */
interface ClipsListView : MvpView {

    fun closeScreen()

    fun openClipViewScreen(clipId: Int)

    fun openClipEditScreen(clipId: Int)
}
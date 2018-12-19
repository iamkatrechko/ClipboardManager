package com.iamkatrechko.clipboardmanager.view.presenter

import com.iamkatrechko.clipboardmanager.App
import com.iamkatrechko.clipboardmanager.data.repository.ClipboardRepository
import com.iamkatrechko.clipboardmanager.view.base.BasePresenter

/**
 *
 * @author iamkatrechko
 *         Date: 19.12.18
 */
class ClipsListPresenter : BasePresenter<ClipsListView>() {

    private val clipManager = App.clipManager
    private val clipRepository = ClipboardRepository.getInstance()

    fun onClipViewClicked(clipId: Int) {
        view?.openClipViewScreen(clipId)
    }

    fun onClipEditClicked(clipId: Int) {
        view?.openClipEditScreen(clipId)
    }

    fun onCopyAndCloseClicked(clipId: Int) {
        clipManager.toClipboard(clipRepository.getClip(clipId.toLong())?.text.orEmpty())
        view?.closeScreen()
    }
}
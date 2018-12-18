package com.iamkatrechko.clipboardmanager.view.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.iamkatrechko.clipboardmanager.R
import com.iamkatrechko.clipboardmanager.data.model.Clip
import com.iamkatrechko.clipboardmanager.databinding.FragmentClipEditBinding
import com.iamkatrechko.clipboardmanager.view.DialogManager
import com.iamkatrechko.clipboardmanager.view.base.BaseFragment
import com.iamkatrechko.clipboardmanager.view.dialog.DialogChangeCategory
import com.iamkatrechko.clipboardmanager.view.dialog.DialogSaveClip
import com.iamkatrechko.clipboardmanager.view.extension.onClick
import com.iamkatrechko.clipboardmanager.view.presenter.ClipEditPresenter
import com.iamkatrechko.clipboardmanager.view.presenter.ClipEditView

/**
 * Фрагмент экрана редактирования заметки
 * @author iamkatrechko
 *         Date: 01.11.2016
 */
class ClipEditFragment : BaseFragment<FragmentClipEditBinding>(), ClipEditView {

    override val layoutId: Int
        get() = R.layout.fragment_clip_edit

    /** Презентер экрана */
    private lateinit var presenter: ClipEditPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        val clipId = arguments?.getLong(KEY_CLIP_ID, -1L)?.let { if (it != -1L) it else null }
        presenter = ClipEditPresenter(clipId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        binding.apply {
            linearCategory.onClick(presenter::onCategoryClick)
            fab.onClick(presenter::onSaveClick)
            etContent.requestFocus()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.onAttach(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.onDetach()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK || data == null) {
            return
        }
        if (DialogManager.DIALOG_CHANGE_CATEGORY == requestCode) {
            val categoryId = data.getLongExtra(DialogChangeCategory.KEY_CATEGORY_ID, 0)
            presenter.onCategorySelected(categoryId)
            return
        }
        if (DialogManager.DIALOG_CANCEL_CHANGES == requestCode) {
            val save = data.getBooleanExtra(DialogSaveClip.KEY_IS_SAVE, true)
            presenter.onCancelResult(save)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun showClip(clip: Clip) {
        binding.etTitle.setText(clip.title)
        binding.etContent.setText(clip.text)
    }

    override fun showCategory(categoryTitle: String) {
        binding.tvCategory.text = categoryTitle
    }

    override fun closeScreen() {
        activity?.finish()
    }

    override fun showCategoryDialog() {
        DialogManager.showDialogChangeCategory(this)
    }

    override fun showDialogSave() {
        DialogManager.showDialogCancel(this)
    }

    /** Нажатие на кнопку назад */
    fun onBackPressed() {
        presenter.onBackPressed()
    }

    override fun getClipViewModel(): ClipEditView.ClipViewModel =
            ClipEditView.ClipViewModel(binding.etTitle.text.toString(), binding.etContent.text.toString())

    companion object {

        /** Ключ аргумента. URI заметки */
        private const val KEY_CLIP_ID = "KEY_CLIP_ID"

        /**
         * Возвращает новый экземпляр фрагмента
         * @param clipId id редактируемой записи
         */
        fun newInstance(clipId: Long?): ClipEditFragment {
            val fragment = ClipEditFragment()
            fragment.arguments = bundleOf(
                    KEY_CLIP_ID to clipId
            )
            return fragment
        }
    }


}

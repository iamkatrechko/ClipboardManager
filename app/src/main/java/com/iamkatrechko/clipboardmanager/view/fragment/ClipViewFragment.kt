package com.iamkatrechko.clipboardmanager.view.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.os.bundleOf
import com.iamkatrechko.clipboardmanager.R
import com.iamkatrechko.clipboardmanager.data.model.Category
import com.iamkatrechko.clipboardmanager.data.model.Clip
import com.iamkatrechko.clipboardmanager.databinding.FragmentClipViewBinding
import com.iamkatrechko.clipboardmanager.domain.util.ClipUtils
import com.iamkatrechko.clipboardmanager.domain.util.DateFormatUtils
import com.iamkatrechko.clipboardmanager.domain.util.IntentUtils
import com.iamkatrechko.clipboardmanager.view.DialogManager
import com.iamkatrechko.clipboardmanager.view.activity.ClipEditActivity
import com.iamkatrechko.clipboardmanager.view.base.BaseFragment
import com.iamkatrechko.clipboardmanager.view.dialog.DialogChangeCategory
import com.iamkatrechko.clipboardmanager.view.extension.TAG
import com.iamkatrechko.clipboardmanager.view.extension.onClick
import com.iamkatrechko.clipboardmanager.view.presenter.ClipViewPresenter
import com.iamkatrechko.clipboardmanager.view.presenter.ClipViewView

/**
 * Фрагмент экрана просмотра записи
 * @author iamkatrechko
 *         Date: 01.11.2016
 */
class ClipViewFragment : BaseFragment<FragmentClipViewBinding>(), ClipViewView {

    override val layoutId: Int
        get() = R.layout.fragment_clip_view

    /** Презентер экрана */
    private lateinit var presenter: ClipViewPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        val clipId = arguments?.getLong(KEY_CLIP_ID)
                ?: error("Не передан обязательный параметр $KEY_CLIP_ID")
        presenter = ClipViewPresenter(clipId)
        Log.i(TAG, "Просмотр заметки [id=$clipId]")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        binding.apply {
            linearCategory.onClick(presenter::onCategoryClick)
            ivIsFavorite.onClick(presenter::onFavoriteClick)
            ivShare.onClick(presenter::onShareClick)
            ivCopy.onClick(presenter::onCopyToClipboardClick)
            fab.onClick(presenter::onEditClick)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_view_clip, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> activity?.onBackPressed()
            R.id.action_delete -> presenter.onDeleteClick()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun showData(clip: Clip, category: Category) {
        // TODO засунуть в биндинги
        binding.apply {
            textViewTitle.text = clip.title
            textViewContent.text = clip.text
            tvDate.text = DateFormatUtils.getTimeInString(clip.dateTime)
            tvCategory.text = category.title
            //categoryId = clip.categoryId
            ivIsFavorite.setImageResource(if (clip.isFavorite) R.drawable.ic_star else R.drawable.ic_star_border)
        }
    }

    override fun openEditView(clipId: Long) {
        startActivity(ClipEditActivity.newIntent(requireContext(), clipId))
    }

    override fun copyToClipboard(content: String) {
        // ToDo: создать ClipManager и провайдить в презентер
        ClipUtils.copyToClipboard(requireContext(), content)
    }

    override fun sendEmail(content: String) {
        IntentUtils.sendMail(requireContext(), content)
    }

    override fun showCategoryChangeDiaolog() {
        DialogManager.showDialogChangeCategory(this)
    }

    override fun finish() {
        activity?.finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK || data == null) {
            return
        }
        if (DialogManager.DIALOG_CHANGE_CATEGORY == requestCode) {
            val categoryId = data.getLongExtra(DialogChangeCategory.KEY_CATEGORY_ID, -1)
            presenter.onCategoryChanged(categoryId)
        }
    }

    companion object {

        /** Ключ аргумента. Идентификатор заметки */
        private const val KEY_CLIP_ID = "KEY_CLIP_ID"

        /** Возвращает новый экземпляр фрагмента */
        fun newInstance(clipId: Long) = ClipViewFragment().apply {
            arguments = bundleOf(KEY_CLIP_ID to clipId)
        }
    }
}

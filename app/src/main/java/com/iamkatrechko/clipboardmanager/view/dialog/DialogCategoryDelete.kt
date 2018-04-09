package com.iamkatrechko.clipboardmanager.view.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import androidx.core.os.bundleOf
import com.iamkatrechko.clipboardmanager.R
import com.iamkatrechko.clipboardmanager.data.repository.CategoryRepository

/**
 * Диалог удаления категории
 * @author iamkatrechko
 *         Date: 01.11.2016
 */
class DialogCategoryDelete : DialogFragment() {

    private fun sendResult(deleteCategoryId: Long, newCategoryId: Long, moveToNewCategory: Boolean) {
        val intent = Intent().apply {
            putExtra(KEY_DELETE_CATEGORY_ID, deleteCategoryId)
            putExtra("newCategoryId", newCategoryId)
            putExtra("moveToNewCategory", moveToNewCategory)
        }
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
    }

    override fun onCreateDialog(bundle: Bundle?): Dialog {
        var selectedCategoryPos = 0
        val deleteCategoryId = arguments?.getLong(KEY_DELETE_CATEGORY_ID, -1) ?: -1
        val categories = CategoryRepository.getInstance().getCategories(context!!).filter { it.id != deleteCategoryId }
        val valuesList = categories.map { it.title }.toTypedArray()

        // TODO добавить пункт "удалить вместе с категорией"
        return AlertDialog.Builder(context!!)
                .setTitle(R.string.title_delete_and_move)
                .setPositiveButton(R.string.delete) { _, _ ->
                    sendResult(deleteCategoryId, categories[selectedCategoryPos].id, true)
                }
                .setNegativeButton(R.string.cancel) { dialog, id -> dialog.cancel() }
                .setSingleChoiceItems(valuesList, 0) { _, pos -> selectedCategoryPos = pos }
                .create()
    }

    companion object {

        const val KEY_DELETE_CATEGORY_ID = "KEY_DELETE_CATEGORY_ID"

        /**
         * Возвращает новый экземпляр диалога
         * @param categoryId идентификатор удаляемой категории
         * @return новый экземпляр диалога
         */
        fun newInstance(categoryId: Long): DialogCategoryDelete {
            val fragment = DialogCategoryDelete()
            fragment.arguments = bundleOf(KEY_DELETE_CATEGORY_ID to categoryId)
            return fragment
        }
    }
}

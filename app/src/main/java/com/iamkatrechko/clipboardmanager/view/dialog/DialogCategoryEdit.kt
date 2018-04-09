package com.iamkatrechko.clipboardmanager.view.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.widget.EditText
import androidx.core.os.bundleOf
import com.iamkatrechko.clipboardmanager.R
import com.iamkatrechko.clipboardmanager.data.repository.CategoryRepository

/**
 * Диалог редактирования категории
 * @author iamkatrechko
 *         Date: 01.11.2016
 */
class DialogCategoryEdit : DialogFragment() {

    private fun sendResult(categoryId: Long, newName: String) {
        val intent = Intent().apply {
            putExtra(KEY_CATEGORY_ID, categoryId)
            putExtra(KEY_NEW_CATEGORY_ID, newName)
        }
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
    }

    override fun onCreateDialog(bundle: Bundle?): Dialog {
        val view = activity!!.layoutInflater.inflate(R.layout.dialog_category_edit, null)
        val etTitle = view.findViewById<EditText>(R.id.etTitle)
        val categoryId = arguments?.getLong(KEY_CATEGORY_ID, -1L) ?: -1L
        val isNewCategory = categoryId == -1L

        if (isNewCategory) {
            etTitle.setText(R.string.new_category)
        } else {
            val category = CategoryRepository.getInstance().getCategory(context!!, categoryId)
            etTitle.setText(category?.title)
        }

        return AlertDialog.Builder(context!!)
                .setView(view)
                .setTitle(R.string.title_title_change)
                .setPositiveButton(R.string.save) { _, _ -> sendResult(categoryId, etTitle.text.toString()) }
                .setNegativeButton(R.string.cancel, null)
                .create()
    }

    companion object {

        const val KEY_CATEGORY_ID = "KEY_CATEGORY_ID"
        const val KEY_NEW_CATEGORY_ID = "KEY_NEW_CATEGORY_ID"

        fun newInstance(categoryId: Long): DialogCategoryEdit {
            return DialogCategoryEdit().apply {
                arguments = bundleOf(KEY_CATEGORY_ID to categoryId)
            }
        }
    }
}

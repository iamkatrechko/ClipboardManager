package com.iamkatrechko.clipboardmanager.view.dialog

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import com.iamkatrechko.clipboardmanager.R
import com.iamkatrechko.clipboardmanager.data.repository.CategoryRepository
import com.iamkatrechko.clipboardmanager.view.extension.onActivityResultOk

/**
 * Диалог смены категории для заметки
 * @author iamkatrechko
 *         Date: 01.11.2016
 */
class DialogChangeCategory : DialogFragment() {

    /**
     * Возвращает данные обратно во фрагмент
     * @param categoryId идентификатор выбранной категории
     */
    private fun sendResult(categoryId: Long) {
        onActivityResultOk {
            putExtra(KEY_CATEGORY_ID, categoryId)
        }
    }

    override fun onCreateDialog(bundle: Bundle?): Dialog {
        val categories = CategoryRepository.getInstance().getCategories(context!!)
        val names = categories.map { it.title }.toTypedArray()

        return AlertDialog.Builder(activity)
                .setTitle(R.string.move_to)
                .setItems(names) { _, item -> sendResult(categories[item].id) }
                .create()
    }

    companion object {

        const val KEY_CATEGORY_ID = "KEY_CATEGORY_ID"

        fun newInstance(): DialogChangeCategory {
            return DialogChangeCategory()
        }
    }
}


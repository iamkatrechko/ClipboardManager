package com.iamkatrechko.clipboardmanager.view.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import com.iamkatrechko.clipboardmanager.R
import com.iamkatrechko.clipboardmanager.view.extension.onActivityResultOk

/**
 * Диалог подтверждения удаления
 * @author iamkatrechko
 *         Date: 01.11.2016
 */
class DialogDeleteConfirm : DialogFragment() {

    /**
     * Возвращает данные обратно во фрагмент
     * @param delete флаг подтверждения удаления
     */
    private fun sendResult(delete: Boolean) {
        onActivityResultOk {
            putExtra(KEY_IS_DELETE, delete)

        }
    }

    override fun onCreateDialog(bundle: Bundle?): Dialog {
        return AlertDialog.Builder(activity!!)
                .setTitle(R.string.confirm)
                .setMessage(R.string.question_delete_selected)
                .setPositiveButton(R.string.yes) { _, _ -> sendResult(true) }
                .setNegativeButton(R.string.cancel, null).create()
    }

    companion object {

        const val KEY_IS_DELETE = "KEY_IS_DELETE"

        fun newInstance(): DialogDeleteConfirm {
            return DialogDeleteConfirm()
        }
    }
}

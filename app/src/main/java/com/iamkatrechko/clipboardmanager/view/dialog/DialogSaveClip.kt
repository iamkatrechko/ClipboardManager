package com.iamkatrechko.clipboardmanager.view.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import com.iamkatrechko.clipboardmanager.R

/**
 * Диалог сохранения заметки перед выходом
 * @author iamkatrechko
 *         Date: 01.11.2016
 */
class DialogSaveClip : DialogFragment() {

    /**
     * Возвращает данные обратно во фрагмент
     * @param save флаг сохранения заметки
     */
    private fun sendResult(save: Boolean) {
        val intent = Intent().apply {
            putExtra(KEY_IS_SAVE, save)
        }
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
    }

    override fun onCreateDialog(bundle: Bundle?): Dialog {
        return AlertDialog.Builder(activity!!)
                .setTitle(R.string.question_cancel)
                .setMessage(R.string.question_exit_without_save)
                .setPositiveButton(R.string.yes) { _, _ -> sendResult(false) }
                .setNegativeButton(R.string.cancel, null).create()
    }

    companion object {

        const val KEY_IS_SAVE = "KEY_IS_SAVE"

        fun newInstance(): DialogSaveClip {
            return DialogSaveClip()
        }
    }
}
package com.iamkatrechko.clipboardmanager.view.dialog

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import com.iamkatrechko.clipboardmanager.R
import com.iamkatrechko.clipboardmanager.view.extension.onActivityResultOk

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
        onActivityResultOk {
            putExtra(KEY_IS_SAVE, save)
        }
    }

    override fun onCreateDialog(bundle: Bundle?): Dialog {
        return AlertDialog.Builder(activity!!)
                .setTitle(R.string.question_save)
                .setMessage(R.string.question_save_on_exit)
                .setPositiveButton(R.string.yes) { _, _ -> sendResult(true) }
                .setNegativeButton(R.string.no) { _, _ -> sendResult(false) }
                .setNeutralButton(R.string.cancel, null)
                .create()
    }

    companion object {

        const val KEY_IS_SAVE = "KEY_IS_SAVE"

        @JvmStatic
        fun newInstance(): DialogSaveClip =
                DialogSaveClip()
    }
}

package com.iamkatrechko.clipboardmanager.view.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.widget.CheckBox
import android.widget.EditText
import com.iamkatrechko.clipboardmanager.R
import com.iamkatrechko.clipboardmanager.domain.util.PrefsManager

/**
 * Диалог объединения заметок в одну
 * @author iamkatrechko
 *         Date: 01.11.2016
 */
class DialogSplitClips : DialogFragment() {

    /**
     * Возвращает данные обратно во фрагмент
     * @param splitChar      символ-разделитель
     * @param deleteOldClips флаг удаления объединенных заметок
     */
    private fun sendResult(splitChar: String, deleteOldClips: Boolean) {
        val intent = Intent().apply {
            putExtra(KEY_SPLIT_CHAR, splitChar)
            putExtra(KEY_IS_DELETE_OLD_CLIPS, deleteOldClips)
        }
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
    }

    override fun onCreateDialog(bundle: Bundle?): Dialog {
        val view = activity!!.layoutInflater.inflate(R.layout.dialog_split_clips, null)
        val etSplitChar = view.findViewById<EditText>(R.id.etSplitChar)
        val cbDelete = view.findViewById<CheckBox>(R.id.cbDelete)

        etSplitChar.setText(PrefsManager.getInstance().clipSplitChar)

        return AlertDialog.Builder(context!!)
                .setView(view)
                .setTitle(R.string.title_split_items)
                .setPositiveButton(R.string.split) { _, _ ->
                    val splitChar = etSplitChar.text.toString()
                    val deleteOldClips = cbDelete.isChecked

                    PrefsManager.getInstance().clipSplitChar = splitChar
                    sendResult(splitChar, deleteOldClips)
                }
                .setNegativeButton(R.string.cancel, null)
                .create()
    }

    companion object {

        const val KEY_SPLIT_CHAR = "KEY_SPLIT_CHAR"
        const val KEY_IS_DELETE_OLD_CLIPS = "KEY_IS_DELETE_OLD_CLIPS"

        fun newInstance() = DialogSplitClips()
    }
}

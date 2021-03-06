package com.iamkatrechko.clipboardmanager.view.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import com.iamkatrechko.clipboardmanager.R
import com.iamkatrechko.clipboardmanager.domain.param.values.OrderType
import com.iamkatrechko.clipboardmanager.domain.util.PrefsManager
import com.iamkatrechko.clipboardmanager.domain.util.UtilPreferences
import com.iamkatrechko.clipboardmanager.view.extension.onActivityResultOk

/**
 * Диалог выбора сортировки заметки
 * @author iamkatrechko
 *         Date: 01.11.2016
 */
class DialogSetOrderType : DialogFragment() {

    /**
     * Возвращает данные обратно во фрагмент
     * @param orderType тип сортировки
     */
    private fun sendResult(orderType: Int) {
        onActivityResultOk {
            putExtra("orderType", orderType)
        }
    }

    override fun onCreateDialog(bundle: Bundle?): Dialog {
        val itemsNames = OrderType.values().map { getString(it.nameResId) }.toTypedArray()
        val current = PrefsManager.getInstance().clipsOrderType

        return AlertDialog.Builder(context)
                .setTitle(R.string.title_sort)
                .setSingleChoiceItems(itemsNames, current.ordinal) { _, which ->
                    sendResult(which)
                    dismiss()
                }
                .create()
    }

    companion object {

        /**
         * Возвращает новый экземпляр диалога
         * @return новый экземпляр диалога
         */
        fun newInstance(): DialogSetOrderType {
            return DialogSetOrderType()
        }
    }
}

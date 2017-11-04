package com.iamkatrechko.clipboardmanager.view.dialogs

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import com.iamkatrechko.clipboardmanager.domain.param.values.OrderType
import com.iamkatrechko.clipboardmanager.domain.util.UtilPreferences

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
        if (targetFragment == null) {
            return
        }
        val intent = Intent()
        intent.putExtra("orderType", orderType)
        targetFragment.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
    }

    override fun onCreateDialog(bundle: Bundle?): Dialog {
        val mItemsNames = OrderType.values().mapNotNull { getString(it.nameResId) }.toTypedArray()
        val current = UtilPreferences.getOrderType(activity)

        return AlertDialog.Builder(activity)
                .setTitle("Сортировка:")
                .setSingleChoiceItems(mItemsNames, current.ordinal) { dialog, which ->
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

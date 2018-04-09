package com.iamkatrechko.clipboardmanager.view.dialog

import android.app.Dialog
import android.app.DialogFragment
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AlertDialog
import com.iamkatrechko.clipboardmanager.R

/**
 * Диалог включения служебного сервиса
 * @author iamkatrechko
 *         Date: 01.11.2016
 */
class DialogEnableAccessibility : DialogFragment() {

    override fun onCreateDialog(bundle: Bundle): Dialog {
        // TODO Вынести ресурсы
        return AlertDialog.Builder(activity)
                .setTitle("Название")
                .setMessage("Для включения необходимо перейти в настройки и включить специальную слежбу блаблабла")
                .setPositiveButton("Перейти") { _, _ ->
                    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                    startActivityForResult(intent, 122161)
                }
                .setNegativeButton(R.string.cancel, null)
                .create()
    }

    companion object {

        fun newInstance() = DialogEnableAccessibility()
    }
}

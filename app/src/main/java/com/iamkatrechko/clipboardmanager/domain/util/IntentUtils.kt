package com.iamkatrechko.clipboardmanager.domain.util

import android.content.Context
import android.content.Intent
import com.iamkatrechko.clipboardmanager.R

/**
 * Утилиты по работе с интентами
 * @author iamkatrechko
 *         Date: 07.11.17
 */
object IntentUtils {

    /**
     * Отправляет почтовое сообщение на почту разработчика
     * @param context контекст
     * @param text    текст сообщения
     */
    fun sendDeveloperMail(context: Context, text: String?) {
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.type = "plain/text"
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("iamkatrechko@gmail.com"))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name))
        emailIntent.putExtra(Intent.EXTRA_TEXT, text ?: "")
        emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(Intent.createChooser(emailIntent, context.getString(R.string.select_app_to_share)))
    }

    /**
     * Отправляет почтовое сообщение
     * @param context контекст
     * @param text    текст сообщения
     */
    fun sendMail(context: Context, text: String?) {
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.type = "plain/text"
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name))
        emailIntent.putExtra(Intent.EXTRA_TEXT, text ?: "")
        emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(Intent.createChooser(emailIntent, context.getString(R.string.select_app_to_share)))
    }
}

package com.iamkatrechko.clipboardmanager.domain.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.core.content.systemService
import com.iamkatrechko.clipboardmanager.data.model.SimpleClip

/**
 * Утилиты для работы с буфером обмена
 * @author iamkatrechko
 *         Date: 01.08.2017
 */
object ClipUtils {

    /** Идентификатор записи буфера для фильтрации */
    // ToDo: усложнить строку
    const val CLIP_LABEL = "891652"
    /** Идентификатор записи буфера для фильтрации Accessibility сервиса */
    // ToDo: усложнить строку
    const val CLIP_LABEL_ACCESSIBILITY = "126126126"

    /**
     * Отправляет запись в буфер обмена для своего [AccessibilityService]
     * @param text текст записи
     */
    fun sendClipToMyAccessibilityService(context: Context, text: String?) {
        val clipBoard = context.systemService<ClipboardManager>()
        clipBoard.primaryClip = ClipData.newPlainText(CLIP_LABEL_ACCESSIBILITY, text)
    }

    /** Возвращает идентификатор записи в буфере обмена */
    fun getClipboardLabel(context: Context): String {
        val clipBoard = context.systemService<ClipboardManager>()
        return clipBoard.primaryClipDescription?.label?.toString().orEmpty()
    }
}

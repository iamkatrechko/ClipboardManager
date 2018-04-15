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
    const val CLIP_LABEL = "891652"
    /** Идентификатор записи буфера для фильтрации Accessibility сервиса */
    const val CLIP_LABEL_ACCESSIBILITY = "126126126"

    /**
     * Копирует текст в буфер обмена
     * @param text текст для копирования
     */
    fun copyToClipboard(context: Context, text: String?) {
        val clipboard = context.systemService<ClipboardManager>()
        clipboard.primaryClip = ClipData.newPlainText(CLIP_LABEL, text)
    }

    /**
     * Отправляет запись в буфер обмена для своего [AccessibilityService]
     * @param text текст записи
     */
    fun sendClipToMyAccessibilityService(context: Context, text: String?) {
        val clipBoard = context.systemService<ClipboardManager>()
        clipBoard.primaryClip = ClipData.newPlainText(CLIP_LABEL_ACCESSIBILITY, text)
    }

    /** Возвращает текст записи из буфера обмена */
    fun getClipboardText(context: Context): String {
        val clipboard = context.systemService<ClipboardManager>()
        val clipText = if (clipboard.hasPrimaryClip()) clipboard.primaryClip.getItemAt(0).text else ""
        return clipText?.toString().orEmpty()
    }

    /** Возвращает идентификатор записи в буфере обмена */
    fun getClipboardLabel(context: Context): String {
        val clipBoard = context.systemService<ClipboardManager>()
        return clipBoard.primaryClipDescription?.label?.toString().orEmpty()
    }

    /** Возвращает запись буфера обмена */
    fun getClip(context: Context): SimpleClip {
        val text = getClipboardText(context)
        val label = getClipboardLabel(context)
        return SimpleClip(text, label)
    }
}

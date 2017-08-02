package com.iamkatrechko.clipboardmanager.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * Утилиты для работы с буфером обмена
 * @author iamkatrechko
 *         Date: 01.08.2017
 */
public class ClipUtils {

    /** Идентификатор записи буфера для фильтрации */
    public static final String CLIP_LABEL = "891652";
    /** Идентификатор записи буфера для фильтрации Accessibility сервиса */
    public static final String CLIP_LABEL_ACCESSIBILITY = "126126126";

    /** Приватный конструктор */
    private ClipUtils() {
    }

    /**
     * Копирует текст в буфер обмена
     * @param context контекст
     * @param text    текст для копирования
     */
    public static void copyToClipboard(Context context, String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(CLIP_LABEL, text);
        clipboard.setPrimaryClip(clip);
    }

    /**
     * Отправляет запись в буфер обмена для своего {@link android.accessibilityservice.AccessibilityService}
     * @param context контекст
     * @param text    текст записи
     */
    public static void sendClipToMyAccessibilityService(Context context, String text) {
        ClipboardManager clipBoard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = android.content.ClipData.newPlainText(CLIP_LABEL_ACCESSIBILITY, text);
        clipBoard.setPrimaryClip(clip);
    }

    /**
     * Возвращает запись из буфера обмена
     * @param context контекст
     * @return запись из буфера обмена
     */
    public static String getClipboardText(Context context) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        CharSequence clipText = clipboard.hasPrimaryClip() ? clipboard.getPrimaryClip().getItemAt(0).getText() : "";
        return clipText != null ? clipText.toString() : "";
    }

    /**
     * Возвращает идентификатор записи в буфере обмена
     * @param context контекст
     * @return идентификатор записи в буфере обмена
     */
    public static String getClipboardLabel(Context context) {
        ClipboardManager clipBoard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        CharSequence label = clipBoard.getPrimaryClipDescription().getLabel();
        return label != null ? label.toString() : "";
    }
}

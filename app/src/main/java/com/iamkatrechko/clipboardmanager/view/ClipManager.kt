package com.iamkatrechko.clipboardmanager.view

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import com.iamkatrechko.clipboardmanager.data.model.SimpleClip
import com.iamkatrechko.clipboardmanager.domain.IClipManager
import com.iamkatrechko.clipboardmanager.domain.util.ClipUtils

/**
 * Менеджер бумера обмена
 * @author iamkatrechko
 *         Date: 19.12.18
 *
 * @param ctx контекст приложения
 */
class ClipManager(
        private val ctx: Context
) : IClipManager {

    /** Системный менеджер буфера */
    private val clipboard = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    override fun toClipboard(text: String) {
        clipboard.primaryClip = ClipData.newPlainText(ClipUtils.CLIP_LABEL, text)
    }

    override fun getClip(): SimpleClip =
            SimpleClip(getClipboardText(), getClipboardLabel())

    override fun getClipboardText(): String {
        val clipText = if (clipboard.hasPrimaryClip()) clipboard.primaryClip.getItemAt(0).text else ""
        return clipText?.toString().orEmpty()
    }

    private fun getClipboardLabel(): String =
            clipboard.primaryClipDescription?.label?.toString().orEmpty()
}
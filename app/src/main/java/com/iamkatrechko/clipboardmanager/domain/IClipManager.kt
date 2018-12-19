package com.iamkatrechko.clipboardmanager.domain

import com.iamkatrechko.clipboardmanager.data.model.SimpleClip

/**
 * Менеджер буфера обмена
 * @author iamkatrechko
 *         Date: 19.12.18
 */
interface IClipManager {

    fun toClipboard(text: String)

    fun getClipboardText(): String

    fun getClip(): SimpleClip
}
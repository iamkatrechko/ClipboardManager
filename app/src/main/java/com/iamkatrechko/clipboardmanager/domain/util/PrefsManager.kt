package com.iamkatrechko.clipboardmanager.domain.util

import android.content.Context
import android.preference.PreferenceManager
import com.iamkatrechko.clipboardmanager.data.repository.common.Provider
import com.iamkatrechko.clipboardmanager.view.extension.delegateString

/**
 * Хранилище вспомогательных настроек
 * @author iamkatrechko
 *         Date: 13.04.2018
 */
class PrefsManager private constructor(context: Context) {

    /** Хранилище настроек */
    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)

    /** Разделитель записей при их соединении */
    var clipSplitChar by prefs.delegateString(" | ")

    companion object : Provider<PrefsManager>() {

        /** Приватный экземпляр класса */
        private var INSTANCE: PrefsManager? = null

        /** Инициализирует компонент */
        fun init(context: Context) {
            INSTANCE = PrefsManager(context)
        }

        override fun createInstance() = INSTANCE ?: error("Компонент не инициализирован")
    }
}
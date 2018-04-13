package com.iamkatrechko.clipboardmanager.domain.util

import android.content.Context
import android.preference.PreferenceManager
import com.iamkatrechko.clipboardmanager.R
import com.iamkatrechko.clipboardmanager.data.repository.common.Provider
import com.iamkatrechko.clipboardmanager.view.extension.delegateBoolean

/**
 * Хранилище настроек программы
 * @author iamkatrechko
 *         Date: 13.04.2018
 */
class SettingsValues private constructor(context: Context) {

    /** Хранилище настроек */
    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)

    /** Статус работы мониторинга */
    val monitoringEnabled by prefs.delegateBoolean(true, context.getString(R.string.key_settings_monitoring_enable))
    /** Статус отображения уведомления */
    val notificationShow by prefs.delegateBoolean(true, context.getString(R.string.key_settings_notification_enable))
    /** Статус отображения истории записей в уведомлении */
    val displayHistory by prefs.delegateBoolean(true, context.getString(R.string.key_settings_display_history))

    companion object : Provider<SettingsValues>() {

        /** Приватный экземпляр класса */
        private var INSTANCE: SettingsValues? = null

        /** Инициализирует компонент */
        fun init(context: Context) {
            INSTANCE = SettingsValues(context)
        }

        override fun createInstance() = INSTANCE ?: error("Компонент не инициализирован")
    }
}
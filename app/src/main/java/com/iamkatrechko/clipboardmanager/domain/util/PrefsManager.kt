package com.iamkatrechko.clipboardmanager.domain.util

import android.content.Context
import android.preference.PreferenceManager
import com.iamkatrechko.clipboardmanager.data.repository.common.Provider
import com.iamkatrechko.clipboardmanager.domain.param.values.OrderType
import com.iamkatrechko.clipboardmanager.view.extension.delegateBoolean
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
    var clipSplitChar: String by prefs.delegateString(" | ")
    /** Отображение только избранных записей */
    var isShowOnlyFavorite by prefs.delegateBoolean(false)
    /** Отображение только избранных записей в уведомлении */
    var isShowOnlyFavoriteInNotification by prefs.delegateBoolean(false)

    /** Сортировка записей на главном списке */
    var clipsOrderType: OrderType
        get() = OrderType.values()[prefs.getInt("order_type", 0)]
        set(value) = prefs.edit().putInt("order_type", value.ordinal).apply()

    // TODO Вынести в настройки программы
    /** Отображение мета-данных в списке записей (для разработчиков) */
    val devShowMetaInClipsList by prefs.delegateBoolean(false)

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
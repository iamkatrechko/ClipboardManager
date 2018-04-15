package com.iamkatrechko.clipboardmanager.view.fragment

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.ListPreference
import android.preference.Preference
// TODO Подключить библиотечный фрагмент
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import android.util.Log
import android.view.MenuItem
import com.iamkatrechko.clipboardmanager.R
import com.iamkatrechko.clipboardmanager.domain.service.ClipboardService
import com.iamkatrechko.clipboardmanager.domain.util.ServiceUtils
import com.iamkatrechko.clipboardmanager.view.DialogManager
import com.iamkatrechko.clipboardmanager.view.activity.SettingsActivity
import com.iamkatrechko.clipboardmanager.view.extension.TAG
import com.iamkatrechko.clipboardmanager.view.extension.startActivity

/**
 * Фрагмент настроек приложения
 * @author iamkatrechko
 *         Date: 15.04.2018
 */
class SettingsFragment : PreferenceFragment(), SharedPreferences.OnSharedPreferenceChangeListener {

    /** Хранилище настроек */
    lateinit var prefs: SharedPreferences

    /** Слушатель изменения значений (для перехвата и отключения в некоторых случаях) */
    private val preferenceChangeListener = Preference.OnPreferenceChangeListener { preference, value ->
        val key = preference.key
        Log.d(TAG, "Pref: $key, Value:$value")

        when (key) {
            PREF_ACCESSIBILITY_SERVICE -> {
                if (!ServiceUtils.isAccessibilityEnabled(activity)) {
                    DialogManager.showDialogEnableAccessibility(activity)
                    return@OnPreferenceChangeListener false
                }
            }
        }
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(activity)
        // Если специальная служба не работает, отключаем ее в настройках
        val accesSeEnabled = ServiceUtils.isAccessibilityEnabled(activity)
        // TODO Вынести эту настройку в открытие через Intent в xml
        prefs.edit().putBoolean(PREF_ACCESSIBILITY_SERVICE, accesSeEnabled).apply()
        addPreferencesFromResource(R.xml.pref_general)
        setHasOptionsMenu(true)

        prefs.registerOnSharedPreferenceChangeListener(this)

        findPreference(PREF_ACCESSIBILITY_SERVICE).onPreferenceChangeListener = preferenceChangeListener

        bindPreferenceSummaryToValue(findPreference("example_text"))
        bindPreferenceSummaryToValue(findPreference(PREF_NOTIFICATION_PRIORITY))
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            // TODO Данной кнопки слева вверху нету, включить
            activity.startActivity<SettingsActivity>()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        Log.d(TAG, "onSharedPreferenceChanged: $key")

        when (key) {
            PREF_NOTIFICATION_PRIORITY -> {
                setPreferenceTextForList(findPreference(key) as ListPreference)
                ClipboardService.startMyService(activity)
            }
            PREF_DISPLAY_NOTIFICATION, PREF_DISPLAY_HISTORY -> ClipboardService.startMyService(activity)
        }

        if (key == PREF_ENABLE_SERVICE) {
            val enable = sharedPreferences.getBoolean(key, false)
            if (enable) {
                ClipboardService.startMyService(activity)
            } else {
                activity.stopService(Intent(activity, ClipboardService::class.java))
            }
        }
    }

    override fun onPause() {
        super.onPause()
        prefs.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        Log.d("onActivityResult", "" + requestCode)
        if (requestCode == 122161) {
            // TODO Типа изменение флажка на ВКЛ (accessibilityService)
        }
    }

    // Биндим настройки списка для обновления описания при изменении значения
    private fun bindPreferenceSummaryToValue(preference: Preference) {
        preference.onPreferenceChangeListener = sBindPreferenceSummaryToValueListener
        // Форсируем слушатель для применения описания настройки
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, prefs.getString(preference.key, ""))
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    // Биндинг значений в описение настройки при открытии экрана и последующем изменении настройки
    private val sBindPreferenceSummaryToValueListener = Preference.OnPreferenceChangeListener { preference, value ->
        val stringKey = preference.key

        if (preference is ListPreference) {
            setPreferenceTextForList(preference)
        } else {
            preference.summary = value.toString()
        }
        true
    }

    private fun setPreferenceTextForList(preference: ListPreference) {
        val stringValue = prefs.getString(preference.key, "")
        val index = preference.findIndexOfValue(stringValue)

        preference.summary = preference.entries.getOrNull(index)
    }

    companion object {

        const val PREF_ENABLE_SERVICE = "enable_service"
        const val PREF_NOTIFICATION_PRIORITY = "notification_priority"
        const val PREF_DISPLAY_NOTIFICATION = "display_notification"
        const val PREF_DISPLAY_HISTORY = "display_history"
        const val PREF_ACCESSIBILITY_SERVICE = "enable_accessibility_service"

        const val PREF_SHOW_SAVE_DIALOG_BEFORE_EXIT = "show_save_dialog_before_exit"
    }
}
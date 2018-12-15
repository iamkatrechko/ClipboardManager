package com.iamkatrechko.clipboardmanager

import android.app.Application
import android.util.Log
import com.iamkatrechko.clipboardmanager.data.repository.CategoryRepository
import com.iamkatrechko.clipboardmanager.data.repository.ClipboardRepository
import com.iamkatrechko.clipboardmanager.domain.service.ClipboardService
import com.iamkatrechko.clipboardmanager.domain.util.PrefsManager
import com.iamkatrechko.clipboardmanager.domain.util.SettingsValues

/**
 * Класс Application
 * @author iamkatrechko
 *         Date: 02.11.17
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.d("App", "CreateAppInstance")

        SettingsValues.init(this)
        ClipboardRepository.init(this)
        CategoryRepository.init(this)
        PrefsManager.init(this)
        if (SettingsValues.getInstance().monitoringEnabled) {
            ClipboardService.startMyService(this)
        }
    }
}
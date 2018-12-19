package com.iamkatrechko.clipboardmanager

import android.app.Application
import android.util.Log
import com.iamkatrechko.clipboardmanager.data.repository.CategoryRepository
import com.iamkatrechko.clipboardmanager.data.repository.ClipboardRepository
import com.iamkatrechko.clipboardmanager.domain.IClipManager
import com.iamkatrechko.clipboardmanager.domain.service.ClipboardService
import com.iamkatrechko.clipboardmanager.domain.service.experiment.CursorClipsRepo
import com.iamkatrechko.clipboardmanager.domain.util.PrefsManager
import com.iamkatrechko.clipboardmanager.domain.util.SettingsValues
import com.iamkatrechko.clipboardmanager.view.ClipManager

/**
 * Класс Application
 * @author iamkatrechko
 *         Date: 02.11.17
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.d("App", "CreateAppInstance")

        clipManager = ClipManager(this)
        SettingsValues.init(this)
        ClipboardRepository.init(this)
        CategoryRepository.init(this)
        PrefsManager.init(this)
        CursorClipsRepo.init(this)
        if (SettingsValues.getInstance().monitoringEnabled) {
            ClipboardService.startMyService(this)
        }
    }

    companion object {

        /** Менеджер буфера обмена */
        lateinit var clipManager: IClipManager
            private set
    }
}

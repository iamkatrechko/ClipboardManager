package com.iamkatrechko.clipboardmanager

import android.app.Application
import android.util.Log

/**
 * Класс Application
 * @author iamkatrechko
 *         Date: 02.11.17
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.d("App", "CreateAppInstance")
    }
}
package com.iamkatrechko.clipboardmanager.domain.util

import android.content.Context

/**
 * Утилиты по работе с интерфейсом
 * @author iamkatrechko
 *         Date: 07.11.17
 */
object UiUtils {

    /**
     * Переводит DP в PX
     * @param context  контекст
     * @param dipValue количество DP
     * @return количество PX
     */
    fun dp2px(context: Context, dipValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dipValue * scale + 0.5f).toInt()
    }

    /**
     * Переводит PX в DP
     * @param context контекст
     * @param pxValue количество PX
     * @return количество DP
     */
    fun px2dp(context: Context, pxValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }
}
package com.iamkatrechko.clipboardmanager.domain.util

import java.text.SimpleDateFormat
import java.util.*

/**
 * Утилиты по работе с форматированием дат
 * @author iamkatrechko
 *         Date: 07.11.17
 */
object DateFormatUtils {

    /**
     * Возвращает дату в формате "dd MMMM - HH:mm"
     * @param timeInMillis миллисекунды
     * @return дата в формате "dd MMMM - HH:mm"
     */
    fun getTimeInString(timeInMillis: Long): String {
        val calendar = Calendar.getInstance()
        val df: SimpleDateFormat
        try {
            val currentYear = calendar.get(Calendar.YEAR)
            val currentDay = calendar.get(Calendar.DAY_OF_YEAR)
            calendar.timeInMillis = timeInMillis
            df = if (calendar.get(Calendar.YEAR) == currentYear) {
                if (calendar.get(Calendar.DAY_OF_YEAR) == currentDay) {
                    SimpleDateFormat("HH:mm")
                } else {
                    SimpleDateFormat("dd MMMM, HH:mm")
                }
            } else {
                SimpleDateFormat("dd MMMM yyyy, HH:mm")
            }
            return df.format(calendar.time)
        } catch (e: Exception) {
            return ""
        }
    }
}
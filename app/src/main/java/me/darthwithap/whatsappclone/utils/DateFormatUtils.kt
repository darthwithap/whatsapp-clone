package me.darthwithap.whatsappclone.utils

import android.content.Context
import android.text.format.DateUtils
import me.darthwithap.whatsappclone.R
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.*

private fun getCurrentLocale(context: Context) =
    context.resources.configuration.locales[0]

val Date.calendar: Calendar
    get() {
        val field = getInstance()
        field.time = this
        return field
    }

fun Date.isThisWeek(): Boolean {
    val thisCalendar = getInstance()
    val thisWeek = thisCalendar.get(WEEK_OF_YEAR)
    val thisYear = thisCalendar.get(YEAR)

    return calendar.get(YEAR) == thisYear && calendar.get(WEEK_OF_YEAR) == thisWeek
}

fun Date.isToday(): Boolean {
    return DateUtils.isToday(this.time)
}

fun Date.isYesterday(): Boolean {
    val yesterdayCalender = getInstance()
    yesterdayCalender.add(DAY_OF_YEAR, -1)

    return yesterdayCalender.get(YEAR) == calendar.get(YEAR)
            && yesterdayCalender.get(DAY_OF_YEAR) == calendar.get(DAY_OF_YEAR)
}

fun Date.isThisYear(): Boolean {
    return getInstance().get(YEAR) == calendar.get(YEAR)
}

fun Date.isSameDayAs(date: Date): Boolean {
    return this.calendar.get(DAY_OF_YEAR) == date.calendar.get(DAY_OF_YEAR)
}

fun Date.formatAsTime(): String {
    val hour = calendar.get(HOUR_OF_DAY).toString().padStart(2, '0')
    val minute = calendar.get(MINUTE).toString().padStart(2, '0')
    return "$hour:$minute"
}

fun Date.formatAsYesterday(c: Context): String {
    return c.getString(R.string.yesterday)
}

fun Date.formatAsWeekDay(c: Context): String {
    val s = { id: Int -> c.getString(id) }

    return when (calendar.get(DAY_OF_WEEK)) {
        MONDAY -> s(R.string.monday)
        TUESDAY -> s(R.string.tuesday)
        WEDNESDAY -> s(R.string.wednesday)
        THURSDAY -> s(R.string.thursday)
        FRIDAY -> s(R.string.friday)
        SATURDAY -> s(R.string.saturday)
        SUNDAY -> s(R.string.sunday)
        else -> SimpleDateFormat("d LLL", getCurrentLocale(c)).format(this)
    }
}

fun Date.formatAsFull(context: Context, abbreviated: Boolean = false): String {
    val month = if (abbreviated) "LLL" else "LLLL"

    return SimpleDateFormat("d $month yyyy", getCurrentLocale(context))
        .format(this)
}

fun Date.formatAsListItem(context: Context): String {
    val currentLocale = getCurrentLocale(context)

    return when {
        isToday() -> formatAsTime()
        isYesterday() -> formatAsYesterday(context)
        isThisWeek() -> formatAsWeekDay(context)
        isThisYear() -> {
            SimpleDateFormat("d LLL", currentLocale).format(this)
        }
        else -> {
            formatAsFull(context, abbreviated = true)
        }
    }
}

fun Date.formatAsHeader(context: Context): String {
    return when {
        isToday() -> context.getString(R.string.today)
        isYesterday() -> formatAsYesterday(context)
        isThisWeek() -> formatAsWeekDay(context)
        isThisYear() -> {
            SimpleDateFormat("d LLLL", getCurrentLocale(context))
                .format(this)
        }
        else -> {
            formatAsFull(context, abbreviated = false)
        }
    }
}
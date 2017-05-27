package by.vshkl.android.piktures.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    fun getReadableDate(date: Date?): String
            = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(date)

    fun getReadableDayAndTime(date: Date?): String
            = SimpleDateFormat("EEEE hh:mm", Locale.getDefault()).format(date)
}
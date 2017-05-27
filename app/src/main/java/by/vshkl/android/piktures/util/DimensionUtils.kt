package by.vshkl.android.piktures.util

import com.drew.lang.Rational
import java.util.*

object DimensionUtils {

    fun getReadableMpix(width: Int, height: Int): String
            = String.format(Locale.getDefault(), "%.1f%s", width.toFloat() * height / 1000000, "MP")

    fun getReadableFileSize(bytes: Int): String {
        val unit = 1024.0
        if (bytes < unit) return bytes.toString() + " B"
        val exp = (Math.log(bytes.toDouble()) / Math.log(unit)).toInt()
        val pre = "KMGTPE"[exp - 1]
        return String.format(Locale.getDefault(), "%.1f %sB", bytes / Math.pow(unit, exp.toDouble()), pre)
    }

    fun getReadableExposure(exposure: Rational): String
            = String.format(Locale.getDefault(), "1/%d", (exposure.denominator / exposure.numerator))
}
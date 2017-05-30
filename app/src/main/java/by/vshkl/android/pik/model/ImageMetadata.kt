package by.vshkl.android.pik.model

import com.drew.lang.GeoLocation
import com.drew.lang.Rational
import java.util.*

data class ImageMetadata(
        var fileName: String? = null,
        var fileSize: Int? = 0,
        var fileDateModified: Date? = null,
        var imageWidth: Int? = 0,
        var imageHeight: Int? = 0,
        var exifMake: String? = null,
        var exifModel: String? = null,
        var exitExposure: Rational? = null,
        var exifFNumber: String? = null,
        var exifFocalLength: String? = null,
        var exifIso: String? = null,
        var gpsLocation: GeoLocation? = null
) {
    fun hasFileInfo(): Boolean = fileName != null || fileSize!! > 0 || fileDateModified != null

    fun hasImageInfo(): Boolean = imageWidth!! > 0 && imageHeight!! > 0

    fun hasExifInfo(): Boolean = (exifMake != null || exifModel != null) && (exitExposure != null
            || exifFNumber != null || exifFocalLength != null || exifIso != null)

    fun hasGpsInfo(): Boolean = gpsLocation != null && !gpsLocation?.isZero!!
}
package by.vshkl.android.pik.util

import android.graphics.BitmapFactory
import by.vshkl.android.pik.model.ImageMetadata
import com.drew.imaging.ImageMetadataReader
import com.drew.metadata.exif.*
import com.drew.metadata.file.FileMetadataDirectory
import io.reactivex.Observable
import java.io.File
import java.util.*

object MetadateUtils {

    fun getImageMetadata(filePath: String): Observable<ImageMetadata>
            = Observable.create({ emitter -> emitter.onNext(readExifInfo(filePath)) })

    private fun readExifInfo(filePath: String): ImageMetadata {
        val metadata = ImageMetadataReader.readMetadata(File(filePath))
        val imageMetadata = ImageMetadata()

        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(filePath, options)
        imageMetadata.imageWidth = options.outWidth
        imageMetadata.imageHeight = options.outHeight

        if (metadata.containsDirectoryOfType(FileMetadataDirectory::class.java)) {
            val fDir = metadata.getFirstDirectoryOfType(FileMetadataDirectory::class.java)
            imageMetadata.fileName = fDir.getString(FileMetadataDirectory.TAG_FILE_NAME)
            imageMetadata.fileSize = fDir.getInteger(FileMetadataDirectory.TAG_FILE_SIZE)
            imageMetadata.fileDateModified = fDir.getDate(FileMetadataDirectory.TAG_FILE_MODIFIED_DATE, TimeZone.getDefault())
        }

        if (metadata.containsDirectoryOfType(ExifIFD0Directory::class.java)) {
            val eDir = metadata.getFirstDirectoryOfType(ExifIFD0Directory::class.java)
            imageMetadata.exifMake = eDir.getString(ExifDirectoryBase.TAG_MAKE)
            imageMetadata.exifModel = eDir.getString(ExifDirectoryBase.TAG_MODEL)
        }

        if (metadata.containsDirectoryOfType(ExifSubIFDDirectory::class.java)) {
            val esDir = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory::class.java)
            val esDescriptor = ExifSubIFDDescriptor(esDir)
            imageMetadata.exitExposure = esDir.getRational(ExifDirectoryBase.TAG_EXPOSURE_TIME)
            imageMetadata.exifFNumber = esDescriptor.fNumberDescription
            imageMetadata.exifFocalLength = esDescriptor.focalLengthDescription
            imageMetadata.exifIso = esDescriptor.isoEquivalentDescription
        }

        if (metadata.containsDirectoryOfType(GpsDirectory::class.java)) {
            val gDir = metadata.getFirstDirectoryOfType(GpsDirectory::class.java)
            imageMetadata.gpsLocation = gDir.geoLocation
        }

        return imageMetadata
    }
}
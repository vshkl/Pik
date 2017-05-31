package by.vshkl.android.pik.ui.imageinfo

import android.content.Context
import android.location.Geocoder
import by.vshkl.android.pik.BasePresenter
import by.vshkl.android.pik.R
import by.vshkl.android.pik.model.ImageInfo
import by.vshkl.android.pik.model.ImageMetadata
import by.vshkl.android.pik.util.DateUtils
import by.vshkl.android.pik.util.DimensionUtils
import by.vshkl.android.pik.util.MetadataUtils
import by.vshkl.android.pik.util.RxUtils
import com.arellomobile.mvp.InjectViewState
import io.reactivex.Observable
import java.util.*

@InjectViewState
class ImageInfoPresenter : BasePresenter<ImageInfoView>() {

    fun getImageInfo(context: Context, imagePath: String) {
        setDisposable(MetadataUtils.getImageMetadata(imagePath)
                .compose(RxUtils.applySchedulers())
                .flatMap { createImageInfoItems(context, it) }
                .subscribe({ viewState.showImageInfo(it) }))
    }

    private fun createImageInfoItems(context: Context, imageMetadata: ImageMetadata): Observable<List<ImageInfo>> {
        return Observable.create { emitter ->
            val imageInfos: MutableList<ImageInfo> = ArrayList()

            if (imageMetadata.hasFileInfo()) {
                imageInfos.add(ImageInfo(
                        R.drawable.ic_calendar,
                        DateUtils.getReadableDate(imageMetadata.fileDateModified),
                        DateUtils.getReadableDayAndTime(imageMetadata.fileDateModified)
                ))
            }

            if (imageMetadata.hasFileInfo() && imageMetadata.hasImageInfo()) {
                imageInfos.add(ImageInfo(
                        R.drawable.ic_image,
                        imageMetadata.fileName!!,
                        String.format(Locale.getDefault(), "%S   %d x %d   %s",
                                DimensionUtils.getReadableMpix(imageMetadata.imageWidth!!, imageMetadata.imageHeight!!),
                                imageMetadata.imageWidth,
                                imageMetadata.imageHeight,
                                DimensionUtils.getReadableFileSize(imageMetadata.fileSize!!))
                ))
            }

            if (imageMetadata.hasExifInfo()) {
                imageInfos.add(ImageInfo(
                        R.drawable.ic_camera,
                        String.format(Locale.getDefault(), "%s %s",
                                imageMetadata.exifMake!!, imageMetadata.exifModel),
                        String.format(Locale.getDefault(), "%s   %s   %s   ISO%s",
                                imageMetadata.exifFNumber,
                                DimensionUtils.getReadableExposure(imageMetadata.exitExposure!!),
                                imageMetadata.exifFocalLength,
                                imageMetadata.exifIso)
                ))
            }

            if (imageMetadata.hasGpsInfo()) {
                val geocoder = Geocoder(context, Locale.getDefault())
                val address = geocoder.getFromLocation(imageMetadata.gpsLocation?.latitude!!,
                        imageMetadata.gpsLocation?.longitude!!, 1)[0]

                imageInfos.add(ImageInfo(
                        R.drawable.ic_location,
                        String.format(Locale.getDefault(), "%s, %s",
                                address.getAddressLine(1),
                                address.getAddressLine(2)),
                        imageMetadata.gpsLocation?.toDMSString()!!,
                        String.format(Locale.getDefault(), "geo:%f,%f",
                                address.latitude,
                                address.longitude)
                ))
            }

            emitter.onNext(imageInfos)
        }
    }
}
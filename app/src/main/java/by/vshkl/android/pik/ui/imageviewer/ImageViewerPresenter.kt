package by.vshkl.android.pik.ui.imageviewer

import android.content.Context
import android.net.Uri
import android.support.v4.app.Fragment
import by.vshkl.android.pik.BasePresenter
import by.vshkl.android.pik.local.Storage
import by.vshkl.android.pik.model.Image
import by.vshkl.android.pik.util.RxUtils
import com.arellomobile.mvp.InjectViewState
import java.lang.ref.WeakReference

@InjectViewState
class ImageViewerPresenter : BasePresenter<ImageViewerView>() {

    fun showImagePager(images: List<Image>?, startPosition: Int, shouldReplace: Boolean) {
        viewState.showImagePager(images, startPosition, false, shouldReplace)
    }

    fun showImageInfo(imagePath: String?) {
        viewState.showImageInfo(imagePath)
    }

    fun shareImages(imagePaths: List<String>?) {
        viewState.shareImages(imagePaths)
    }

    fun editImage(fragment: Fragment, requestCode: Int, imagePath: String?) {
        viewState.editImage(fragment, requestCode, imagePath)
    }

    fun openMap(locationUri: Uri) {
        viewState.openMap(locationUri)
    }

    fun useImageAs(image: Image?) {
        viewState.useImageAs(image)
    }

    fun getImages(context: Context, imagePath: String) {
        Storage.getImages(WeakReference(context), imagePath)
                .compose(RxUtils.applySchedulers())
                .subscribe({
                    viewState.showImagePager(it, it.indexOf(it.find { it.image == imagePath }), false, false)
                })
    }
}
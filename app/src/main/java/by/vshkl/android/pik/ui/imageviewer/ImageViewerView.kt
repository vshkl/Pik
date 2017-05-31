package by.vshkl.android.pik.ui.imageviewer

import android.net.Uri
import android.support.v4.app.Fragment
import by.vshkl.android.pik.model.Image
import com.arellomobile.mvp.MvpView

interface ImageViewerView : MvpView {

    fun showImagePager(images: List<Image>?, startPosition: Int, addToBackStack: Boolean, shouldReplace: Boolean)

    fun showImageInfo(imagePath: String?)

    fun shareImages(imagePaths: List<String>?)

    fun editImage(fragment: Fragment, requestCode: Int, imagePath: String?)

    fun openMap(locationUri: Uri)

    fun useImageAs(image: Image?)
}
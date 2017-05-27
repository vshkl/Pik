package by.vshkl.android.piktures.ui.main

import android.net.Uri
import by.vshkl.android.piktures.model.Album
import by.vshkl.android.piktures.model.Image
import com.arellomobile.mvp.MvpView

interface MainView : MvpView {

    fun checkStoragePermission()

    fun showAlbums()

    fun showAlbum(album: Album?)

    fun showImagePager(images: List<Image>?, startPosition: Int)

    fun showImageInfo(imagePath: String?)

    fun shareImages(imagePaths: List<String>?)

    fun openMap(locationUri: Uri)
}
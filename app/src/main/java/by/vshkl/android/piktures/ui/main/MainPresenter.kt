package by.vshkl.android.piktures.ui.main

import android.net.Uri
import by.vshkl.android.piktures.BasePresenter
import by.vshkl.android.piktures.model.Album
import by.vshkl.android.piktures.model.Image
import com.arellomobile.mvp.InjectViewState

@InjectViewState
class MainPresenter : BasePresenter<MainView>() {

    fun checkStoragePermission() {
        viewState.checkStoragePermission()
    }

    fun showAlbums() {
        viewState.showAlbums()
    }

    fun showAlbum(album: Album?) {
        viewState.showAlbum(album)
    }

    fun showImagePager(images: List<Image>?, startPosition: Int) {
        viewState.showImagePager(images, startPosition)
    }

    fun showImageInfo(imagePath: String?) {
        viewState.showImageInfo(imagePath)
    }

    fun shareImages(imagePaths: List<String>?) {
        viewState.shareImages(imagePaths)
    }

    fun openMap(locationUri: Uri) {
        viewState.openMap(locationUri)
    }
}
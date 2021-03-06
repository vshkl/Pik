package by.vshkl.android.pik.ui.main

import android.view.View
import by.vshkl.android.pik.model.Album
import by.vshkl.android.pik.model.Image
import com.arellomobile.mvp.MvpView

interface MainView : MvpView {

    fun checkStoragePermission()

    fun showAlbums()

    fun showAlbum(album: Album?, startSharedView: View?)

    fun showImagePager(images: List<Image>?, startPosition: Int, addToBackStack: Boolean, shouldReplace: Boolean)

    fun showImageInfo(imagePath: String?)

    fun shareImages(imagePaths: List<String>?)
}
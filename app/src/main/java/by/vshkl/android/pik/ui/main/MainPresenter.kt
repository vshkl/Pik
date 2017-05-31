package by.vshkl.android.pik.ui.main

import android.content.Context
import android.view.View
import by.vshkl.android.pik.BasePresenter
import by.vshkl.android.pik.local.Repository
import by.vshkl.android.pik.model.Album
import by.vshkl.android.pik.model.Image
import by.vshkl.android.pik.util.RxUtils
import com.arellomobile.mvp.InjectViewState
import java.lang.ref.WeakReference

@InjectViewState
class MainPresenter : BasePresenter<MainView>() {

    fun checkStoragePermission() {
        viewState.checkStoragePermission()
    }

    fun showAlbums() {
        viewState.showAlbums()
    }

    fun showAlbum(album: Album?, startSharedView: View?) {
        viewState.showAlbum(album, startSharedView)
    }

    fun showImagePager(images: List<Image>?, startPosition: Int, shouldReplace: Boolean) {
        viewState.showImagePager(images, startPosition, true, shouldReplace)
    }

    fun showImageInfo(imagePath: String?) {
        viewState.showImageInfo(imagePath)
    }

    fun shareImages(imagePaths: List<String>?) {
        viewState.shareImages(imagePaths)
    }

    fun getImages(context: Context, imagePath: String) {
        Repository.getImages(WeakReference(context), imagePath)
                .compose(RxUtils.applySchedulers())
                .subscribe({
                    viewState.showImagePager(it, it.indexOf(it.find { it.image == imagePath }), false, false)
                })
    }
}
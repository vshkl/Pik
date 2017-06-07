package by.vshkl.android.pik.ui.album

import android.content.Context
import by.vshkl.android.pik.BasePresenter
import by.vshkl.android.pik.local.Storage
import by.vshkl.android.pik.model.Album
import by.vshkl.android.pik.util.RxUtils
import com.arellomobile.mvp.InjectViewState
import java.lang.ref.WeakReference

@InjectViewState
class AlbumPresenter : BasePresenter<AlbumView>() {

    fun getAlbum(context: Context, album: Album?) {
        setDisposable(Storage.getImages(WeakReference(context), album)
                .compose(RxUtils.applySchedulers())
                .subscribe({
                    viewState.hideLoading()
                    viewState.showAlbum(it.toMutableList())
                }))
    }

    fun deleteImage(context: Context, imagePaths: List<String>?, deletedIndexes: Array<Int>?) {
        setDisposable(Storage.deleteImages(WeakReference(context), imagePaths)
                .compose(RxUtils.applySchedulers())
                .subscribe({
                    if (it == imagePaths?.size) {
                        viewState.imagesDeleted(deletedIndexes)
                    }
                }))
    }
}
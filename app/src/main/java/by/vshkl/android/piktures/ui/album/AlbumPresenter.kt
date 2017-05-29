package by.vshkl.android.piktures.ui.album

import android.content.Context
import by.vshkl.android.piktures.BasePresenter
import by.vshkl.android.piktures.local.Repository
import by.vshkl.android.piktures.util.RxUtils
import com.arellomobile.mvp.InjectViewState
import java.lang.ref.WeakReference

@InjectViewState
class AlbumPresenter : BasePresenter<AlbumView>() {

    fun getAlbum(context: Context, albumId: String?) {
        setDisposable(Repository.getImages(WeakReference(context), albumId!!)
                .compose(RxUtils.applySchedulers())
                .subscribe({
                    viewState.hideLoading()
                    viewState.showAlbum(it)
                }))
    }

    fun deleteImage(context: Context, imagePaths: List<String>?, deletedIndexes: Array<Int>?) {
        setDisposable(Repository.deleteImages(WeakReference(context), imagePaths)
                .compose(RxUtils.applySchedulers())
                .subscribe({
                    if (it == imagePaths?.size) {
                        viewState.imagesDeleted(deletedIndexes)
                    }
                }))
    }
}
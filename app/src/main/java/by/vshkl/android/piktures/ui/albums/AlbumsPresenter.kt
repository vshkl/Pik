package by.vshkl.android.piktures.ui.albums

import android.content.Context
import by.vshkl.android.piktures.BasePresenter
import by.vshkl.android.piktures.local.Repository
import by.vshkl.android.piktures.util.RxUtils
import com.arellomobile.mvp.InjectViewState
import java.lang.ref.WeakReference

@InjectViewState
class AlbumsPresenter : BasePresenter<AlbumsView>() {

    fun getAlbums(context: Context) {
        setDisposable(Repository.getAlbums(WeakReference(context))
                .compose(RxUtils.applySchedulers())
                .subscribe({
                    viewState.hideLoading()
                    viewState.showAlbums(it)
                }))
    }

    fun deleteAlbums(context: Context, albumIds: List<String>?, deletedIndexes: Array<Int>?) {
        setDisposable(Repository.deleteAlbum(WeakReference(context), albumIds)
                .compose(RxUtils.applySchedulers())
                .subscribe({
                    if (it == albumIds?.size) {
                        viewState.albumsDeleted(deletedIndexes)
                    }
                }))
    }
}
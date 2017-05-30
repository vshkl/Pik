package by.vshkl.android.piktures.ui.albums

import android.content.Context
import by.vshkl.android.piktures.BasePresenter
import by.vshkl.android.piktures.local.Repository
import by.vshkl.android.piktures.model.Album
import by.vshkl.android.piktures.util.RxUtils
import com.arellomobile.mvp.InjectViewState
import java.lang.ref.WeakReference

@InjectViewState
class AlbumsPresenter : BasePresenter<AlbumsView>() {

    fun showRenameDialog(album: Album?) {
        viewState.showRenameDialog(album)
    }

    fun getAlbums(context: Context) {
        setDisposable(Repository.getAlbums(WeakReference(context))
                .compose(RxUtils.applySchedulers())
                .subscribe({
                    viewState.hideLoading()
                    viewState.showAlbums(it)
                }))
    }

    fun deleteAlbums(context: Context, albums: List<Album>?, deletedIndexes: Array<Int>?) {
        setDisposable(Repository.deleteAlbums(WeakReference(context), albums)
                .compose(RxUtils.applySchedulers())
                .subscribe({
                    if (it == albums?.size) {
                        viewState.onAlbumsDeleted(deletedIndexes)
                    }
                }))
    }

    fun renameAlbum(context: Context, album: Album?, newName: String, albumPosition: Int) {
        Repository.renameAlbum(WeakReference(context), album, newName)
                .compose(RxUtils.applySchedulers())
                .subscribe({ albumId ->
                    viewState.takeIf { albumId != null }?.onAlbumRenamed(albumId, newName, albumPosition)
                })
    }
}
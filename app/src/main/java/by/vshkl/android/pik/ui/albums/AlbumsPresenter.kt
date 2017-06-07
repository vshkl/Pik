package by.vshkl.android.pik.ui.albums

import android.content.Context
import by.vshkl.android.pik.BasePresenter
import by.vshkl.android.pik.local.Cache
import by.vshkl.android.pik.local.Storage
import by.vshkl.android.pik.model.Album
import by.vshkl.android.pik.util.RxUtils
import com.arellomobile.mvp.InjectViewState
import java.lang.ref.WeakReference

@InjectViewState
class AlbumsPresenter : BasePresenter<AlbumsView>() {

    fun showRenameDialog(album: Album?) {
        viewState.showRenameDialog(album)
    }

    fun getAlbums(context: Context) {
        getAlbumsFromCache(context)
    }

    fun deleteAlbums(context: Context, albums: List<Album>?, deletedIndexes: Array<Int>?) {
        setDisposable(Storage.deleteAlbums(WeakReference(context), albums)
                .compose(RxUtils.applySchedulers())
                .subscribe({
                    if (it == albums?.size) {
                        viewState.onAlbumsDeleted(deletedIndexes)
                    }
                }))
    }

    fun renameAlbum(context: Context, album: Album?, newName: String, albumPosition: Int) {
        Storage.renameAlbum(WeakReference(context), album, newName)
                .compose(RxUtils.applySchedulers())
                .subscribe({ albumId ->
                    viewState.takeIf { albumId != null }?.onAlbumRenamed(albumId, newName, albumPosition)
                })
    }

    private fun getAlbumsFromCache(context: Context) {
        setDisposable(Cache.getAlbums()
                .compose(RxUtils.applySchedulers())
                .subscribe({
                    getAlbumsFromStorage(context)
                    viewState.showAlbums(it.toMutableList())
                }))
    }

    private fun getAlbumsFromStorage(context: Context) {
        setDisposable(Storage.getAlbums(WeakReference(context))
                .compose(RxUtils.applySchedulers())
                .subscribe({
                    viewState.hideLoading()
                    viewState.showAlbums(it.toMutableList())
                    putAlbumsToCache(it)
                }))
    }

    private fun putAlbumsToCache(albums: List<Album>) {
        setDisposable(Cache.putAlbums(albums)
                .compose(RxUtils.applySchedulers())
                .subscribe())
    }
}
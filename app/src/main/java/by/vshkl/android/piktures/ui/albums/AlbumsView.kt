package by.vshkl.android.piktures.ui.albums

import by.vshkl.android.piktures.model.Album
import com.arellomobile.mvp.MvpView

interface AlbumsView : MvpView {

    fun showLoading()

    fun hideLoading()

    fun showAlbums(albums: MutableList<Album>)

    fun albumsDeleted(deletedIndexes: Array<Int>?)
}

package by.vshkl.android.piktures.ui.albums

import by.vshkl.android.piktures.model.Album
import com.arellomobile.mvp.MvpView

interface AlbumView : MvpView {

    fun showLoading()

    fun hideLoading()

    fun showAlbums(albums: List<Album>)
}

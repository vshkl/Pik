package by.vshkl.android.piktures.ui.albums

import by.vshkl.android.piktures.model.Album

interface AlbumsListener {

    fun onAlbumClicked(album: Album?)

    fun onAlbumSelectionClicked(index: Int)

    fun onAlbumSelectionLongClicked(index: Int)
}
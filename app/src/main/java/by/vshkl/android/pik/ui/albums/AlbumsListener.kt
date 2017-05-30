package by.vshkl.android.pik.ui.albums

import by.vshkl.android.pik.model.Album

interface AlbumsListener {

    fun onAlbumClicked(album: Album?)

    fun onAlbumSelectionClicked(index: Int)

    fun onAlbumSelectionLongClicked(index: Int)
}
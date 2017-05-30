package by.vshkl.android.pik.ui.albums

import by.vshkl.android.pik.model.Album

interface AlbumsRenameListener {

    fun onAlbumRenamed(album: Album?, newName: String, albumPosition: Int)
}
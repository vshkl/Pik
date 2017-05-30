package by.vshkl.android.piktures.ui.albums

import by.vshkl.android.piktures.model.Album

interface AlbumsRenameListener {

    fun onAlbumRenamed(album: Album?, newName: String, albumPosition: Int)
}
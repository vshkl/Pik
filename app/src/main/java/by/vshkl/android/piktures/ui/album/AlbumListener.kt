package by.vshkl.android.piktures.ui.album

import by.vshkl.android.piktures.model.Image

interface AlbumListener {

    fun onImageClicked(images: List<Image>?, startPosition: Int)

    fun onImageSelectionClicked(index: Int)

    fun onImageSelectionLongClicked(index: Int)
}
package by.vshkl.android.pik.ui.album

import by.vshkl.android.pik.model.Image

interface AlbumListener {

    fun onImageClicked(images: List<Image>?, startPosition: Int)

    fun onImageSelectionClicked(index: Int)

    fun onImageSelectionLongClicked(index: Int)
}
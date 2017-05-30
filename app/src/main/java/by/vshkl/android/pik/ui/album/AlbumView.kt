package by.vshkl.android.pik.ui.album

import by.vshkl.android.pik.model.Image
import com.arellomobile.mvp.MvpView

interface AlbumView : MvpView {

    fun showLoading()

    fun hideLoading()

    fun showAlbum(images: MutableList<Image>)

    fun imagesDeleted(deletedIndexes: Array<Int>?)
}
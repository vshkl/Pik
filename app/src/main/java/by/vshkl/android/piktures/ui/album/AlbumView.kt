package by.vshkl.android.piktures.ui.album

import by.vshkl.android.piktures.model.Image
import com.arellomobile.mvp.MvpView

interface AlbumView : MvpView {

    fun showLoading()

    fun hideLoading()

    fun showAlbum(images: MutableList<Image>)

    fun shareImages(imagePaths: Array<String>)
}
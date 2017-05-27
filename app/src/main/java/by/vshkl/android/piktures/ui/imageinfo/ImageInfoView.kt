package by.vshkl.android.piktures.ui.imageinfo

import by.vshkl.android.piktures.model.ImageInfo
import com.arellomobile.mvp.MvpView

interface ImageInfoView : MvpView {

    fun showImageInfo(imageInfos: List<ImageInfo>)
}
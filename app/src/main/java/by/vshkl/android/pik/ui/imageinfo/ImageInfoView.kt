package by.vshkl.android.pik.ui.imageinfo

import by.vshkl.android.pik.model.ImageInfo
import com.arellomobile.mvp.MvpView

interface ImageInfoView : MvpView {

    fun showImageInfo(imageInfos: List<ImageInfo>)
}
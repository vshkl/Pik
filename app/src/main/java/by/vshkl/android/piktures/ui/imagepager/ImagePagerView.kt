package by.vshkl.android.piktures.ui.imagepager

import com.arellomobile.mvp.MvpView

interface ImagePagerView : MvpView {

    fun imageDeleted(deletedPosition: Int)
}
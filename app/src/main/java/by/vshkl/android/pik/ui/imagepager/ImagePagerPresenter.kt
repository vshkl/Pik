package by.vshkl.android.pik.ui.imagepager

import android.content.Context
import by.vshkl.android.pik.BasePresenter
import by.vshkl.android.pik.local.Storage
import by.vshkl.android.pik.util.RxUtils
import com.arellomobile.mvp.InjectViewState
import java.lang.ref.WeakReference

@InjectViewState
class ImagePagerPresenter : BasePresenter<ImagePagerView>() {

    fun deleteImage(context: Context, imagePath: String?, deletedPosition: Int) {
        setDisposable(Storage.deleteImages(WeakReference(context), listOf(imagePath!!))
                .compose(RxUtils.applySchedulers())
                .subscribe({
                    if (it == 1) {
                        viewState.imageDeleted(deletedPosition)
                    }
                }))
    }
}
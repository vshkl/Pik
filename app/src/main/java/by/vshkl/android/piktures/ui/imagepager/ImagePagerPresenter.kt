package by.vshkl.android.piktures.ui.imagepager

import android.content.Context
import by.vshkl.android.piktures.BasePresenter
import by.vshkl.android.piktures.local.Repository
import by.vshkl.android.piktures.util.RxUtils
import com.arellomobile.mvp.InjectViewState
import java.lang.ref.WeakReference

@InjectViewState
class ImagePagerPresenter : BasePresenter<ImagePagerView>() {

    fun deleteImage(context: Context, imagePath: String?, deletedPosition: Int) {
        setDisposable(Repository.deleteImages(WeakReference(context), listOf(imagePath!!))
                .compose(RxUtils.applySchedulers())
                .subscribe({
                    if (it == 1) {
                        viewState.imageDeleted(deletedPosition)
                    }
                }))
    }
}
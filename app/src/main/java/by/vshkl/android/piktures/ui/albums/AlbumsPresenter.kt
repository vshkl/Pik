package by.vshkl.android.piktures.ui.albums

import android.content.Context
import by.vshkl.android.piktures.local.Repository
import by.vshkl.android.piktures.ui.BasePresenter
import by.vshkl.android.piktures.util.RxUtils
import com.arellomobile.mvp.InjectViewState
import java.lang.ref.WeakReference

@InjectViewState
class AlbumsPresenter : BasePresenter<AlbumView>() {

    val repository: Repository = Repository()

    fun getAlbums(context: Context) {
        repository.getAlbums(WeakReference(context))
                .compose(RxUtils.applySchedulers())
                .subscribe({
                    viewState.hideLoading()
                    viewState.showAlbums(it)
                })
    }
}
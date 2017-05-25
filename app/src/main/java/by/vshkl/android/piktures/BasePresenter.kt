package by.vshkl.android.piktures

import io.reactivex.disposables.Disposable
import com.arellomobile.mvp.MvpPresenter
import com.arellomobile.mvp.MvpView

open class BasePresenter<View : MvpView> : MvpPresenter<View>() {

    private var disposable: Disposable? = null

    override fun onDestroy() {
        if (disposable != null && !disposable!!.isDisposed) {
            disposable!!.dispose()
        }
    }

    protected fun setDisposable(disposable: Disposable) {
        this.disposable = disposable
    }
}
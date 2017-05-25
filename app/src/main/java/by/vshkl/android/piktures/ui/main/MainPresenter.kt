package by.vshkl.android.piktures.ui.main

import by.vshkl.android.piktures.ui.BasePresenter
import com.arellomobile.mvp.InjectViewState

@InjectViewState
class MainPresenter : BasePresenter<MainView>() {

    fun checkStoragePermission() {
        viewState.checkStoragePermission()
    }

    fun showAlbums() {
        viewState.showAlbums()
    }
}
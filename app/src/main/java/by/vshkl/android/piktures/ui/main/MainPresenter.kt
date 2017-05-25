package by.vshkl.android.piktures.ui.main

import by.vshkl.android.piktures.BasePresenter
import by.vshkl.android.piktures.model.Album
import com.arellomobile.mvp.InjectViewState

@InjectViewState
class MainPresenter : BasePresenter<MainView>() {

    fun checkStoragePermission() {
        viewState.checkStoragePermission()
    }

    fun showAlbums() {
        viewState.showAlbums()
    }

    fun showAlbum(album: Album?) {
        viewState.showAlbum(album)
    }
}
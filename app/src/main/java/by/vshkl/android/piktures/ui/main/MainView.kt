package by.vshkl.android.piktures.ui.main

import by.vshkl.android.piktures.model.Album
import com.arellomobile.mvp.MvpView

interface MainView : MvpView {

    fun checkStoragePermission()

    fun showAlbums()

    fun showAlbum(album: Album?)
}
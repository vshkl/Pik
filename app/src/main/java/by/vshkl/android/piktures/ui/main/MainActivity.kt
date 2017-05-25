package by.vshkl.android.piktures.ui.main

import android.Manifest
import android.media.MediaScannerConnection
import android.media.MediaScannerConnection.OnScanCompletedListener
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import by.vshkl.android.piktures.R
import by.vshkl.android.piktures.model.Album
import by.vshkl.android.piktures.util.Navigation
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.tbruyelle.rxpermissions2.RxPermissions

class MainActivity : MvpAppCompatActivity(),
        MainView, OnScanCompletedListener {

    @InjectPresenter lateinit var mainPresenter: MainPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainPresenter.checkStoragePermission()
    }

    //---[ Listeners ]--------------------------------------------------------------------------------------------------

    override fun onScanCompleted(path: String?, uri: Uri?) = mainPresenter.showAlbums()

    //---[ View implementation ]----------------------------------------------------------------------------------------

    override fun showAlbums() = Navigation.navigateToAlbums(this)

    override fun showAlbum(album: Album?) = Navigation.navigateToAlbum(this, album)

    //---[ Other ]------------------------------------------------------------------------------------------------------

    override fun checkStoragePermission() {
        val rxPermissions: RxPermissions = RxPermissions(this)
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe { granted ->
                    when {
                        granted -> scanMedia()
                        else -> {
                            //TODO: Show dialog with grant permission prompt
                        }
                    }
                }
    }

    private fun scanMedia() = MediaScannerConnection
            .scanFile(this, arrayOf(Environment.getExternalStorageDirectory().toString()), null, this)
}

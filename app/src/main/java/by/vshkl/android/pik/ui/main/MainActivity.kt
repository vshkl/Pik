package by.vshkl.android.pik.ui.main

import android.Manifest
import android.content.Intent
import android.media.MediaScannerConnection
import android.media.MediaScannerConnection.OnScanCompletedListener
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.view.View
import by.vshkl.android.pik.R
import by.vshkl.android.pik.model.Album
import by.vshkl.android.pik.model.Image
import by.vshkl.android.pik.util.Navigation
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.tbruyelle.rxpermissions2.RxPermissions

class MainActivity : MvpAppCompatActivity(),
        MainView, OnScanCompletedListener {

    @InjectPresenter lateinit var mainPresenter: MainPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intent = intent
        when {
            intent?.action == Intent.ACTION_VIEW && intent.type.startsWith("image/") ->
                mainPresenter.getImages(this, intent.data.path ?: "")
            else -> mainPresenter.checkStoragePermission()
        }
    }

    //---[ Listeners ]--------------------------------------------------------------------------------------------------

    override fun onScanCompleted(path: String?, uri: Uri?) = mainPresenter.showAlbums()

    //---[ View implementation ]----------------------------------------------------------------------------------------

    override fun showAlbums() = Navigation.navigateToAlbums(this)

    override fun showAlbum(album: Album?, startSharedView: View?)
            = Navigation.navigateToAlbum(this, album, startSharedView)

    override fun showImagePager(images: List<Image>?, startPosition: Int, addToBackStack: Boolean, shouldReplace: Boolean)
            = Navigation.navigateToImagePager(this, images, startPosition, addToBackStack, shouldReplace)

    override fun showImageInfo(imagePath: String?) = Navigation.showImageInfoDialog(this, imagePath)

    override fun shareImages(imagePaths: List<String>?) = Navigation.shareImages(this, imagePaths)

    override fun editImage(fragment: Fragment, requestCode: Int, imagePath: String?) {
        Navigation.editImage(this, fragment, requestCode, imagePath)
    }

    override fun openMap(locationUri: Uri) = Navigation.openInMap(this, locationUri)

    override fun useImageAs(image: Image?) = Navigation.useImageAs(this, image)

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

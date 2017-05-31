package by.vshkl.android.pik.ui.imageviewer

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import by.vshkl.android.pik.R
import by.vshkl.android.pik.model.Image
import by.vshkl.android.pik.util.Navigation
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter

class ImageViewerActivity : MvpAppCompatActivity(), ImageViewerView {

    @InjectPresenter lateinit var imageViewerPresenter: ImageViewerPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_pager)

        if (intent.hasExtra(KEY_IMAGE_LIST) && intent.hasExtra(KEY_START_POSITION)) {
            val images = intent?.getParcelableArrayListExtra<Image>(KEY_IMAGE_LIST)?.toList()
            val startPosition = intent?.getIntExtra(KEY_START_POSITION, 0) ?: 0
            Navigation.navigateToImagePager(this, images, startPosition, false, false)
        }
    }

    //---[ View implementation ]----------------------------------------------------------------------------------------

    override fun showImagePager(images: List<Image>?, startPosition: Int, addToBackStack: Boolean, shouldReplace: Boolean)
            = Navigation.navigateToImagePager(this, images, startPosition, addToBackStack, shouldReplace)

    override fun showImageInfo(imagePath: String?) = Navigation.showImageInfoDialog(this, imagePath)

    override fun shareImages(imagePaths: List<String>?) = Navigation.shareImages(this, imagePaths)

    override fun editImage(fragment: Fragment, requestCode: Int, imagePath: String?)
            = Navigation.editImage(this, fragment, requestCode, imagePath)

    override fun openMap(locationUri: Uri) = Navigation.openInMap(this, locationUri)

    override fun useImageAs(image: Image?) = Navigation.useImageAs(this, image)

    //---[ Other ]------------------------------------------------------------------------------------------------------

    companion object {
        private val KEY_IMAGE_LIST = "ImageViewerActivity.KEY_IMAGE_LIST"
        private val KEY_START_POSITION = "ImageViewerActivity.KEY_START_POSITION"

        fun newIntent(context: Context, images: List<Image>?, startPosition: Int): Intent {
            val intent = Intent(context, ImageViewerActivity::class.java)
            intent.putParcelableArrayListExtra(KEY_IMAGE_LIST, ArrayList(images))
            intent.putExtra(KEY_START_POSITION, startPosition)
            return intent
        }
    }
}
package by.vshkl.android.pik.ui.imageviewer

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.widget.ViewDragHelper.STATE_DRAGGING
import android.support.v4.widget.ViewDragHelper.STATE_SETTLING
import by.vshkl.android.pik.R
import by.vshkl.android.pik.model.Image
import by.vshkl.android.pik.util.Navigation
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.r0adkll.slidr.Slidr
import com.r0adkll.slidr.model.SlidrConfig
import com.r0adkll.slidr.model.SlidrInterface
import com.r0adkll.slidr.model.SlidrListener
import com.r0adkll.slidr.model.SlidrPosition
import kotlinx.android.synthetic.main.fragment_image_pager.*

class ImageViewerActivity : MvpAppCompatActivity(), ImageViewerView, SlidrListener {

    @InjectPresenter lateinit var imageViewerPresenter: ImageViewerPresenter
    private var slidrInterface: SlidrInterface? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_pager)

        slidrInterface = Slidr.attach(this, SlidrConfig.Builder()
                .position(SlidrPosition.VERTICAL)
                .sensitivity(0.1F)
                .scrimStartAlpha(1F)
                .scrimEndAlpha(0.1F)
                .velocityThreshold(0.5F)
                .distanceThreshold(0.25F)
                .listener(this)
                .build())

        val intent = intent
        when {
            intent?.action == Intent.ACTION_VIEW && intent.type.startsWith("image/") ->
                imageViewerPresenter.getImages(this, intent.data.path ?: "")
            intent.hasExtra(KEY_IMAGE_LIST) && intent.hasExtra(KEY_START_POSITION) -> {
                val images = intent?.getParcelableArrayListExtra<Image>(KEY_IMAGE_LIST)?.toList()
                val startPosition = intent?.getIntExtra(KEY_START_POSITION, 0) ?: 0
                Navigation.navigateToImagePager(this, images, startPosition, false, false)
            }
        }
    }

    //---[ Listeners ]--------------------------------------------------------------------------------------------------

    override fun onSlideClosed() {

    }

    override fun onSlideStateChanged(state: Int) {
        when (state) {
            STATE_DRAGGING, STATE_SETTLING -> {
                clRoot.setBackgroundColor(ContextCompat.getColor(baseContext, android.R.color.transparent))
                arrayOf(tbToolbar, ivActionShare, ivActionEdit, ivActionInfo, ivActionDelete).forEach { it.alpha = 0F }
            }
            else -> {
                clRoot.setBackgroundColor(ContextCompat.getColor(baseContext, android.R.color.black))
                arrayOf(tbToolbar, ivActionShare, ivActionEdit, ivActionInfo, ivActionDelete).forEach { it.alpha = 1F }
            }
        }
    }

    override fun onSlideChange(percent: Float) {

    }

    override fun onSlideOpened() {

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
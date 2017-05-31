package by.vshkl.android.pik.ui.imagepager

import android.content.Context
import android.content.Intent
import android.os.Bundle
import by.vshkl.android.pik.R
import by.vshkl.android.pik.model.Image
import by.vshkl.android.pik.util.Navigation
import com.arellomobile.mvp.MvpAppCompatActivity

class ImagePagerActivity : MvpAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_pager)

        if (intent.hasExtra(KEY_IMAGE_LIST) && intent.hasExtra(KEY_START_POSITION)) {
            val images = intent?.getParcelableArrayListExtra<Image>(KEY_IMAGE_LIST)?.toList()
            val startPosition = intent?.getIntExtra(KEY_START_POSITION, 0) ?: 0
            Navigation.navigateToImagePager(this, images, startPosition, false, false)
        }
    }

    companion object {
        private val KEY_IMAGE_LIST = "ImagePagerActivity.KEY_IMAGE_LIST"
        private val KEY_START_POSITION = "ImagePagerActivity.KEY_START_POSITION"

        fun newIntent(context: Context, images: List<Image>?, startPosition: Int): Intent {
            val intent = Intent(context, ImagePagerActivity::class.java)
            intent.putParcelableArrayListExtra(KEY_IMAGE_LIST, ArrayList(images))
            intent.putExtra(KEY_START_POSITION, startPosition)
            return intent
        }
    }
}
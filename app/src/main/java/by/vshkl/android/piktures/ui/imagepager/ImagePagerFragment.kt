package by.vshkl.android.piktures.ui.imagepager

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import by.vshkl.android.piktures.BaseFragment
import by.vshkl.android.piktures.R
import by.vshkl.android.piktures.model.Image
import com.arellomobile.mvp.presenter.InjectPresenter
import kotlinx.android.synthetic.main.fragment_gallery.*

class ImagePagerFragment : BaseFragment(), ImagePagerView {

    @InjectPresenter lateinit var imagePagerPresenter: ImagePagerPresenter
    private var images: List<Image>? = null
    private var startPosition: Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        images = arguments.getParcelableArrayList(KEY_IMAGE_LIST)
        startPosition = arguments.getInt(KEY_START_POSITION, 0)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater?.inflate(R.layout.fragment_image_pager, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getParentActivity()?.setSupportActionBar(tbToolbar)
        getParentActivity()?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        getParentActivity()?.supportActionBar?.setDisplayShowTitleEnabled(false)
        setHasOptionsMenu(true)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
    }

    override fun onDetach() {
        imagePagerPresenter.onDestroy()
        super.onDetach()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                activity?.onBackPressed()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    //---[ Listeners ]--------------------------------------------------------------------------------------------------

    //---[ View implementation ]----------------------------------------------------------------------------------------

    //---[ Other ]------------------------------------------------------------------------------------------------------

    companion object {
        private val KEY_IMAGE_LIST = "ImagePagerFragment.KEY_IMAGE_LIST"
        private val KEY_START_POSITION = "ImagePagerFragment.KEY_START_POSITION"

        fun newInstance(images: ArrayList<Image>?, startPosition: Int): Fragment {
            val args: Bundle = Bundle()
            args.putParcelableArrayList(KEY_IMAGE_LIST, images)
            args.putInt(KEY_START_POSITION, startPosition)
            val fragment: ImagePagerFragment = ImagePagerFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
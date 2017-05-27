package by.vshkl.android.piktures.ui.imagepager

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import by.vshkl.android.piktures.BaseFragment
import by.vshkl.android.piktures.R
import by.vshkl.android.piktures.model.Image
import com.arellomobile.mvp.presenter.InjectPresenter
import kotlinx.android.synthetic.main.fragment_image_pager.*

class ImagePagerFragment : BaseFragment(), ImagePagerView, OnClickListener, ImagePagerListener {

    @InjectPresenter lateinit var imagePagerPresenter: ImagePagerPresenter
    private var startPosition: Int = 0
    private var imagePagerAdapter: ImagePagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startPosition = arguments.getInt(KEY_START_POSITION, 0)
        imagePagerAdapter = ImagePagerAdapter(arguments.getParcelableArrayList(KEY_IMAGE_LIST))
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater?.inflate(R.layout.fragment_image_pager, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getParentActivity()?.setSupportActionBar(tbToolbar)
        getParentActivity()?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        getParentActivity()?.supportActionBar?.setDisplayShowTitleEnabled(false)
        ivActionShare.setOnClickListener(this)
        ivActionEdit.setOnClickListener(this)
        ivActionInfo.setOnClickListener(this)
        ivActionDelete.setOnClickListener(this)
        setHasOptionsMenu(true)
        setupViewPager()
    }

    override fun onResume() {
        super.onResume()
        imagePagerAdapter?.imagePagerListener = this
    }

    override fun onPause() {
        imagePagerAdapter?.imagePagerListener = null
        super.onPause()
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

    override fun onClick(v: View?) {
        when (v) {
            ivActionShare -> getParentActivity()?.mainPresenter
                    ?.shareImages(imagePagerAdapter?.getImagePath(vpPager.currentItem))
            ivActionEdit -> println("Edit")
            ivActionInfo -> getParentActivity()?.mainPresenter
                    ?.showImageInfo(imagePagerAdapter?.getImagePath(vpPager.currentItem)?.get(0))
            ivActionDelete -> println("Delete")
        }
    }

    override fun onImageClicked() {
        println("On image clicked")
    }

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

    private fun setupViewPager() {
        vpPager.adapter = imagePagerAdapter
        vpPager.currentItem = startPosition
    }
}
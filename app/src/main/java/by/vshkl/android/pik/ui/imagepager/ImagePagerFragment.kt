package by.vshkl.android.pik.ui.imagepager

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import android.view.View.*
import android.view.animation.AlphaAnimation
import by.vshkl.android.pik.BasePagerFragment
import by.vshkl.android.pik.R
import by.vshkl.android.pik.model.Image
import com.arellomobile.mvp.presenter.InjectPresenter
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.fragment_image_pager.*

class ImagePagerFragment : BasePagerFragment(), ImagePagerView, OnClickListener, ImagePagerListener,
        OnSystemUiVisibilityChangeListener {

    @InjectPresenter lateinit var imagePagerPresenter: ImagePagerPresenter
    private var currentPosition: Int = 0
    private var imagePagerAdapter: ImagePagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentPosition = arguments.getInt(KEY_START_POSITION, 0)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            restartImageViewPager(imagePagerAdapter?.images, currentPosition, true)
        }
    }

    override fun onResume() {
        super.onResume()
        getParentActivity()?.window?.decorView?.setOnSystemUiVisibilityChangeListener(this)
        imagePagerAdapter?.imagePagerListener = this
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_image_pager, menu)
    }

    override fun onPause() {
        imagePagerAdapter?.imagePagerListener = null
        getParentActivity()?.window?.decorView?.setOnSystemUiVisibilityChangeListener(null)
        super.onPause()
    }

    override fun onDetach() {
        imagePagerPresenter.onDestroy()
        super.onDetach()
    }

    //---[ Listeners ]--------------------------------------------------------------------------------------------------

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                activity?.onBackPressed()
                return true
            }
            R.id.action_use_as -> {
                getParentActivity()?.imageViewerPresenter?.useImageAs(imagePagerAdapter?.images?.get(vpPager.currentItem))
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            ivActionShare -> getParentActivity()?.imageViewerPresenter?.shareImages(
                    imagePagerAdapter?.getImagePath(vpPager.currentItem))
            ivActionEdit -> {
                currentPosition = vpPager.currentItem
                getParentActivity()?.imageViewerPresenter?.editImage(
                        this, UCrop.REQUEST_CROP, imagePagerAdapter?.getImagePath(vpPager.currentItem)?.get(0))
            }
            ivActionInfo -> getParentActivity()?.imageViewerPresenter?.showImageInfo(
                    imagePagerAdapter?.getImagePath(vpPager.currentItem)?.get(0))
            ivActionDelete -> imagePagerPresenter.deleteImage(
                    context, imagePagerAdapter?.getImagePath(vpPager.currentItem)?.get(0), vpPager.currentItem)
        }
    }

    override fun onImageClicked() {
        when (getParentActivity()?.window?.decorView?.systemUiVisibility) {
            SYSTEM_UI_FLAG_VISIBLE, SYSTEM_UI_VISIBLE ->
                getParentActivity()?.window?.decorView?.systemUiVisibility = SYSTEM_UI_HIDDEN
            SYSTEM_UI_HIDDEN ->
                getParentActivity()?.window?.decorView?.systemUiVisibility = SYSTEM_UI_VISIBLE
        }
    }

    override fun onSystemUiVisibilityChange(visibility: Int) {
        when (visibility and SYSTEM_UI_FLAG_FULLSCREEN) {
            0 -> showUi()
            else -> hideUi()
        }
    }

    //---[ View implementation ]----------------------------------------------------------------------------------------

    override fun showUi() {
        val animation = AlphaAnimation(0.0f, 1.0f)
        animation.duration = 500
        animation.fillAfter = true
        arrayOf(tbToolbar, ivActionShare, ivActionEdit, ivActionInfo, ivActionDelete).forEach {
            it.startAnimation(animation)
            it.visibility = View.VISIBLE
        }
    }

    override fun hideUi() {
        val animation = AlphaAnimation(1.0f, 0.0f)
        animation.duration = 500
        animation.fillAfter = true
        arrayOf(tbToolbar, ivActionShare, ivActionEdit, ivActionInfo, ivActionDelete).forEach {
            it.startAnimation(animation)
            it.visibility = View.GONE
        }
    }

    override fun imageDeleted(deletedPosition: Int) {
        restartImageViewPager(imagePagerAdapter?.images?.toMutableList()?.apply { removeAt(deletedPosition) },
                deletedPosition, true)
    }

    //---[ Other ]------------------------------------------------------------------------------------------------------

    companion object {
        private val KEY_IMAGE_LIST = "ImagePagerFragment.KEY_IMAGE_LIST"
        private val KEY_START_POSITION = "ImagePagerFragment.KEY_START_POSITION"

        val SYSTEM_UI_HIDDEN = SYSTEM_UI_FLAG_LAYOUT_STABLE or
                SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                SYSTEM_UI_FLAG_FULLSCREEN or
                SYSTEM_UI_FLAG_IMMERSIVE
        val SYSTEM_UI_VISIBLE = SYSTEM_UI_FLAG_LAYOUT_STABLE or
                SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

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
        vpPager.currentItem = currentPosition
    }

    private fun restartImageViewPager(images: List<Image>?, startPosition: Int, shouldReplace: Boolean) {
        getParentActivity()?.imageViewerPresenter?.showImagePager(images, startPosition, shouldReplace)
    }
}
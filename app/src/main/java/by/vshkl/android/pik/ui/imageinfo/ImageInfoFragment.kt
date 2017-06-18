package by.vshkl.android.pik.ui.imageinfo

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.*
import android.view.ViewGroup
import by.vshkl.android.pik.BaseDialogFragment
import by.vshkl.android.pik.R
import by.vshkl.android.pik.model.ImageInfo
import by.vshkl.android.pik.util.Navigation
import com.arellomobile.mvp.presenter.InjectPresenter

class ImageInfoFragment : BaseDialogFragment(), ImageInfoView, ImageInfoListener {

    @InjectPresenter lateinit var imageInfoPresenter: ImageInfoPresenter
    private var imageInfoAdapter: ImageInfoAdapter? = null
    private var tbToolbar: Toolbar? = null
    private var rvInfo: RecyclerView? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_image_info, container, false)
        tbToolbar = view?.findViewById(R.id.tbToolbar)
        rvInfo = view?.findViewById(R.id.rvInfo)
        initToolbar()
        initRecyclerView()
        return view
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageInfoPresenter.getImageInfo(context, arguments.getString(KEY_IMAGE_PATH, ""))
    }

    override fun onStart() {
        super.onStart()
        dialog?.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onResume() {
        super.onResume()
        imageInfoAdapter?.setImageInfoListener(this)
    }

    override fun onPause() {
        imageInfoAdapter?.setImageInfoListener(null)
        super.onPause()
    }

    override fun onDetach() {
        imageInfoPresenter.onDestroy()
        super.onDetach()
    }

    //---[ Listeners ]--------------------------------------------------------------------------------------------------

    override fun onImageInfoLocationClicked(location: String) {
        Navigation.openInMap(context, Uri.parse(location))
    }

    //---[ View implementation ]----------------------------------------------------------------------------------------

    override fun showImageInfo(imageInfos: List<ImageInfo>) {
        imageInfoAdapter?.setImageInfos(imageInfos)
        imageInfoAdapter?.notifyDataSetChanged()
    }

    //---[ Other ]------------------------------------------------------------------------------------------------------

    companion object {
        private val KEY_IMAGE_PATH = "AlbumFragment.KEY_IMAGE_PATH"

        fun newInstance(imagePath: String?): DialogFragment {
            val args: Bundle = Bundle()
            args.putString(KEY_IMAGE_PATH, imagePath)
            val fragment: ImageInfoFragment = ImageInfoFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private fun initToolbar() {
        tbToolbar?.title = getString(R.string.image_info_title)
        tbToolbar?.setNavigationIcon(R.drawable.ic_arrow_back)
        tbToolbar?.setNavigationOnClickListener { dialog.dismiss() }
    }

    private fun initRecyclerView() {
        rvInfo?.layoutManager = LinearLayoutManager(context)
        imageInfoAdapter = ImageInfoAdapter()
        rvInfo?.adapter = imageInfoAdapter
    }
}
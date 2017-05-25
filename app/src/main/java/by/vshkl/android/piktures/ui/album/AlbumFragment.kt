package by.vshkl.android.piktures.ui.album

import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.support.v7.view.ActionMode
import android.support.v7.view.ActionMode.Callback
import android.support.v7.widget.GridLayoutManager
import android.view.*
import android.widget.Toast
import by.vshkl.android.piktures.BaseFragment
import by.vshkl.android.piktures.R
import by.vshkl.android.piktures.model.Album
import by.vshkl.android.piktures.model.Image
import com.afollestad.dragselectrecyclerview.DragSelectRecyclerViewAdapter.SelectionListener
import com.arellomobile.mvp.presenter.InjectPresenter
import kotlinx.android.synthetic.main.fragment_gallery.*

class AlbumFragment : BaseFragment(), AlbumView, AlbumListener, SelectionListener, Callback {

    @InjectPresenter lateinit var albumPresenter: AlbumPresenter
    private var album: Album? = null
    private var albumAdapter: AlbumAdapter? = null
    private var actionMode: ActionMode? = null
    private var menu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        album = arguments.getParcelable(KEY_ALBUM)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_gallery, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tbToolbar.title = album?.name
        getParentActivity()?.setSupportActionBar(tbToolbar)
        getParentActivity()?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setHasOptionsMenu(true)
        initRecyclerView(savedInstanceState)
        albumPresenter.getAlbum(context, album?.id)
    }

    override fun onResume() {
        super.onResume()
        albumAdapter?.setAlbumListened(this)
        albumAdapter?.setSelectionListener(this)
    }

    override fun onPause() {
        albumAdapter?.setAlbumListened(null)
        albumAdapter?.setSelectionListener(null)
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        albumAdapter?.saveInstanceState(outState)
    }

    override fun onDetach() {
        albumPresenter.onDestroy()
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

    override fun onImageClicked(images: List<Image>?, startPosition: Int) {

    }

    override fun onImageSelectionClicked(index: Int) {
        albumAdapter?.toggleSelected(index)
    }

    override fun onImageSelectionLongClicked(index: Int) {
        rvGallery.setDragSelectActive(true, index)
    }

    override fun onDragSelectionChanged(count: Int) {
        when {
            count != 0 -> {
                when (actionMode) {
                    null -> actionMode = getParentActivity()?.startSupportActionMode(this)
                    else -> menu?.findItem(R.id.action_rename)?.isVisible = count == 1
                }
                actionMode?.title = getString(R.string.all_menu_selected,
                        albumAdapter?.selectedCount, albumAdapter?.itemCount)
            }
            else -> finishActionMode()
        }
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        mode?.menuInflater?.inflate(R.menu.menu_image_select, menu)
        this.menu = menu
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean = false

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_share -> {
                finishActionMode()
                return true
            }
            R.id.action_info -> {
                finishActionMode()
                return true
            }
            R.id.action_delete -> {
                Toast.makeText(context, "Delete", Toast.LENGTH_SHORT).show()
                return true
            }
        }
        return false
    }

    override fun onDestroyActionMode(mode: ActionMode?) = finishActionMode()

    //---[ View implementation ]----------------------------------------------------------------------------------------

    override fun showLoading() {
        pbProgress.visibility = View.VISIBLE
        rvGallery.visibility = View.GONE
    }

    override fun hideLoading() {
        rvGallery.visibility = View.VISIBLE
        pbProgress.visibility = View.GONE
    }

    override fun showAlbum(images: MutableList<Image>) {
        albumAdapter?.setImages(images)
    }

    override fun shareImages(imagePaths: Array<String>) {

    }

    //---[ Other ]------------------------------------------------------------------------------------------------------

    companion object {
        private val KEY_ALBUM = "AlbumFragment.KEY_ALBUM"

        fun newInstance(album: Album?): AlbumFragment {
            val args: Bundle = Bundle()
            args.putParcelable(KEY_ALBUM, album)
            val fragment: AlbumFragment = AlbumFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private fun initRecyclerView(savedInstanceState: Bundle?) {
        var itemDimension = Resources.getSystem().displayMetrics.widthPixels

        val gridLayoutManager: GridLayoutManager
        when (resources.configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                gridLayoutManager = GridLayoutManager(context, 4)
                itemDimension /= 4
            }
            else -> {
                gridLayoutManager = GridLayoutManager(context, 3)
                itemDimension /= 3
            }
        }

        albumAdapter = AlbumAdapter(itemDimension)
        albumAdapter?.restoreInstanceState(savedInstanceState)
        rvGallery.setHasFixedSize(true)
        rvGallery.layoutManager = gridLayoutManager
        rvGallery.setAdapter(albumAdapter)
    }

    private fun finishActionMode() {
        actionMode?.finish()
        actionMode = null
        menu = null
        albumAdapter?.clearSelected()
        albumAdapter?.setSelectionMode(false)
    }
}
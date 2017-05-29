package by.vshkl.android.piktures.ui.albums

import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.view.ActionMode
import android.support.v7.view.ActionMode.Callback
import android.support.v7.widget.GridLayoutManager
import android.view.*
import android.widget.Toast
import by.vshkl.android.piktures.BaseFragment
import by.vshkl.android.piktures.R
import by.vshkl.android.piktures.model.Album
import com.afollestad.dragselectrecyclerview.DragSelectRecyclerViewAdapter.SelectionListener
import com.arellomobile.mvp.presenter.InjectPresenter
import kotlinx.android.synthetic.main.fragment_gallery.*

class AlbumsFragment : BaseFragment(), AlbumsView, AlbumsListener, SelectionListener, Callback {

    @InjectPresenter lateinit var albumsPresenter: AlbumsPresenter
    private var albumsAdapter: AlbumsAdapter? = null
    private var actionMode: ActionMode? = null
    private var menu: Menu? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater?.inflate(R.layout.fragment_gallery, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getParentActivity()?.setSupportActionBar(tbToolbar)
        initRecyclerView(savedInstanceState)
        albumsPresenter.getAlbums(context)
    }

    override fun onResume() {
        super.onResume()
        albumsAdapter?.setAlbumsListener(this)
        albumsAdapter?.setSelectionListener(this)
    }

    override fun onPause() {
        albumsAdapter?.setAlbumsListener(null)
        albumsAdapter?.setSelectionListener(null)
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        albumsAdapter?.saveInstanceState(outState)
    }

    override fun onDetach() {
        albumsPresenter.onDestroy()
        super.onDetach()
    }

    //---[ Listeners ]--------------------------------------------------------------------------------------------------

    override fun onAlbumClicked(album: Album?) {
        getParentActivity()?.mainPresenter?.showAlbum(album)
    }

    override fun onAlbumSelectionClicked(index: Int) {
        albumsAdapter?.toggleSelected(index)
    }

    override fun onAlbumSelectionLongClicked(index: Int) {
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
                        albumsAdapter?.selectedCount, albumsAdapter?.itemCount)
            }
            else -> finishActionMode()
        }
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        mode?.menuInflater?.inflate(R.menu.menu_albums_select, menu)
        this.menu = menu
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean = false

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_rename -> {
                Toast.makeText(context, "Rename", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.action_delete -> {
                albumsPresenter.deleteAlbums(context, albumsAdapter?.getSelectedAlbums(),
                        albumsAdapter?.selectedIndices)
                finishActionMode()
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

    override fun showAlbums(albums: MutableList<Album>) {
        albumsAdapter?.setAlbums(albums)
        albumsAdapter?.notifyDataSetChanged()
    }

    override fun albumsDeleted(deletedIndexes: Array<Int>?) {
        albumsAdapter?.deleteAlbums(deletedIndexes ?: emptyArray())
        albumsAdapter?.notifyDataSetChanged()
    }

    //---[ Other ]------------------------------------------------------------------------------------------------------

    companion object {
        fun newInstance(): Fragment = AlbumsFragment()
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

        albumsAdapter = AlbumsAdapter(itemDimension)
        albumsAdapter?.restoreInstanceState(savedInstanceState)
        rvGallery.setHasFixedSize(true)
        rvGallery.layoutManager = gridLayoutManager
        rvGallery.setAdapter(albumsAdapter)
    }

    private fun finishActionMode() {
        actionMode?.finish()
        actionMode = null
        menu = null
        albumsAdapter?.clearSelected()
        albumsAdapter?.setSelectionMode(false)
    }
}
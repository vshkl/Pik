package by.vshkl.android.pik.ui.albums

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import by.vshkl.android.pik.R
import by.vshkl.android.pik.model.Album
import by.vshkl.android.pik.util.Dimentions
import com.afollestad.dragselectrecyclerview.DragSelectRecyclerViewAdapter
import com.bumptech.glide.Glide

class AlbumsAdapter(private val itemSize: Int) : DragSelectRecyclerViewAdapter<AlbumsViewHolder>() {

    private var albums: MutableList<Album>? = null
    private var albumsListener: AlbumsListener? = null
    private var isSelectingMode = false
    private var padding = Dimentions.px2dp(8F).toInt()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): AlbumsViewHolder
            = AlbumsViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.item_album, parent, false))

    override fun onBindViewHolder(holder: AlbumsViewHolder?, position: Int) {
        super.onBindViewHolder(holder, position)

        val album = albums?.get(position)

        Glide.with(holder?.itemView?.context)
                .load(album?.thumbnail)
                .asBitmap()
                .override(itemSize, itemSize)
                .centerCrop()
                .into(holder?.ivThumbnail)
        if (isIndexSelected(position)) {
            holder?.ivCheck?.visibility = View.VISIBLE
            holder?.itemView?.setPadding(padding, padding, padding, padding)
        } else {
            holder?.ivCheck?.visibility = View.GONE
            holder?.itemView?.setPadding(0, 0, 0, 0)
        }
        holder?.tvName?.text = album?.name
        holder?.tvCount?.text = album?.count.toString()
        holder?.itemView?.setOnClickListener({
            when {
                isSelectingMode -> albumsListener?.onAlbumSelectionClicked(position)
                else -> albumsListener?.onAlbumClicked(album)
            }
        })
        holder?.itemView?.setOnLongClickListener {
            isSelectingMode = true
            albumsListener?.onAlbumSelectionLongClicked(position)
            true
        }
    }

    override fun onViewRecycled(holder: AlbumsViewHolder?) {
        Glide.clear(holder?.ivThumbnail)
        holder?.itemView?.setOnClickListener(null)
        holder?.itemView?.setOnLongClickListener(null)
        super.onViewRecycled(holder)
    }

    override fun getItemCount(): Int = albums?.size ?: 0

    fun setAlbums(albums: MutableList<Album>) {
        this.albums = albums
    }

    fun setAlbumsListener(albumsListener: AlbumsListener?) {
        this.albumsListener = albumsListener
    }

    fun setSelectionMode(isSelectionMode: Boolean) {
        this.isSelectingMode = isSelectionMode
    }

    fun getSelectedAlbums(): List<Album>? = albums
            ?.filterIndexed { index, _ -> selectedIndices.contains(index) }

    fun deleteAlbums(deletedIndexes: Array<Int>) {
        albums = albums?.filterIndexed { index, _ -> !deletedIndexes.contains(index) }?.toMutableList()
    }

    fun renameAlbum(albumId: String, newName: String, albumPosition: Int) {
        albums?.forEachIndexed { index, album ->
            if (index == albumPosition) {
                album.name = newName
                album.thumbnail = album.thumbnail.replace(album.name, newName, false)
                album.id = albumId
            }
        }
    }
}
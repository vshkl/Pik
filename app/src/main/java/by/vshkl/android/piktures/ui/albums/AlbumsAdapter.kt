package by.vshkl.android.piktures.ui.albums

import android.view.LayoutInflater
import android.view.ViewGroup
import by.vshkl.android.piktures.R
import by.vshkl.android.piktures.model.Album
import com.afollestad.dragselectrecyclerview.DragSelectRecyclerViewAdapter
import com.bumptech.glide.Glide

class AlbumsAdapter(private var itemSize: Int) : DragSelectRecyclerViewAdapter<AlbumsViewHolder>() {

    private var albums: MutableList<Album>? = null
    private var albumsListener: AlbumsListener? = null
    private var isSelectingMode = false

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

    fun setAlbums(albums: List<Album>) {
        this.albums = albums.toMutableList()
    }

    fun setAlbumsListener(albumsListener: AlbumsListener?) {
        this.albumsListener = albumsListener
    }

    fun setSelectionMode(isSelectionMode: Boolean) {
        this.isSelectingMode = isSelectionMode
    }
}
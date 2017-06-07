package by.vshkl.android.pik.ui.album

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import by.vshkl.android.pik.R
import by.vshkl.android.pik.model.Image
import by.vshkl.android.pik.util.DimensionUtils
import com.afollestad.dragselectrecyclerview.DragSelectRecyclerViewAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class AlbumAdapter(private val itemSize: Int) : DragSelectRecyclerViewAdapter<AlbumViewHolder>() {

    private var images: MutableList<Image>? = null
    private var albumListener: AlbumListener? = null
    private var isSelectingMode = false
    private var padding = DimensionUtils.px2dp(8F).toInt()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): AlbumViewHolder
            = AlbumViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.item_image, parent, false))

    override fun onBindViewHolder(holder: AlbumViewHolder?, position: Int) {
        super.onBindViewHolder(holder, position)

        val image = images?.get(position)

        Glide.with(holder?.itemView?.context)
                .load(image?.image)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
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
        holder?.itemView?.setOnClickListener({
            when {
                isSelectingMode -> albumListener?.onImageSelectionClicked(position)
                else -> albumListener?.onImageClicked(images, position)
            }
        })
        holder?.itemView?.setOnLongClickListener {
            isSelectingMode = true
            albumListener?.onImageSelectionLongClicked(position)
            true
        }
    }

    override fun getItemCount(): Int = images?.size ?: 0

    fun setImages(images: MutableList<Image>) {
        this.images = images
    }

    fun setAlbumListened(albumListener: AlbumListener?) {
        this.albumListener = albumListener
    }

    fun setSelectionMode(isSelectionMode: Boolean) {
        this.isSelectingMode = isSelectionMode
    }

    fun getSelectedImagePaths(): List<String>? = images
            ?.filterIndexed { index, _ -> selectedIndices.contains(index) }
            ?.map { it.image }

    fun deleteImages(deletedIndexes: Array<Int>) {
        images = images?.filterIndexed { index, _ -> !deletedIndexes.contains(index) }?.toMutableList()
    }
}
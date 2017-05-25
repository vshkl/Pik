package by.vshkl.android.piktures.ui.albums

import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.View
import kotlinx.android.synthetic.main.item_album.view.*

class AlbumsViewHolder(itemView: View) : ViewHolder(itemView) {
    val ivThumbnail = itemView.ivAlbumThumbnail
    val tvName = itemView.tvAlbumName
    val tvCount = itemView.tvAlbumCount
}

package by.vshkl.android.pik.ui.albums

import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.View
import android.widget.ImageView
import by.vshkl.android.pik.customview.PlayRegularTextView
import kotlinx.android.synthetic.main.item_album.view.*

class AlbumsViewHolder(itemView: View) : ViewHolder(itemView) {
    val ivThumbnail: ImageView = itemView.ivAlbumThumbnail
    val ivCheck: ImageView = itemView.ivCheck
    val tvName: PlayRegularTextView = itemView.tvAlbumName
    val tvCount: PlayRegularTextView = itemView.tvAlbumCount
}

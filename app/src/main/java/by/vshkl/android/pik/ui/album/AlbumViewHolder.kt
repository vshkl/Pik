package by.vshkl.android.pik.ui.album

import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.View
import android.widget.ImageView
import kotlinx.android.synthetic.main.item_image.view.*

class AlbumViewHolder(itemView: View) : ViewHolder(itemView) {
    val ivThumbnail: ImageView = itemView.ivImageThumbnail
    val ivCheck: ImageView = itemView.ivCheck
}
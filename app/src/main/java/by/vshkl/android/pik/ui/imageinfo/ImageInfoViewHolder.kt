package by.vshkl.android.pik.ui.imageinfo

import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.View
import android.widget.ImageView
import by.vshkl.android.pik.customview.PlayRegularTextView
import kotlinx.android.synthetic.main.item_info.view.*

class ImageInfoViewHolder(itemView: View) : ViewHolder(itemView) {
    val ivThumbnail: ImageView = itemView.ivIcon
    val tvTitle: PlayRegularTextView = itemView.tvTitle
    val tvSubtitle: PlayRegularTextView = itemView.tvSubtitle
}
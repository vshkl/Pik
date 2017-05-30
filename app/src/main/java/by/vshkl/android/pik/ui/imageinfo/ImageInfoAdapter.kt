package by.vshkl.android.pik.ui.imageinfo

import android.support.v7.widget.RecyclerView.Adapter
import android.view.LayoutInflater
import android.view.ViewGroup
import by.vshkl.android.pik.R
import by.vshkl.android.pik.model.ImageInfo

class ImageInfoAdapter : Adapter<ImageInfoViewHolder>() {

    private var imageInfos: List<ImageInfo>? = null
    private var imageInfoListener: ImageInfoListener? = null

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ImageInfoViewHolder
            = ImageInfoViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.item_info, parent, false))

    override fun onBindViewHolder(holder: ImageInfoViewHolder?, position: Int) {
        val imageInfo = imageInfos?.get(position)

        holder?.ivThumbnail?.setImageResource(imageInfo?.iconRes!!)
        holder?.tvTitle?.text = imageInfo?.title
        holder?.tvSubtitle?.text = imageInfo?.subtitle

        imageInfo?.locationUri
                .takeIf { it != null }
                ?.also { location ->
                    holder?.itemView?.setOnClickListener { imageInfoListener?.onImageInfoLocationClicked(location) }
                }
    }

    override fun getItemCount(): Int = imageInfos?.size ?: 0

    fun setImageInfos(imageInfos: List<ImageInfo>?) {
        this.imageInfos = imageInfos
    }

    fun setImageInfoListener(imageInfoListener: ImageInfoListener?) {
        this.imageInfoListener = imageInfoListener
    }
}
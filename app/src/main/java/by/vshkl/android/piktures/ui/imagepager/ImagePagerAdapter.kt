package by.vshkl.android.piktures.ui.imagepager

import android.net.Uri
import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup
import by.vshkl.android.piktures.model.Image
import com.github.piasy.biv.view.BigImageView

class ImagePagerAdapter(var images: List<Image>?) : PagerAdapter() {

    var imagePagerListener: ImagePagerListener? = null

    override fun instantiateItem(container: ViewGroup?, position: Int): Any {
        val bigImageView: BigImageView = BigImageView(container?.context)
        bigImageView.showImage(Uri.parse("file:" + images?.get(position)?.image))
        bigImageView.setOnClickListener { imagePagerListener?.onImageClicked() }
        container?.addView(bigImageView)
        return bigImageView
    }

    override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any?) {
        container?.removeView(`object` as View)
    }

    override fun getCount(): Int = images?.size ?: 0

    override fun isViewFromObject(view: View?, `object`: Any?): Boolean {
        return view == `object`
    }

    fun getImagePath(position: Int): List<String> {
        return listOf(images?.get(position)?.image!!)
    }
}
package by.vshkl.android.pik

import android.content.Context
import by.vshkl.android.pik.ui.imageviewer.ImageViewerActivity
import com.arellomobile.mvp.MvpAppCompatFragment
import java.lang.ref.WeakReference

open class BasePagerFragment : MvpAppCompatFragment() {

    private var parentActivityRef: WeakReference<ImageViewerActivity>? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ImageViewerActivity) {
            parentActivityRef = WeakReference(context)
        }
    }

    override fun onDetach() {
        parentActivityRef?.clear()
        parentActivityRef = null
        super.onDetach()
    }

    fun getParentActivity(): ImageViewerActivity? {
        return parentActivityRef?.get()
    }
}
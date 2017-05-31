package by.vshkl.android.pik

import android.content.Context
import by.vshkl.android.pik.ui.imagepager.ImagePagerActivity
import com.arellomobile.mvp.MvpAppCompatFragment
import java.lang.ref.WeakReference

open class BasePagerFragment : MvpAppCompatFragment() {

    private var parentActivityRef: WeakReference<ImagePagerActivity>? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ImagePagerActivity) {
            parentActivityRef = WeakReference(context)
        }
    }

    override fun onDetach() {
        parentActivityRef?.clear()
        parentActivityRef = null
        super.onDetach()
    }

    fun getParentActivity(): ImagePagerActivity? {
        return parentActivityRef?.get()
    }
}
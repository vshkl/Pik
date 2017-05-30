package by.vshkl.android.pik

import android.content.Context
import by.vshkl.android.pik.ui.main.MainActivity
import com.arellomobile.mvp.MvpAppCompatDialogFragment
import java.lang.ref.WeakReference

open class BaseDialogFragment : MvpAppCompatDialogFragment() {

    private var parentActivityRef: WeakReference<MainActivity>? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            parentActivityRef = WeakReference(context)
        }
    }

    override fun onDetach() {
        parentActivityRef?.clear()
        parentActivityRef = null
        super.onDetach()
    }

    fun getParentActivity(): MainActivity? {
        return parentActivityRef?.get()
    }
}
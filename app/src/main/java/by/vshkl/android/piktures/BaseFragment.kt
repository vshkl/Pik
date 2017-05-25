package by.vshkl.android.piktures

import android.content.Context
import by.vshkl.android.piktures.ui.main.MainActivity
import com.arellomobile.mvp.MvpAppCompatFragment
import java.lang.ref.WeakReference


open class BaseFragment : MvpAppCompatFragment() {

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

    fun getParrentActivity(): MainActivity? {
        return parentActivityRef?.get()
    }
}
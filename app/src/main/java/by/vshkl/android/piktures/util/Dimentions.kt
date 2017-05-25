package by.vshkl.android.piktures.util

import android.content.res.Resources
import android.util.TypedValue

object Dimentions {

    fun px2dp(px: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, Resources.getSystem().displayMetrics)
    }
}
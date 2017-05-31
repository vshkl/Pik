package by.vshkl.android.pik.customview

import android.content.Context
import android.graphics.Typeface
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet

class PlayRegularTextView : AppCompatTextView {

    constructor(context: Context?) : super(context) {
        setupTypeface(context)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        setupTypeface(context)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setupTypeface(context)
    }

    private fun setupTypeface(context: Context?) {
        this.typeface = Typeface.createFromAsset(context?.assets, "fonts/Play-Regular.ttf")
    }
}
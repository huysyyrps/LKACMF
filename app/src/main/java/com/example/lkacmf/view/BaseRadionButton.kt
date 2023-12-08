package com.example.lkacmf.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.RadioButton
import com.example.lkacmf.R

class BaseRadionButton : androidx.appcompat.widget.AppCompatRadioButton {
    private var drawable: Drawable? = null
    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.BaseRadioButton) //获取我们定义的属性

        drawable = typedArray.getDrawable(R.styleable.BaseRadioButton_drawableTop)
        drawable?.setBounds(0, 3, 50, 53)
        setCompoundDrawables(null, drawable, null, null)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {

    }
}
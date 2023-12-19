package com.example.lkacmf.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import com.example.lkacmf.R
import com.example.lkacmf.serialport.DataManagement
import com.example.lkacmf.util.LogUtil
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IDataSet


class BaseLineChart : LineChart {
    var paint = Paint()
    constructor(context: Context) : super(context) {
        initView(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView(context, attrs)

    }

    private fun initView(context: Context, attrs: AttributeSet?) {
        paint.color = context.getColor(R.color.red)
        paint.style = Paint.Style.STROKE
        paint.isAntiAlias = true
        paint.strokeWidth = 3.0f
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
//        canvasWidth = width!!
//        canvasHeight = height!!
        if((DataManagement.frameRect.left!=0&&DataManagement.frameRect.top!=0)&&
            (DataManagement.frameRect.right!=0&&DataManagement.frameRect.bottom!=0)){
            canvas?.drawRect(DataManagement.frameRect, paint);
        }
    }

//    @SuppressLint("ClickableViewAccessibility")
//    override fun onTouchEvent(event: MotionEvent?): Boolean {
//        when (event?.action) {
//            MotionEvent.ACTION_DOWN -> {
//                downX = event?.x
//                downY = event?.y
//                rect.left = event?.x.toInt()
//                rect.top = event?.y.toInt()
//                //返回当前数据下标
//                val highlight = this.getHighlightByTouchPoint(event.x, event.y)
//                val set = mData.getDataSetByIndex(highlight.dataSetIndex)
//                val e: Entry = mData.getEntryForHighlight(highlight)
//                val entryIndex = set.getEntryIndex(e)
//                LogUtil.e("LineChartSetting","$entryIndex")
//            }
//            MotionEvent.ACTION_MOVE -> {
//                rect.right = event?.x.toInt()
//                rect.bottom = event?.y.toInt()
//                invalidate()
//            }
//            MotionEvent.ACTION_UP -> {
//                //返回当前数据下标
//                val highlight = this.getHighlightByTouchPoint(event.x, event.y)
//                val set = mData.getDataSetByIndex(highlight.dataSetIndex)
//                val e: Entry = mData.getEntryForHighlight(highlight)
//                val entryIndex = set.getEntryIndex(e)
//                LogUtil.e("LineChartEnd","$entryIndex")
//                rect.setEmpty()
//                invalidate()
//            }
//        }
//        return false
//    }
}
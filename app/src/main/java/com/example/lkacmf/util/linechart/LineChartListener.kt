package com.example.lkacmf.util.linechart

import android.graphics.Matrix
import android.graphics.Rect
import android.os.Build
import android.view.MotionEvent
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.lkacmf.R
import com.example.lkacmf.fragment.AnalystsFragment
import com.example.lkacmf.fragment.CalibrationFragment
import com.example.lkacmf.serialport.DataManagement
import com.example.lkacmf.util.LogUtil
import com.example.lkacmf.view.BaseLineChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.ViewPortHandler
@RequiresApi(Build.VERSION_CODES.O)
object LineChartListener {
    var startIndex = 0
    var endIndex = 0
    var oldScaleX = 0F
    var oldScaleY = 0F
    var mMatrix = Matrix()
    val mSavedMatrix = Matrix()
    private val mTouchPointCenter = MPPointF.getInstance(0f, 0f)
    private var rect: Rect = DataManagement.frameRect
    fun lineChartSetListener(view: BaseLineChart, lineChartBX: BaseLineChart, lineChartBZ: BaseLineChart, lineChart: LineChart, fragment: CalibrationFragment) {
        view.onChartGestureListener = object : OnChartGestureListener {
            override fun onChartGestureStart(event: MotionEvent, lastPerformedGesture: ChartTouchListener.ChartGesture) {
                if (event.pointerCount == 1) {
                    if (fragment.screenState) {
                        LogUtil.e("TAG", "按下")
                        rect.left = event?.x.toInt()
                        rect.top = event?.y.toInt()
                        //返回当前数据下标
                        val highlight = view.getHighlightByTouchPoint(event.x, event.y)
                        val set = view.data.getDataSetByIndex(highlight.dataSetIndex)
                        val e: Entry = view.data.getEntryForHighlight(highlight)
                        startIndex = set.getEntryIndex(e)
//                    binding.lineChartAnaBX.invalidate()
                    } else {
                        LogUtil.e("TAG", "按下")
                        val h: Highlight = view.getHighlightByTouchPoint(event.x, event.y)
                        LogUtil.e("LineChartSetting", "$h")
                        mSavedMatrix.set(mMatrix)
                    }
                }
            }

            override fun onChartGestureDoubleStart(event: MotionEvent) {
                if (event.pointerCount >= 2) {
                    if (fragment.screenState) {
                        LogUtil.e("TAG", "按下TWO")
                        rect.setEmpty()
                        view.invalidate()
                    } else {
                        midPoint(mTouchPointCenter, event)
                    }
                }
            }

            override fun onChartGestureEnd(event: MotionEvent, lastPerformedGesture: ChartTouchListener.ChartGesture) {
                // 抬起,取消
                LogUtil.e("TAG", "抬起")
//                if (event.pointerCount == 1) {
                if (fragment.screenState) {
                    if ((rect.left != 0 && rect.top != 0) && (rect.right != 0 && rect.bottom != 0)) {
                        val highlight = view.getHighlightByTouchPoint(event.x, event.y)
                        val set = view.data.getDataSetByIndex(highlight.dataSetIndex)
                        val e: Entry = view.data.getEntryForHighlight(highlight)
                        endIndex = set.getEntryIndex(e)
                        rect.setEmpty()
                        view.invalidate()
                        LineDataRead.framePlay(startIndex, endIndex, lineChartBX, lineChartBZ, lineChart)
                    }
                    rect.setEmpty()
                } else {
                    // 抬起,取消
                    LogUtil.e("TAG", "抬起")
                    val h: Highlight = view.getHighlightByTouchPoint(event.x, event.y)
                    LogUtil.e("LineChartEnd", "$h")
                }

//                }
            }

            override fun onChartLongPressed(me: MotionEvent) {
                // 长按
                LogUtil.e("TAG", "长按")
            }

            override fun onChartDoubleTapped(me: MotionEvent) {
                // 双击
                LogUtil.e("TAG", "双击")
            }

            override fun onChartSingleTapped(me: MotionEvent) {
                // 单击
                LogUtil.e("TAG", "单击")
            }

            override fun onChartFling(me1: MotionEvent, me2: MotionEvent, velocityX: Float, velocityY: Float) {
                // 甩动
                LogUtil.e("TAG", "甩动")
            }

            override fun onChartScale(event: MotionEvent, scaleX: Float, scaleY: Float) {
//                if (!fragment.screenState) {
                // 缩放
                when (view.id) {
                    R.id.lineChartBX -> {
                        if (oldScaleX != scaleX || oldScaleY != scaleY) {
                            if (scaleX != 1F || scaleY != 1F) {
                                mMatrix.set(mSavedMatrix)
                                val t = getTrans(
                                    mTouchPointCenter.x,
                                    mTouchPointCenter.y,
                                    fragment.binding.lineChartBZ
                                )
                                mMatrix.postScale(scaleX, 1F, t.x, t.y)
                                fragment.binding.lineChartBZ.viewPortHandler.refresh(mMatrix, fragment.binding.lineChartBZ, true)
                                fragment.binding.lineChart.viewPortHandler.refresh(mMatrix, fragment.binding.lineChart, true)
                            }
                        }
                    }
                    R.id.lineChartBZ -> {
                        if (oldScaleX != scaleX || oldScaleY != scaleY) {
                            if (scaleX != 1F || scaleY != 1F) {
                                mMatrix.set(mSavedMatrix)
                                val t = getTrans(
                                    mTouchPointCenter.x,
                                    mTouchPointCenter.y,
                                    fragment.binding.lineChartBX
                                )
                                mMatrix.postScale(scaleX, 1F, t.x, t.y)
                                fragment.binding.lineChartBX.viewPortHandler.refresh(mMatrix, fragment.binding.lineChartBX, true)
                                fragment.binding.lineChart.viewPortHandler.refresh(mMatrix, fragment.binding.lineChart, true)
                            }
                        }
                    }
                    R.id.lineChart -> {
                        if (oldScaleX != scaleX || oldScaleY != scaleY) {
                            if (scaleX != 1F || scaleY != 1F) {
                                mMatrix.set(mSavedMatrix)
                                val t = getTrans(
                                    mTouchPointCenter.x,
                                    mTouchPointCenter.y,
                                    fragment.binding.lineChart
                                )
                                mMatrix.postScale(scaleX, 1F, t.x, t.y)
                                fragment.binding.lineChartBZ.viewPortHandler.refresh(mMatrix, fragment.binding.lineChartBZ, true)
                                fragment.binding.lineChartBX.viewPortHandler.refresh(mMatrix, fragment.binding.lineChartBX, true)
                            }
                        }
                    }
//                    }
                }
            }

            override fun onChartTranslate(me: MotionEvent, dX: Float, dY: Float) {
//                if (!fragment.screenState){
                // 移动
                LogUtil.e("TAG", "移动")
                LogUtil.e("TAG", "$dX  $dY")
                if (!fragment.screenState){
                    when (view.id) {
                        R.id.lineChartBX -> {
                            mMatrix.set(mSavedMatrix)
                            mMatrix.postTranslate(dX, dY)
                            fragment.binding.lineChartBZ.viewPortHandler.refresh(mMatrix, fragment.binding.lineChartBZ, true)
                            fragment.binding.lineChart.viewPortHandler.refresh(mMatrix, fragment.binding.lineChart, true)
                        }
                        R.id.lineChartBZ -> {
                            mMatrix.set(mSavedMatrix)
                            mMatrix.postTranslate(dX, dY)
                            fragment.binding.lineChartBX.viewPortHandler.refresh(mMatrix, fragment.binding.lineChartBX, true)
                            fragment.binding.lineChart.viewPortHandler.refresh(mMatrix, fragment.binding.lineChart, true)
                        }
                        R.id.lineChart -> {
                            mMatrix.set(mSavedMatrix)
                            mMatrix.postTranslate(dX, dY)
                            fragment.binding.lineChartBZ.viewPortHandler.refresh(mMatrix, fragment.binding.lineChartBZ, true)
                            fragment.binding.lineChartBX.viewPortHandler.refresh(mMatrix, fragment.binding.lineChartBX, true)
                        }
                    }
                }
//                }
            }

            override fun onChartMove(event: MotionEvent) {
                if (event.pointerCount == 1) {
                    if (fragment.screenState) {
                        rect.right = event.x.toInt()
                        rect.bottom = event.y.toInt()
                        view.invalidate()
                    }
                }
            }
        }
    }

    fun lineChartSetListenerAna(view: BaseLineChart, lineChartBX: BaseLineChart, lineChartBZ: BaseLineChart, lineChart: LineChart, fragment: AnalystsFragment) {
        view.onChartGestureListener = object : OnChartGestureListener {
            override fun onChartGestureStart(event: MotionEvent, lastPerformedGesture: ChartTouchListener.ChartGesture) {
                if (event.pointerCount == 1) {
                    if (fragment.screenState) {
                        LogUtil.e("TAG", "按下")
                        rect.left = event?.x.toInt()
                        rect.top = event?.y.toInt()
                        //返回当前数据下标
                        val highlight = view.getHighlightByTouchPoint(event.x, event.y)
                        val set = view.data.getDataSetByIndex(highlight.dataSetIndex)
                        val e: Entry = view.data.getEntryForHighlight(highlight)
                        startIndex = set.getEntryIndex(e)
//                    binding.lineChartAnaBX.invalidate()
                    } else {
                        LogUtil.e("TAG", "按下")
                        val h: Highlight = view.getHighlightByTouchPoint(event.x, event.y)
                        LogUtil.e("LineChartSetting", "$h")
                        mSavedMatrix.set(mMatrix)
                    }
                }
            }

            override fun onChartGestureDoubleStart(event: MotionEvent) {
                if (event.pointerCount >= 2) {
                    if (fragment.screenState) {
                        LogUtil.e("TAG", "按下TWO")
                        rect.setEmpty()
                        view.invalidate()
                    } else {
                        midPoint(mTouchPointCenter, event)
                    }
                }
            }

            override fun onChartGestureEnd(event: MotionEvent, lastPerformedGesture: ChartTouchListener.ChartGesture) {
                // 抬起,取消
                LogUtil.e("TAG", "抬起")
//                if (event.pointerCount == 1) {
                if (fragment.screenState) {
                    if ((rect.left != 0 && rect.top != 0) && (rect.right != 0 && rect.bottom != 0)) {
                        val highlight = view.getHighlightByTouchPoint(event.x, event.y)
                        val set = view.data.getDataSetByIndex(highlight.dataSetIndex)
                        val e: Entry = view.data.getEntryForHighlight(highlight)
                        endIndex = set.getEntryIndex(e)
                        rect.setEmpty()
                        view.invalidate()
                        LineDataRead.framePlay(startIndex, endIndex, lineChartBX, lineChartBZ, lineChart)
                    }
                    rect.setEmpty()
                } else {
                    // 抬起,取消
                    LogUtil.e("TAG", "抬起")
                    val h: Highlight = view.getHighlightByTouchPoint(event.x, event.y)
                    LogUtil.e("LineChartEnd", "$h")
                }

//                }
            }

            override fun onChartLongPressed(me: MotionEvent) {
                // 长按
                LogUtil.e("TAG", "长按")
            }

            override fun onChartDoubleTapped(me: MotionEvent) {
                // 双击
                LogUtil.e("TAG", "双击")
            }

            override fun onChartSingleTapped(me: MotionEvent) {
                // 单击
                LogUtil.e("TAG", "单击")
            }

            override fun onChartFling(me1: MotionEvent, me2: MotionEvent, velocityX: Float, velocityY: Float) {
                // 甩动
                LogUtil.e("TAG", "甩动")
            }

            override fun onChartScale(event: MotionEvent, scaleX: Float, scaleY: Float) {
//                if (!fragment.screenState) {
                // 缩放
                when (view.id) {
                    R.id.lineChartBX -> {
                        if (oldScaleX != scaleX || oldScaleY != scaleY) {
                            if (scaleX != 1F || scaleY != 1F) {
                                mMatrix.set(mSavedMatrix)
                                val t = getTrans(
                                    mTouchPointCenter.x,
                                    mTouchPointCenter.y,
                                    fragment.binding.lineChartBZ
                                )
                                mMatrix.postScale(scaleX, 1F, t.x, t.y)
                                fragment.binding.lineChartBZ.viewPortHandler.refresh(mMatrix, fragment.binding.lineChartBZ, true)
                                fragment.binding.lineChart.viewPortHandler.refresh(mMatrix, fragment.binding.lineChart, true)
                            }
                        }
                    }
                    R.id.lineChartBZ -> {
                        if (oldScaleX != scaleX || oldScaleY != scaleY) {
                            if (scaleX != 1F || scaleY != 1F) {
                                mMatrix.set(mSavedMatrix)
                                val t = getTrans(
                                    mTouchPointCenter.x,
                                    mTouchPointCenter.y,
                                    fragment.binding.lineChartBX
                                )
                                mMatrix.postScale(scaleX, 1F, t.x, t.y)
                                fragment.binding.lineChartBX.viewPortHandler.refresh(mMatrix, fragment.binding.lineChartBX, true)
                                fragment.binding.lineChart.viewPortHandler.refresh(mMatrix, fragment.binding.lineChart, true)
                            }
                        }
                    }
                    R.id.lineChart -> {
                        if (oldScaleX != scaleX || oldScaleY != scaleY) {
                            if (scaleX != 1F || scaleY != 1F) {
                                mMatrix.set(mSavedMatrix)
                                val t = getTrans(
                                    mTouchPointCenter.x,
                                    mTouchPointCenter.y,
                                    fragment.binding.lineChart
                                )
                                mMatrix.postScale(scaleX, 1F, t.x, t.y)
                                fragment.binding.lineChartBZ.viewPortHandler.refresh(mMatrix, fragment.binding.lineChartBZ, true)
                                fragment.binding.lineChartBX.viewPortHandler.refresh(mMatrix, fragment.binding.lineChartBX, true)
                            }
                        }
                    }
//                    }
                }
            }

            override fun onChartTranslate(me: MotionEvent, dX: Float, dY: Float) {
//                if (!fragment.screenState){
                // 移动
                LogUtil.e("TAG", "移动")
                LogUtil.e("TAG", "$dX  $dY")
                if (!fragment.screenState){
                    when (view.id) {
                        R.id.lineChartBX -> {
                            mMatrix.set(mSavedMatrix)
                            mMatrix.postTranslate(dX, dY)
                            fragment.binding.lineChartBZ.viewPortHandler.refresh(mMatrix, fragment.binding.lineChartBZ, true)
                            fragment.binding.lineChart.viewPortHandler.refresh(mMatrix, fragment.binding.lineChart, true)
                        }
                        R.id.lineChartBZ -> {
                            mMatrix.set(mSavedMatrix)
                            mMatrix.postTranslate(dX, dY)
                            fragment.binding.lineChartBX.viewPortHandler.refresh(mMatrix, fragment.binding.lineChartBX, true)
                            fragment.binding.lineChart.viewPortHandler.refresh(mMatrix, fragment.binding.lineChart, true)
                        }
                        R.id.lineChart -> {
                            mMatrix.set(mSavedMatrix)
                            mMatrix.postTranslate(dX, dY)
                            fragment.binding.lineChartBZ.viewPortHandler.refresh(mMatrix, fragment.binding.lineChartBZ, true)
                            fragment.binding.lineChartBX.viewPortHandler.refresh(mMatrix, fragment.binding.lineChartBX, true)
                        }
                    }
                }
//                }
            }

            override fun onChartMove(event: MotionEvent) {
                if (event.pointerCount == 1) {
                    if (fragment.screenState) {
                        rect.right = event.x.toInt()
                        rect.bottom = event.y.toInt()
                        view.invalidate()
                    }
                }
            }
        }
    }

    private fun midPoint(point: MPPointF, event: MotionEvent) {
        var x = event.getX(0) + event.getX(1);
        var y = event.getY(0) + event.getY(1);
        point.x = (x / 2f);
        point.y = (y / 2f);
    }

    fun getTrans(x: Float, y: Float, lineChart: LineChart): MPPointF {
        val vph: ViewPortHandler = lineChart.getViewPortHandler()
        val xTrans = x - vph.offsetLeft()
        var yTrans = 0f

        // check if axis is inverted
        yTrans = -(lineChart.getMeasuredHeight() - y - vph.offsetBottom())
        return MPPointF.getInstance(xTrans, yTrans)
    }
}
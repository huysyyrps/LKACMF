package com.example.lkacmf.util.linechart

import android.graphics.Rect
import android.view.MotionEvent
import com.example.lkacmf.serialport.DataManagement
import com.example.lkacmf.util.LogUtil
import com.example.lkacmf.view.BaseLineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener

object LineChartListener {
    private var startIndex = 0
    private var endIndex = 0
    private var rect: Rect = DataManagement.frameRect
    fun lineChartSetListener(view: BaseLineChart, state: Boolean,lineChartBX: BaseLineChart, lineChartBZ: BaseLineChart, lineChart: BaseLineChart){
        view.onChartGestureListener = object : OnChartGestureListener {
            override fun onChartGestureStart(event: MotionEvent, lastPerformedGesture: ChartTouchListener.ChartGesture) {
                // 按下
                if (event.pointerCount == 1) {
                    LogUtil.e("TAG", "按下")
                    rect.left = event?.x.toInt()
                    rect.top = event?.y.toInt()
                    //返回当前数据下标
                    val highlight = view.getHighlightByTouchPoint(event.x, event.y)
                    val set = view.data.getDataSetByIndex(highlight.dataSetIndex)
                    val e: Entry = view.data.getEntryForHighlight(highlight)
                    startIndex = set.getEntryIndex(e)
//                    binding.lineChartAnaBX.invalidate()
                }
            }

            override fun onChartGestureDoubleStart(event: MotionEvent) {
                if (event.pointerCount >= 2) {
                    LogUtil.e("TAG", "按下TWO")
                    rect.setEmpty()
                    view.invalidate()
                }
            }

            override fun onChartGestureEnd(event: MotionEvent, lastPerformedGesture: ChartTouchListener.ChartGesture) {
                // 抬起,取消
                if (event.pointerCount == 1) {
                    LogUtil.e("TAG", "抬起")
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
                }
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
                // 缩放
            }

            override fun onChartTranslate(me: MotionEvent, dX: Float, dY: Float) {
                // 移动
                LogUtil.e("TAG", "移动")
            }

            override fun onChartMove(event: MotionEvent) {
                if (event.pointerCount == 1) {
                    rect.right = event.x.toInt()
                    rect.bottom = event.y.toInt()
                    view.invalidate()
                }
            }
        }
    }
}
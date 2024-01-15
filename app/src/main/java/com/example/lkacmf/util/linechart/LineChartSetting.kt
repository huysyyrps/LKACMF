package com.example.lkacmf.util.linechart

import android.app.Activity
import android.graphics.Matrix
import android.view.MotionEvent
import com.example.lkacmf.MyApplication
import com.example.lkacmf.R
import com.example.lkacmf.util.LogUtil
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.ViewPortHandler

object LineChartSetting {
    var oldScaleX = 0F
    var oldScaleY = 0F
    var mMatrix = Matrix()
    val mSavedMatrix = Matrix()
    private val mTouchPointCenter = MPPointF.getInstance(0f, 0f)
    fun SettingLineChart(activity: Activity, linechar: LineChart, showX: Boolean,scale:Boolean ) {
        linechar.setDrawGridBackground(false)//是否显示表格颜色
        linechar.setDrawBorders(false)// 是否在折线图上添加边框
        linechar.setScaleEnabled(scale)// 是否可以缩放
        linechar.setPinchZoom(false) // X,Y轴同时缩放，false则X,Y轴单独缩放,默认false
        linechar.isScaleXEnabled = scale  // X轴上的缩放,默认true
        linechar.isScaleYEnabled = scale  // Y轴上的缩放,默认true
        linechar.isDragEnabled = true// 是否可以拖拽
//        linechar.setTouchEnabled(true) // 设置是否可以触摸
        linechar.description = null// 数据描述
        //设置是否可以通过双击屏幕放大图表。默认是true
        linechar.isDoubleTapToZoomEnabled = false;

//        linechar.isDoubleTapToZoomEnabled = false
//        linechar.viewPortHandler.setMaximumScaleX(30.0f)//限制X轴放大限制
//        linechar.viewPortHandler.setMaximumScaleY(3.0f)
        linechar.legend.isEnabled = false;// 不显示图例
//        linechar.setVisibleXRangeMaximum(80.0f)
        linechar.extraTopOffset = 5.0f
        linechar.extraLeftOffset = 0.0f
        linechar.extraRightOffset = 0.0f
        linechar.extraBottomOffset = 5.0f
        linechar.minOffset = 0.0f
        //硬件加速
        linechar.setHardwareAccelerationEnabled(true);
        //region
        // 当前统计图表中最多在x轴坐标线上显示的总量
//        linechar.setVisibleXRangeMaximum(300f);
//        linechar.moveViewToX(300f);
//        linechar.onChartGestureListener = object : OnChartGestureListener {
//            // 手势监听器
//            override fun onChartGestureStart(event: MotionEvent, lastPerformedGesture: ChartTouchListener.ChartGesture) {
//                // 按下
//                LogUtil.e("TAG", "按下")
//                val h: Highlight = linechar.getHighlightByTouchPoint(event.x, event.y)
//                LogUtil.e("LineChartSetting","$h")
//                mSavedMatrix.set(mMatrix)
//            }
//
//            override fun onChartGestureDoubleStart(event: MotionEvent) {
//                if (event.pointerCount >= 2) {
//                    midPoint(mTouchPointCenter, event)
//                }
//            }
//
//            override fun onChartGestureEnd(event: MotionEvent, lastPerformedGesture: ChartTouchListener.ChartGesture) {
//                // 抬起,取消
//                LogUtil.e("TAG", "抬起")
//                val h: Highlight = linechar.getHighlightByTouchPoint(event.x, event.y)
//                LogUtil.e("LineChartEnd","$h")
//            }
//
//            override fun onChartLongPressed(me: MotionEvent) {
//                // 长按
//                LogUtil.e("TAG", "长按")
//            }
//
//            override fun onChartDoubleTapped(me: MotionEvent) {
//                // 双击
//                LogUtil.e("TAG", "双击")
//            }
//
//            override fun onChartSingleTapped(me: MotionEvent) {
//                // 单击
//                LogUtil.e("TAG", "单击")
//            }
//
//            override fun onChartFling(me1: MotionEvent, me2: MotionEvent, velocityX: Float, velocityY: Float) {
//                // 甩动
//                LogUtil.e("TAG", "甩动")
//            }
//
//            override fun onChartScale(event: MotionEvent, scaleX: Float, scaleY: Float) {
//                // 缩放
//                when (linechar.id) {
//                    R.id.lineChartBX -> {
//                        if (oldScaleX != scaleX || oldScaleY != scaleY) {
//                            if (scaleX != 1F || scaleY != 1F) {
//                                mMatrix.set(mSavedMatrix)
//                                val t = getTrans(mTouchPointCenter.x, mTouchPointCenter.y,activity.lineChartBZ)
//                                mMatrix.postScale(scaleX, 1F, t.x, t.y)
//                                activity.lineChartBZ.viewPortHandler.refresh(mMatrix, activity.lineChartBZ, true)
//                                activity.lineChart.viewPortHandler.refresh(mMatrix, activity.lineChart, true)
//                            }
//                        }
//                    }
//                    R.id.lineChartBZ -> {
//                        if (oldScaleX != scaleX || oldScaleY != scaleY) {
//                            if (scaleX != 1F || scaleY != 1F) {
//                                mMatrix.set(mSavedMatrix)
//                                val t = getTrans(mTouchPointCenter.x, mTouchPointCenter.y,activity.lineChartBX)
//                                mMatrix.postScale(scaleX, 1F, t.x, t.y)
//                                activity.lineChartBX.viewPortHandler.refresh(mMatrix, activity.lineChartBX, true)
//                                activity.lineChart.viewPortHandler.refresh(mMatrix, activity.lineChart, true)
//                            }
//                        }
//                    }
//                    R.id.lineChart -> {
//                    if (oldScaleX != scaleX || oldScaleY != scaleY) {
//                        if (scaleX != 1F || scaleY != 1F) {
//                            mMatrix.set(mSavedMatrix)
//                            val t = getTrans(mTouchPointCenter.x, mTouchPointCenter.y,activity.lineChart)
//                            mMatrix.postScale(scaleX, 1F, t.x, t.y)
//                            activity.lineChartBZ.getViewPortHandler().refresh(mMatrix, activity.lineChartBZ, true)
//                            activity.lineChartBX.viewPortHandler.refresh(mMatrix, activity.lineChartBX, true)
//                        }
//                    }
//                }
//                }
//            }
//
//            override fun onChartTranslate(me: MotionEvent, dX: Float, dY: Float) {
//                // 移动
//                LogUtil.e("TAG", "移动")
//                LogUtil.e("TAG", "$dX  $dY")
//                when (linechar.id) {
//                    R.id.lineChartBX -> {
//                        mMatrix.set(mSavedMatrix)
//                        mMatrix.postTranslate(dX, dY)
//                        activity.lineChartBZ.viewPortHandler.refresh(mMatrix, activity.lineChartBZ, true)
//                        activity.lineChart.viewPortHandler.refresh(mMatrix, activity.lineChart, true)
//                    }
//                    R.id.lineChartBZ -> {
//                        mMatrix.set(mSavedMatrix)
//                        mMatrix.postTranslate(dX, dY)
//                        activity.lineChartBX.viewPortHandler.refresh(mMatrix, activity.lineChartBX, true)
//                        activity.lineChart.viewPortHandler.refresh(mMatrix, activity.lineChart, true)
//                    }
//                    R.id.lineChart -> {
//                        mMatrix.set(mSavedMatrix)
//                        mMatrix.postTranslate(dX, dY)
//                        activity.lineChartBZ.viewPortHandler.refresh(mMatrix, activity.lineChartBZ, true)
//                        activity.lineChartBX.viewPortHandler.refresh(mMatrix, activity.lineChartBX, true)
//                    }
//                }
//            }
//
//            override fun onChartMove(me: MotionEvent?) {
//                LogUtil.e("TAG", "滑动")
//            }
//        }
        //endregion


        //X轴
        val xAxis = linechar.xAxis
        xAxis.textColor = MyApplication.context.resources.getColor(R.color.theme_color)
        xAxis.gridColor = MyApplication.context.resources.getColor(R.color.tv_order_left)
        xAxis.axisLineColor = MyApplication.context.resources.getColor(R.color.black)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.axisLineWidth = 0f//轴线宽度
        xAxis.isEnabled = showX//是否显示X轴
        //标签数
        xAxis.setLabelCount(6)
//        xAxis.granularity = 1F//设置 后 value是从0开始的，每次加1
        xAxis.setAvoidFirstLastClipping(true) //图表将避免第一个和最后一个标签条目被减掉在图表或屏幕的边缘


        //左侧Y轴
        val leftYAxis = linechar.axisLeft
        //标签数
        leftYAxis.setLabelCount(6)
        leftYAxis.textColor = MyApplication.context.resources.getColor(R.color.red)
        leftYAxis.gridColor = MyApplication.context.resources.getColor(R.color.tv_order_left)//网格颜色
        leftYAxis.axisLineColor = MyApplication.context.resources.getColor(R.color.theme_back_color1)//轴线颜色
        leftYAxis.axisLineWidth = 0f//轴线宽度
        //leftYAxis.spaceBottom = 10f // 最小值距离底部比例。默认10，y轴独有
//        leftYAxis.spaceTop = 10f // 设置最大值到图标顶部的距离占所有数据范围的比例。默认10，y轴独有
//        leftYAxis.axisMinimum = -10.0f;
//        leftYAxis.axisMaximum = 50f//为此轴设置自定义最大值。
        leftYAxis.isEnabled = true;//是否显示
//        leftYAxis.granularity = 1.0f;//设置Y轴坐标之间的最小间隔（因为此图有缩放功能，X轴,Y轴可设置可缩放）
        leftYAxis.setDrawGridLines(false)
////        yLeftAxis.axisMinimum = -30f
        leftYAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART); //Y轴标签显示位置


        //右侧Y轴
        val yRightAxis = linechar.axisRight
        yRightAxis.axisLineColor = MyApplication.context.resources.getColor(R.color.black)//轴线颜色
        yRightAxis.setDrawLabels(false)//右侧轴线不显示标签
        yRightAxis.axisLineWidth = 0f//轴线宽度
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
package com.example.lkacmf.util.linechart

import android.annotation.SuppressLint
import com.afollestad.materialdialogs.MaterialDialog
import com.example.lkacmf.MyApplication
import com.example.lkacmf.R
import com.example.lkacmf.activity.MainActivity
import com.example.lkacmf.serialport.DataManagement
import com.example.lkacmf.serialport.DataManagement.landBXList
import com.example.lkacmf.serialport.DataManagement.landBZList
import com.example.lkacmf.serialport.DataManagement.landList
import com.example.lkacmf.util.BinaryChange
import com.example.lkacmf.util.LogUtil
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

object LineDataRead {
    var deviceCode = ""
    var activatCode = ""
    private lateinit var deviceDate: String
    private lateinit var context: MainActivity
    private lateinit var dialog: MaterialDialog
//    var landBXList: ArrayList<Entry> = ArrayList()
//    var landBXRemoveList: ArrayList<Entry> = ArrayList()
//    var landBZList: ArrayList<Entry> = ArrayList()
//    var landBZRemoveList: ArrayList<Entry> = ArrayList()
//    var landList: ArrayList<Entry> = ArrayList()
//    var landRemoveList: ArrayList<Entry> = ArrayList()

    var oldXData: Float = 0F
    var removeIndex: Int = 0
    var chartScale: Float = 1F

    /**
     * 测量信息
     */
    fun readMeterData(readData: String, lineChartBX: LineChart, lineChartBZ: LineChart, lineChart: LineChart, punctationState: Boolean) {
        var xData = BinaryChange.hexTofloat(readData.substring(0, 8))
        var yBXData = BinaryChange.hexTofloat(readData.substring(8, 16))
        var yBZData = BinaryChange.hexTofloat(readData.substring(16, 24))
        DataManagement.addBXEntry(xData, yBXData)
        DataManagement.addBZEntry(xData, yBZData)
        DataManagement.addEntry(yBZData, yBXData)

        if (punctationState){
            val ll1 = LimitLine((landBXList[landBXList.size-1].x), "")
            ll1.lineWidth = 1f
            ll1.enableDashedLine(10f, 2f, 0f)
//            ll1.labelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP
//            ll1.textSize = 10f
            val bXXAxis: XAxis = lineChartBX.xAxis
            bXXAxis.addLimitLine(ll1)
            val bZXAxis: XAxis = lineChartBZ.xAxis
            bZXAxis.addLimitLine(ll1)
            DataManagement.punctationList.add(landBXList.size-1)
        }
        notifyChartData(lineChartBX, xData, yBXData)
        notifyChartData(lineChartBZ, xData, yBZData)
        notifyChartData(lineChart, yBZData, yBXData)
//        LogUtil.e("TAG","$xData---$yBZData---$yBXData")
        oldXData = xData
    }

    private fun createSet(): LineDataSet? {
        val lineSet = LineDataSet(null, "DataSet 1")
        //不绘制数据
        lineSet.setDrawValues(false)
        //不绘制圆形指示器
        lineSet.setDrawCircles(false)
        lineSet.setDrawIcons(true)
        //线模式为圆滑曲线（默认折线）CUBIC_BEZIER
        lineSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        lineSet.color = MyApplication.context.resources.getColor(R.color.theme_color)
        return lineSet
    }

    /**
     * 更新数据
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun notifyChartData(lineChart: LineChart, xData: Float, yData: Float) {
        var data = lineChart.data
        if (data == null) {
            data = LineData()
            lineChart.data = data
        }
        var set = data.getDataSetByIndex(0)
        if (set == null) {
            set = createSet()
            data.addDataSet(set)
        }
        val randomDataSetIndex = (Math.random() * data.dataSetCount).toInt()
        data.addEntry(Entry(xData, yData), randomDataSetIndex)
        data.notifyDataChanged()
        lineChart.notifyDataSetChanged()
        lineChart.invalidate()
    }

    /**
     * 删除数据
     */
    private fun removeChartData(lineChart: LineChart) {
        val data: LineData = lineChart.getData()
        if (data != null) {
            val set = data.getDataSetByIndex(0)
            if (set != null) {
                val e = set.getEntryForXValue((set.entryCount - 1).toFloat(), Float.NaN)
                data.removeEntry(e, 0)
                // or remove by index
                // mData.removeEntryByXValue(xIndex, dataSetIndex);
                data.notifyDataChanged()
                lineChart.notifyDataSetChanged()
                lineChart.invalidate()
            }
        }
    }

    /**
     * 回放
     * lineChartBX
     */
    fun backPlay(lineChartAnaBX: LineChart, lineChartAnaBZ: LineChart, lineChartAna: LineChart) {
        if (DataManagement.returnBXList().isNotEmpty()) {
            //将数据添加到图表中
            lineChartAnaBX.clear()
            var lineBXSet = LineDataSet(landBXList, "BX")
            lineBXSet.setDrawValues(false)
            lineBXSet.setDrawCircles(false)
            lineBXSet.mode = LineDataSet.Mode.CUBIC_BEZIER
            lineBXSet.color = MyApplication.context.resources.getColor(R.color.theme_color)
            //将数据集添加到数据 ChartData 中
            val lineDataBX = LineData(lineBXSet)
            lineChartAnaBX.data = lineDataBX
            lineChartAnaBX.animateX(2000)
            lineChartAnaBX.notifyDataSetChanged()
            lineChartAnaBX.invalidate()

            lineChartAnaBZ.clear()
            var lineBZSet = LineDataSet(landBZList, "BX")
            lineBZSet.setDrawValues(false)
            lineBZSet.setDrawCircles(false)
            lineBZSet.mode = LineDataSet.Mode.CUBIC_BEZIER
            lineBZSet.color = MyApplication.context.resources.getColor(R.color.theme_color)
            //将数据集添加到数据 ChartData 中
            val lineDataBZ = LineData(lineBZSet)
            lineChartAnaBZ.data = lineDataBZ
            lineChartAnaBZ.notifyDataSetChanged()
            lineChartAnaBZ.invalidate()
            lineChartAnaBZ.animateX(2000)

            lineChartAna.clear()
            var lineSet = LineDataSet(landList, "BX")
            lineSet.setDrawValues(false)
            lineSet.setDrawCircles(false)
            lineSet.mode = LineDataSet.Mode.CUBIC_BEZIER
            lineSet.color = MyApplication.context.resources.getColor(R.color.theme_color)
            //将数据集添加到数据 ChartData 中
            val lineData = LineData(lineSet)
            lineChartAna.data = lineData
            lineChartAna.notifyDataSetChanged()
            lineChartAna.invalidate()
            lineChartAna.animateX(2000)
        }
    }

    /**
     * 复位
     * lineChartBX
     */
    fun reset(lineChartBX: LineChart, lineChartBZ: LineChart, lineChart: LineChart) {
        if (DataManagement.returnBXList().isNotEmpty()) {
            //将数据添加到图表中
            lineChartBX.clear()
            var lineBXSet = LineDataSet(landBXList, "BX")
            lineBXSet.setDrawValues(false)
            lineBXSet.setDrawCircles(false)
            lineBXSet.mode = LineDataSet.Mode.CUBIC_BEZIER
            lineBXSet.color = MyApplication.context.resources.getColor(R.color.theme_color)
            //将数据集添加到数据 ChartData 中
            val lineDataBX = LineData(lineBXSet)
            lineChartBX.data = lineDataBX
//            lineChartAnaBX.animateX(2000)
//            lineChartBX.setScaleMinima(1F,1F)
            lineChartBX.fitScreen()
            lineChartBX.notifyDataSetChanged()
            lineChartBX.invalidate()

            lineChartBZ.clear()
            var lineBZSet = LineDataSet(landBZList, "BX")
            lineBZSet.setDrawValues(false)
            lineBZSet.setDrawCircles(false)
            lineBZSet.mode = LineDataSet.Mode.CUBIC_BEZIER
            lineBZSet.color = MyApplication.context.resources.getColor(R.color.theme_color)
            //将数据集添加到数据 ChartData 中
            val lineDataBZ = LineData(lineBZSet)
            lineChartBZ.data = lineDataBZ
//            lineChartBX.setScaleMinima(1F,1F)
            lineChartBZ.fitScreen()
            lineChartBZ.notifyDataSetChanged()
            lineChartBZ.invalidate()
//            lineChartAnaBZ.animateX(2000)

            lineChart.clear()
            var lineSet = LineDataSet(landList, "BX")
            lineSet.setDrawValues(false)
            lineSet.setDrawCircles(false)
            lineSet.mode = LineDataSet.Mode.CUBIC_BEZIER
            lineSet.color = MyApplication.context.resources.getColor(R.color.theme_color)
            //将数据集添加到数据 ChartData 中
            val lineData = LineData(lineSet)
            lineChart.data = lineData
//            lineChartBX.setScaleMinima(1F,1F)
            lineChart.fitScreen()
            lineChart.notifyDataSetChanged()
            lineChart.invalidate()
//            lineChartAna.animateX(2000)
        }
    }

    /**
     * Refresh刷新
     */
    fun readRefreshData(lineChartBX: LineChart, lineChartBZ: LineChart, lineChart: LineChart) {
        //将数据添加到图表中
        landBXList.clear()
        landBZList.clear()
        landList.clear()
        lineChartBX.clear()
        lineChartBZ.clear()
        lineChart.clear()
        lineChartBX.notifyDataSetChanged()
        lineChartBX.invalidate()
        lineChartBZ.notifyDataSetChanged()
        lineChartBZ.invalidate()
        lineChart.notifyDataSetChanged()
        lineChart.invalidate()
    }

    /**
     * 框选
     */
    fun framePlay(startIndex:Int, endIndex:Int, lineChartAnaBX: LineChart, lineChartAnaBZ: LineChart, lineChartAna: LineChart) {
        if (DataManagement.returnBXList().isNotEmpty()) {
            //将数据添加到图表中
            lineChartAnaBX.clear()
            var lineBXSet = LineDataSet(if (startIndex<endIndex) landBXList.subList(startIndex,endIndex) else landBXList.subList(endIndex,startIndex), "BX")
            lineBXSet.setDrawValues(false)
            lineBXSet.setDrawCircles(false)
            lineBXSet.mode = LineDataSet.Mode.CUBIC_BEZIER
            lineBXSet.color = MyApplication.context.resources.getColor(R.color.theme_color)
            //将数据集添加到数据 ChartData 中
            val lineDataBX = LineData(lineBXSet)
            lineChartAnaBX.data = lineDataBX
//            lineChartAnaBX.animateX(2000)
            lineChartAnaBX.notifyDataSetChanged()
            lineChartAnaBX.invalidate()

            lineChartAnaBZ.clear()
            var lineBZSet = LineDataSet(if (startIndex<endIndex) landBZList.subList(startIndex,endIndex) else landBZList.subList(endIndex,startIndex), "BX")
            lineBZSet.setDrawValues(false)
            lineBZSet.setDrawCircles(false)
            lineBZSet.mode = LineDataSet.Mode.CUBIC_BEZIER
            lineBZSet.color = MyApplication.context.resources.getColor(R.color.theme_color)
            //将数据集添加到数据 ChartData 中
            val lineDataBZ = LineData(lineBZSet)
            lineChartAnaBZ.data = lineDataBZ
            lineChartAnaBZ.notifyDataSetChanged()
            lineChartAnaBZ.invalidate()
//            lineChartAnaBZ.animateX(2000)

            lineChartAna.clear()
            var lineSet = LineDataSet(if (startIndex<endIndex) landList.subList(startIndex,endIndex) else landList.subList(endIndex,startIndex), "BX")
            lineSet.setDrawValues(false)
            lineSet.setDrawCircles(false)
            lineSet.mode = LineDataSet.Mode.CUBIC_BEZIER
            lineSet.color = MyApplication.context.resources.getColor(R.color.theme_color)
            //将数据集添加到数据 ChartData 中
            val lineData = LineData(lineSet)
            lineChartAna.data = lineData
            lineChartAna.notifyDataSetChanged()
            lineChartAna.invalidate()
        }
    }
}
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
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import kotlinx.android.synthetic.main.fragment_analysts.*

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
    fun readMeterData(readData: String, lineChartBX: LineChart, lineChartBZ: LineChart, lineChart: LineChart) {
        var xData = BinaryChange.hexTofloat(readData.substring(0, 8))
        var yBXData = BinaryChange.hexTofloat(readData.substring(8, 16))
        var yBZData = BinaryChange.hexTofloat(readData.substring(16, 24))
//        landBXList.add(Entry(xData, yBXData))
//        landBZList.add(Entry(xData, yBZData))
//        landList.add(Entry(yBXData, yBZData))
        DataManagement.addBXEntry(xData, yBXData)
        DataManagement.addBZEntry(xData, yBZData)
        DataManagement.addEntry(yBXData, yBZData)


        notifyChartData(lineChartBX, xData, yBXData)
        notifyChartData(lineChartBZ, xData, yBZData)
        notifyChartData(lineChart, yBXData, yBZData)
        LogUtil.e("TAG", "${DataManagement.returnBXList().size}----${DataManagement.returnBZList().size}----${DataManagement.returnList().size}")
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
     * Refresh刷新
     */
    fun readRefreshData(lineChartBX: LineChart, lineChartBZ: LineChart, lineChart: LineChart) {
        //将数据添加到图表中
        landBXList.clear()
        landBZList.clear()
        landList.clear()
//        lineChartBX.notifyDataSetChanged()
//        lineChartBX.invalidate()
//        lineChartBZ.notifyDataSetChanged()
//        lineChartBZ.invalidate()
//        lineChart.notifyDataSetChanged()
//        lineChart.invalidate()
        lineChartBX.clear()
        lineChartBZ.clear()
        lineChart.clear()
    }
}
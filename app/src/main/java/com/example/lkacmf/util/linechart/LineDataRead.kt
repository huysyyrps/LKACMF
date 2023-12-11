package com.example.lkacmf.util.linechart

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.DashPathEffect
import com.afollestad.materialdialogs.MaterialDialog
import com.example.lkacmf.MyApplication
import com.example.lkacmf.R
import com.example.lkacmf.activity.MainActivity
import com.example.lkacmf.util.BinaryChange
import com.example.lkacmf.util.LogUtil
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet

object LineDataRead {
    var deviceCode = ""
    var activatCode = ""
    private lateinit var deviceDate: String
    private lateinit var context: MainActivity
    private lateinit var dialog: MaterialDialog
    var landBXList: ArrayList<Entry> = ArrayList()
    var landBXRemoveList: ArrayList<Entry> = ArrayList()
    var landBZList: ArrayList<Entry> = ArrayList()
    var landBZRemoveList: ArrayList<Entry> = ArrayList()
    var landList: ArrayList<Entry> = ArrayList()
    var landRemoveList: ArrayList<Entry> = ArrayList()

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
        landBXList.add(Entry(xData, yBXData))
        landBZList.add(Entry(xData, yBZData))
        landList.add(Entry(yBXData, yBZData))
        notifyChartData(lineChartBX, xData, yBXData)
        notifyChartData(lineChartBZ, xData, yBZData)
        notifyChartData(lineChart, yBXData, yBZData)
        LogUtil.e("TAG", "${landBXList.size}----${landBZList.size}----${landList.size}")
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
    fun backPlay(chart: LineChart, lineChartBZ: LineChart, lineChart: LineChart) {
        if (landBXList.isNotEmpty()) {
//            chart.clear()


//            chart.lineData.dataSets.clear()
//            chart.lineData.clearValues()
            chart.data = null
//            chart.data.notifyDataChanged()
            chart.notifyDataSetChanged()
            chart.invalidate()


            val set1: LineDataSet
            if (chart.getData() != null && chart.getData().getDataSetCount() > 0) {
                set1 = chart.getData().getDataSetByIndex(0) as LineDataSet
                set1.setValues(landBXList)
                set1.notifyDataSetChanged()
                chart.getData().notifyDataChanged()
                chart.notifyDataSetChanged()
            } else {
                var data = lineChart.data
                if (data == null) {
                    data = LineData()
                    lineChart.data = data
                }
                var set = data.getDataSetByIndex(0)
                if (set == null) {
                    set = LineDataSet(landBXList, "DataSet 1")//createSet()
                    data.addDataSet(set)
                            chart.setData(data)
                }


                // create a dataset and give it a type
//                set1 = LineDataSet(landBXList, "DataSet 1")
//                // draw dashed line
//                val dataSets = java.util.ArrayList<ILineDataSet>()
//                dataSets.add(set1) // add the data sets
//                // create a data object with the data sets
//                val data = LineData(dataSets)
//                // set data
//                chart.setData(data)
            }
            chart.animateX(1500)

            // draw points over time
//            setData(45, 180f, chart)
//            chart.animateX(1500)
//            val l: Legend = chart.getLegend()
//            l.form = LegendForm.LINE


//            //将数据添加到图表中
//            lineChartBX.lineData.clearValues()
//            lineChartBX.clear()
//            lineChartBX.invalidate()
//            var lineBXSet = LineDataSet(landBXList, "BX")
//            lineBXSet.setDrawValues(false)
//            lineBXSet.setDrawCircles(false)
//            lineBXSet.mode = LineDataSet.Mode.CUBIC_BEZIER
//            lineBXSet.color = MyApplication.context.resources.getColor(R.color.theme_color)
//            //将数据集添加到数据 ChartData 中
//            val dataSets = java.util.ArrayList<ILineDataSet>()
//            dataSets.add(lineBXSet) // add the data sets
//            val lineDataBX = LineData(dataSets)
//            lineChartBX.data = lineDataBX
//            lineChartBX.animateX(3000)


//            var lineBXSet = LineDataSet(landBXList, "BX")
//            lineBXSet.setDrawValues(false)
//            lineBXSet.setDrawCircles(false)
//            lineBXSet.mode = LineDataSet.Mode.CUBIC_BEZIER
//            lineBXSet.color = MyApplication.context.resources.getColor(R.color.theme_color)
//            //将数据集添加到数据 ChartData 中
//            val lineDataBX = LineData(lineBXSet)
//            lineChartBX.data = lineDataBX
//            lineChartBX.animateX(2000)
//            lineChartBX.notifyDataSetChanged()
//            lineChartBX.invalidate()

//
//            lineChartBZ.clear()
//            var lineBZSet = LineDataSet(landBZList, "BX")
//            lineBZSet.setDrawValues(false)
//            lineBZSet.setDrawCircles(false)
//            lineBZSet.mode = LineDataSet.Mode.CUBIC_BEZIER
//            lineBZSet.color = MyApplication.context.resources.getColor(R.color.theme_color)
//            //将数据集添加到数据 ChartData 中
//            val lineDataBZ = LineData(lineBZSet)
//            lineChartBZ.data = lineDataBZ
//            lineChartBZ.notifyDataSetChanged()
//            lineChartBZ.invalidate()
//            lineChartBZ.animateX(2000)
//
//            lineChart.clear()
//            var lineSet = LineDataSet(landList, "BX")
//            lineSet.setDrawValues(false)
//            lineSet.setDrawCircles(false)
//            lineSet.mode = LineDataSet.Mode.CUBIC_BEZIER
//            lineSet.color = MyApplication.context.resources.getColor(R.color.theme_color)
//            //将数据集添加到数据 ChartData 中
//            val lineData = LineData(lineSet)
//            lineChart.data = lineData
//            lineChart.notifyDataSetChanged()
//            lineChart.invalidate()
//            lineChart.animateX(2000)
        }
    }

    private fun setData(count: Int, range: Float, chart: LineChart) {
        val values = java.util.ArrayList<Entry>()
        for (i in 0 until 500) {
            val `val` = (Math.random() * range).toFloat() - 30
            values.add(Entry(i.toFloat(), `val`))
        }
        val set1: LineDataSet
        if (chart.getData() != null && chart.getData().getDataSetCount() > 0) {
            set1 = chart.getData().getDataSetByIndex(0) as LineDataSet
            set1.values = values
            set1.notifyDataSetChanged()
            chart.getData().notifyDataChanged()
            chart.notifyDataSetChanged()
        } else {
            // create a dataset and give it a type
            set1 = LineDataSet(values, "DataSet 1")
            set1.setDrawIcons(false)

            // draw dashed line
            set1.enableDashedLine(10f, 5f, 0f)

            // black lines and points
            set1.color = Color.BLACK
            set1.setCircleColor(Color.BLACK)

            // line thickness and point size
            set1.lineWidth = 1f
            set1.circleRadius = 3f

            // draw points as solid circles
            set1.setDrawCircleHole(false)

            // customize legend entry
            set1.formLineWidth = 1f
            set1.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
            set1.formSize = 15f

            // text size of values
            set1.valueTextSize = 9f

            // draw selection line as dashed
            set1.enableDashedHighlightLine(10f, 5f, 0f)

            // set the filled area
            set1.setDrawFilled(true)
            set1.fillFormatter = IFillFormatter { dataSet, dataProvider -> chart.getAxisLeft().getAxisMinimum() }

            // set color of filled area
            val dataSets = java.util.ArrayList<ILineDataSet>()
            dataSets.add(set1) // add the data sets

            // create a data object with the data sets
            val data = LineData(dataSets)

            // set data
            chart.setData(data)
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
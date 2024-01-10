package com.example.lkacmf.serialport

import android.graphics.Rect
import com.example.lkacmf.util.linechart.LineDataRead
import com.github.mikephil.charting.data.Entry

object DataManagement {
    //判断折线图是否可以被框选
    var screenState = false

    var landBXList: ArrayList<Entry> = ArrayList()
    var landBZList: ArrayList<Entry> = ArrayList()
    var landList: ArrayList<Entry> = ArrayList()
    var punctationList: ArrayList<Int> = ArrayList()
    var frameRect: Rect = Rect(0, 0, 0, 0)

    fun addBXEntry(xData:Float, yBXData:Float){
        landBXList.add(Entry(xData, yBXData))
    }
    fun addBZEntry(xData:Float, yBZData:Float){
        landBZList.add(Entry(xData, yBZData))
    }
    fun addEntry(yBXData:Float, yBZData:Float){
        landList.add(Entry(yBXData, yBZData))
    }

    fun returnBXList():ArrayList<Entry>{
        return landBXList
    }
    fun returnBZList():ArrayList<Entry>{
        return landBZList
    }
    fun returnList():ArrayList<Entry>{
        return landList
    }
}
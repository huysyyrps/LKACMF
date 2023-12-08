package com.example.lkacmf.util

import android.os.Build
import androidx.annotation.RequiresApi
import java.util.*

 object DateUtil {
    var calendar: Calendar = Calendar.getInstance() // 创建一个Calendar对象
    var year: Int = calendar.get(Calendar.YEAR) // 年份
    var month: Int = calendar.get(Calendar.MONTH) + 1 // 月份（注意要加上1）
    var dayOfMonth: Int = calendar.get(Calendar.DAY_OF_MONTH) // 日期
    var hourOfDay: Int = calendar.get(Calendar.HOUR_OF_DAY) // 小时数
    var minute: Int = calendar.get(Calendar.MINUTE) // 分钟数
    var second: Int = calendar.get(Calendar.SECOND)

    @RequiresApi(Build.VERSION_CODES.O)
    fun getYearTop():String{
        return BinaryChange.tenToHex(year.toString().substring(0,2).toInt())
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun getYearBotton():String{
        return BinaryChange.tenToHex(year.toString().substring(2,4).toInt())
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun getMonth():String{
        var month = BinaryChange.tenToHex(month)
        var hexMonth = if (month.length<2) {
            "0$month"
        }else{
            month
        }
        return hexMonth.uppercase(Locale.getDefault())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDay():String{
        var day = BinaryChange.tenToHex(dayOfMonth)
        var hexDay = if (day.length<2) {
            "0$day"
        }else{
            day
        }
        return hexDay.uppercase(Locale.getDefault())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getHour():String{
        var hour = BinaryChange.tenToHex(hourOfDay)
        var hexHour = if (hour.length<2) {
            "0$hour"
        }else{
            hour
        }
        return hexHour.uppercase(Locale.getDefault())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getMinute():String{
        var minute = BinaryChange.tenToHex(minute)
        var hexMinute = if (minute.length<2) {
            "0$minute"
        }else{
            minute
        }
        return hexMinute.uppercase(Locale.getDefault())
    }

}
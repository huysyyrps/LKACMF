package com.example.lkacmf.serialport

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.lkacmf.util.BinaryChange
import com.example.lkacmf.util.Constant
import com.example.lkacmf.util.DateUtil
import com.example.lkacmf.util.sp.BaseSharedPreferences

object SerialPortDataMake {
    /**
     * 设备是否激活
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun  haveActivationData():String{
        var data = "${Constant.ACTIVATIONHEADER}${Constant.DATAHAVEACTIVATIONINFO}${DateUtil.getYearTop()}" +
                "${DateUtil.getYearBotton()}${DateUtil.getMonth()}${DateUtil.getDay()}${DateUtil.getHour()}${DateUtil.getMinute()}"
        var checksum = BinaryChange.proofData(data)
        return "$data$checksum"
    }

    /**
     * 激活
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun  activationData(activationCode:String):String{
        var data = "${Constant.ACTIVATIONHEADER}${Constant.DATAACTIVATIONINFO}${activationCode}"
        var checksum = BinaryChange.proofData(data)
        return "$data$checksum"
    }

    /**
     * 授权
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun empowerData(empowerCode:String):String{
        var data = "${Constant.ACTIVATIONHEADER}${Constant.SETTINGID}${empowerCode}"
        var checksum = BinaryChange.proofData(data)
        return "$data$checksum"
    }

    /**
     * 设置
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun  settingData():String{
        var data = "${Constant.DATASETTINGHEADER}${Constant.SETTINGID}${BaseSharedPreferences.get("userEncode","")}" +
                "${BaseSharedPreferences.get("probeNumb","")}${BaseSharedPreferences.get("probeRate","")}" +
                "${BaseSharedPreferences.get("currentWave","")}"
        var checksum = BinaryChange.proofData(data)
        return "$data$checksum"
    }

    /**
     * 按键操作
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun  operateData(state:String):String{
        var data = "${Constant.DATASETTINGHEADER}${Constant.OPERATEID}${DateUtil.getYearTop()}" +
                "${DateUtil.getYearBotton()}${DateUtil.getMonth()}${DateUtil.getDay()}${DateUtil.getHour()}${DateUtil.getMinute()}$state"
        var checksum = BinaryChange.proofData(data)
        return "$data$checksum"
    }
}
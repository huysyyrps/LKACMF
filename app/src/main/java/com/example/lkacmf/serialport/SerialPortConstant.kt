package com.example.lkacmf.serialport

import android.app.Activity
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.lkacmf.R
import com.example.lkacmf.util.BinaryChange
import com.example.lkacmf.util.Constant
import com.example.lkacmf.util.LogUtil
import com.example.lkacmf.util.showToast
import me.f1reking.serialportlib.SerialPortHelper
import me.f1reking.serialportlib.listener.IOpenSerialPortListener
import me.f1reking.serialportlib.listener.ISerialPortDataListener
import me.f1reking.serialportlib.listener.Status
import java.io.File
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate


object SerialPortConstant {
    val timer = Timer()
    private val mSerialPortHelper: SerialPortHelper by lazy { SerialPortHelper() }
    fun getSerialPortHelper(activity: Activity):SerialPortHelper{
        mSerialPortHelper.port = Constant.PORT
        mSerialPortHelper!!.setIOpenSerialPortListener(object : IOpenSerialPortListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onSuccess(device: File) {
                activity.runOnUiThread {
                    LogUtil.e("SerialPortConstant",device.path + " :串口打开成功")
                    timer.scheduleAtFixedRate(0, 1000) {
                        mSerialPortHelper.sendTxt(SerialPortDataMake.haveActivationData())
                    }
                }
            }
            override fun onFail(device: File, status: Status?) {
                activity.runOnUiThread {
                    when (status) {
                        Status.NO_READ_WRITE_PERMISSION ->
                            "${device.path}${activity.resources.getString(R.string.no_rw_permission)}".showToast(activity)
                        Status.OPEN_FAIL ->
                            "${device.path}${activity.resources.getString(R.string.seriaport_open_fail)}".showToast(activity)
                        else -> {
                            "${device.path}${activity.resources.getString(R.string.seriaport_open_fail)}".showToast(activity)
                        }

                    }
                }
            }
        })
        mSerialPortHelper.open()
        return mSerialPortHelper
    }

    fun backData() {
        if (mSerialPortHelper.isOpen) {
            mSerialPortHelper.setISerialPortDataListener(object : ISerialPortDataListener {
                override fun onDataReceived(bytes: ByteArray?) {
                    var receivedData = BinaryChange.byteToHexString(bytes!!)
                    LogUtil.e("TAG", receivedData)
                }

                override fun onDataSend(bytes: ByteArray?) {
                    Log.e("ConfigFragment", "onDataSend: " + bytes?.let { BinaryChange.byteToHexString(it) })
                }
            })
        }
    }

    fun serialPortHelperIsOpen(): Boolean {
        return mSerialPortHelper.isOpen
    }

    @JvmName("getMSerialPortHelper1")
    fun getMSerialPortHelper(activity: Activity): SerialPortHelper {
        if (mSerialPortHelper != null && mSerialPortHelper.isOpen)
            return mSerialPortHelper
        else
            return getSerialPortHelper(activity)
    }

    fun close() {
        if (mSerialPortHelper != null) {
            mSerialPortHelper.close()
        }
    }
}
package com.example.lkacmf.serialport

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.lkacmf.MyApplication
import com.example.lkacmf.R
import com.example.lkacmf.activity.ConfigurationActivity
import com.example.lkacmf.fragment.CalibrationFragment
import com.example.lkacmf.util.BinaryChange
import com.example.lkacmf.util.Constant
import com.example.lkacmf.util.LogUtil
import com.example.lkacmf.util.linechart.LineDataRead
import com.example.lkacmf.util.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.f1reking.serialportlib.SerialPortHelper
import me.f1reking.serialportlib.listener.IOpenSerialPortListener
import me.f1reking.serialportlib.listener.ISerialPortDataListener
import me.f1reking.serialportlib.listener.Status
import java.io.File
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate

@RequiresApi(Build.VERSION_CODES.O)
object SerialPortConstant {
    val timer = Timer()
    private val mSerialPortHelper: SerialPortHelper by lazy { SerialPortHelper() }
    fun getSerialPortHelper(): SerialPortHelper {
        if (mSerialPortHelper.isOpen) {
            return mSerialPortHelper
        } else {
            mSerialPortHelper.port = Constant.PORT
            mSerialPortHelper!!.setIOpenSerialPortListener(object : IOpenSerialPortListener {
                override fun onSuccess(device: File) {
                    LogUtil.e("SerialPortConstant", device.path + " :串口打开成功")
                    timer.scheduleAtFixedRate(0, 1000) {
                        mSerialPortHelper.sendTxt(SerialPortDataMake.haveActivationData())
                    }
                }

                override fun onFail(device: File, status: Status?) {
                    when (status) {
                        Status.NO_READ_WRITE_PERMISSION ->
                            "${device.path}${MyApplication.context.resources.getString(R.string.no_rw_permission)}".showToast(MyApplication.context)
                        Status.OPEN_FAIL ->
                            "${device.path}${MyApplication.context.resources.getString(R.string.seriaport_open_fail)}".showToast(MyApplication.context)
                        else -> {
                            "${device.path}${MyApplication.context.resources.getString(R.string.seriaport_open_fail)}".showToast(MyApplication.context)
                        }

                    }
                }
            })
            mSerialPortHelper.open()
            return mSerialPortHelper
        }
    }

    fun <T> serialPortDataListener(activity: T) {
        mSerialPortHelper.setISerialPortDataListener(object : ISerialPortDataListener {
            override fun onDataReceived(bytes: ByteArray?) {
                var receivedData = BinaryChange.byteToHexString(bytes!!)
                CoroutineScope(Dispatchers.Main).launch {
                    //设备状态
                    if (receivedData.startsWith("B000") && receivedData.length == 48) {
                        (activity as ConfigurationActivity).getBackData(receivedData)
                    }
                    //设置
                    if (receivedData.startsWith("B102") && receivedData.length == 18) {
                        if (BinaryChange.proofData(receivedData.substring(0, 12)) == receivedData.subSequence(12, 14)) {
                            (activity as ConfigurationActivity).getBackData(receivedData)
                        }
                    }
                    //参数
                    if (receivedData.startsWith("B100") && receivedData.length == 24) {
                        if (BinaryChange.proofData(receivedData.substring(0, 18)) == receivedData.subSequence(18, 20)) {
                            (activity as CalibrationFragment).getBackData(receivedData)
                        }
                    }
                    //数据
                    if (receivedData.startsWith("B101") && receivedData.length == 40) {
                        (activity as CalibrationFragment).getBackData(receivedData)
                    }
                }
            }

            override fun onDataSend(bytes: ByteArray?) {
                Log.e("ConfigFragment", "onDataSend: " + bytes?.let { BinaryChange.byteToHexString(it) })
            }
        })
    }

//    fun sendData(data: String) {
//        if (mSerialPortHelper.isOpen) {
//            mSerialPortHelper.sendTxt(data)
//        }
//    }

    fun serialPortHelperIsOpen(): Boolean {
        return mSerialPortHelper.isOpen
    }

    fun close() {
        if (mSerialPortHelper != null) {
            mSerialPortHelper.close()
        }
    }
}
package com.example.lkacmf.fragment

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.lkacmf.R
import com.example.lkacmf.activity.ChangeConfigurationActivity
import com.example.lkacmf.activity.MainActivity
import com.example.lkacmf.databinding.FragmentCalibrationBinding
import com.example.lkacmf.serialport.DataManagement
import com.example.lkacmf.serialport.SerialPortConstant
import com.example.lkacmf.serialport.SerialPortDataMake
import com.example.lkacmf.util.BinaryChange
import com.example.lkacmf.util.LogUtil
import com.example.lkacmf.util.PopupPositionCallBack
import com.example.lkacmf.util.dialog.DialogSureCallBack
import com.example.lkacmf.util.dialog.DialogUtil
import com.example.lkacmf.util.linechart.LineChartListener
import com.example.lkacmf.util.linechart.LineChartListener.endIndex
import com.example.lkacmf.util.linechart.LineChartListener.startIndex
import com.example.lkacmf.util.linechart.LineChartSetting
import com.example.lkacmf.util.linechart.LineDataRead
import com.example.lkacmf.util.popup.CustomBubbleAttachPopup
import com.example.lkacmf.util.popup.PopupListData
import com.example.lkacmf.util.showToast
import com.example.lkacmf.util.sp.BaseSharedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.f1reking.serialportlib.listener.ISerialPortDataListener
import kotlin.math.pow

/**
 * 校准
 */
@RequiresApi(Build.VERSION_CODES.O)
class CalibrationFragment : Fragment(), View.OnClickListener {
    var _binding: FragmentCalibrationBinding? = null
    val binding get() = _binding!!

    //判断折线图是否可以被框选
    var screenState = false

    //点击开始按钮之前停止按钮是否被选中
    var upStartDtate = "stop"

    //是否标记
    var punctationState = false
    val mSerialPortHelper = SerialPortConstant.getSerialPortHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentCalibrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStart() {
        super.onStart()
        binding.btnStart.setOnClickListener(this)
        binding.btnSuspend.setOnClickListener(this)
        binding.btnStop.setOnClickListener(this)
        binding.btnRefresh.setOnClickListener(this)
        binding.btnScreen.setOnClickListener(this)
        binding.btnCount.setOnClickListener(this)
        binding.btnReset.setOnClickListener(this)
        binding.ivLeft.vtvSetting.setOnClickListener(this)
        binding.btnStopCalibration.setOnClickListener(this)
        binding.btnPunctation.setOnClickListener(this)
        binding.btnDirection.setOnClickListener(this)
        binding.btnConfiguration.setOnClickListener(this)

        mSerialPortHelper.sendTxt(SerialPortDataMake.operateData("00"))
        mSerialPortHelper.setISerialPortDataListener(object : ISerialPortDataListener {
            override fun onDataReceived(bytes: ByteArray?) {
                var receivedData = BinaryChange.byteToHexString(bytes!!)
                LogUtil.e("TAGCalibrationFragment", receivedData)
                CoroutineScope(Dispatchers.Main).launch {
                    //参数
                    if (receivedData.length == 24) {
                        getBackData(receivedData)
                    }
                    //数据
                    if (receivedData.length == 40) {
                        getBackData(receivedData)
                    }
                }
            }

            override fun onDataSend(bytes: ByteArray?) {
                Log.e("TAGCalibrationFragment", "onDataSend: " + bytes?.let { BinaryChange.byteToHexString(it) })
            }
        })

        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId==R.id.btnScreen){
                screenState = true
                binding.lineChartBX.mCanScale = false
            }else{
                screenState = false
                binding.lineChartBX.mCanScale = true
            }
        }
        LineChartSetting.SettingLineChart(requireActivity(), binding.lineChartBX, showX = true, scale = true)
        LineChartSetting.SettingLineChart(requireActivity(), binding.lineChartBZ, showX = true, scale = true)
        LineChartSetting.SettingLineChart(requireActivity(), binding.lineChart, showX = true, scale = true)
        LineChartListener.lineChartSetListener(binding.lineChartBX, binding.lineChartBX, binding.lineChartBZ, binding.lineChart, this)
        LineChartListener.lineChartSetListener(binding.lineChartBZ, binding.lineChartBX, binding.lineChartBZ, binding.lineChart, this)
        LineChartListener.lineChartSetListener(binding.lineChart, binding.lineChartBX, binding.lineChartBZ, binding.lineChart, this)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnStart->{
                LineDataRead.reset(binding.lineChartBX, binding.lineChartBZ, binding.lineChart)
                if (upStartDtate=="stop"){
                    LineDataRead.readRefreshData(binding.lineChartBX, binding.lineChartBZ, binding.lineChart)
                }
                upStartDtate = "start"
                binding.btnStart.visibility = View.GONE
                binding.btnSuspend.visibility = View.VISIBLE
                mSerialPortHelper.sendTxt(SerialPortDataMake.operateData("01"))
            }
            R.id.btnStop -> {
                upStartDtate = "stop"
                mSerialPortHelper.sendTxt( SerialPortDataMake.operateData("00"))
            }
            R.id.btnSuspend->{
                upStartDtate = "suspend"
                binding.btnStart.isChecked = true
                binding.btnStart.visibility = View.VISIBLE
                binding.btnSuspend.visibility = View.GONE
                mSerialPortHelper.sendTxt( SerialPortDataMake.operateData("02"))
            }
            R.id.btnRefresh->{
                mSerialPortHelper.sendTxt( SerialPortDataMake.operateData("00"))
                mSerialPortHelper.sendTxt( SerialPortDataMake.operateData("03"))
                LineDataRead.readRefreshData(binding.lineChartBX, binding.lineChartBZ, binding.lineChart)
            }
            R.id.btnScreen->{
                if(upStartDtate == "start"){
                    "请先暂停".showToast(requireContext())
                    binding.btnSuspend.isChecked = true
                }else{
                    mSerialPortHelper.sendTxt( SerialPortDataMake.operateData("02"))
                    Thread.sleep(150)
                    binding.btnScreen.isChecked = true
                }
            }
            R.id.btnCount->{
                if (startIndex==0|| endIndex==0){
                    "请框选区域".showToast(requireContext())
                    return
                }
                var countList = if (startIndex< endIndex){
                    DataManagement.landBXList.subList(startIndex, endIndex)
                }else{
                    DataManagement.landBXList.subList(endIndex, startIndex)
                }

                //获取数组最大值下标
                var maxTopValue = countList[0].y
                var maxBottonValue = countList[0].y
                var minValue = countList[0].y
                var minIndex = 0
                for (i in 0 until countList.size) {
                    var current = countList[i].y
                    if (current < minValue) {
                        minValue = current
                        minIndex = i
                    }
                }
                var countTopList = countList.subList(0, minIndex)
                for (i in 0 until countTopList.size) {
                    var current = countTopList[i].y
                    if (current > maxTopValue) {
                        maxTopValue = current;
                    }
                }
                var countBottonList = countList.subList(minIndex, countList.size-1)
                for (i in 0 until countBottonList.size) {
                    var current = countBottonList[i].y
                    if (current > maxBottonValue) {
                        maxBottonValue = current;
                    }
                }
                var bx0 = (maxTopValue+maxBottonValue)/2
                var sbx = 1-minValue/bx0
                var dx = 0.8762* sbx.pow(3)-3.634*sbx.pow(2)+6.174*sbx+0.04038
                val formattedDx = String.format("%.2f", dx)
                binding.tvDepth.text = formattedDx
                binding.tvDepthRatio.text = String.format("%.2f", dx / 3)
                BaseSharedPreferences.put("depthRatio",String.format("%.2f", dx / 3))
//                var length = if (maxTopValue>maxBottonValue){
//                    1.02*(maxTopValue-minValue)
//                }else{
//                    1.02*(maxBottonValue-minValue)
//                }
//                val formattedLength = String.format("%.2f", length)
//                binding.tvLength.text = "$formattedLength"
//                LogUtil.e("TAG","$formattedDx----$formattedLength")
            }
            R.id.btnReset -> {
                LineDataRead.reset(binding.lineChartBX, binding.lineChartBZ, binding.lineChart)
            }
            R.id.btnStopCalibration -> {
                MainActivity.actionStart(requireContext())
                requireActivity().finish()
            }
            R.id.btnPunctation -> {
                punctationState = true
            }
            R.id.vtvSetting->{
                DialogUtil.settingDialog(requireActivity(), object: DialogSureCallBack {
                    @RequiresApi(Build.VERSION_CODES.O)
                    override fun sureCallBack(data: String) {
                        mSerialPortHelper.sendTxt(SerialPortDataMake.settingData())
                    }

                    override fun cancelCallBack(data: String) {
                    }
                })
           }
            //路径
            R.id.btnDirection -> {
                com.lxj.xpopup.XPopup.Builder(requireContext())
                    .hasShadowBg(false)
                    .isTouchThrough(true)
                    .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                    .atView(binding.btnDirection)
                    .isCenterHorizontal(true)
                    .hasShadowBg(false) // 去掉半透明背景
                    .isClickThrough(true)
                    .asCustom(CustomBubbleAttachPopup(requireContext(),"popup", object : PopupPositionCallBack {
                        override fun backPosition(index: Int) {
//                            PopupListData.setPopupListData()[index].title.showToast(requireContext())
                            upStartDtate = "suspend"
                            binding.btnStart.isChecked = true
                            binding.btnStart.visibility = View.VISIBLE
                            binding.btnSuspend.visibility = View.GONE
                            mSerialPortHelper.sendTxt( SerialPortDataMake.operateData("02"))
                            mSerialPortHelper.sendTxt( SerialPortDataMake.operateData("03"))
                            LineDataRead.readRefreshData(binding.lineChartBX, binding.lineChartBZ, binding.lineChart)
                        }

                    }))
            }
            //更换配置
            R.id.btnConfiguration->{
                ChangeConfigurationActivity.actionStart(requireActivity())
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getBackData(receivedData: String) {
//        if (!screenState){
        //参数
        if (receivedData.startsWith("B100")) {
            if (BinaryChange.proofData(receivedData.substring(0, 18)) == receivedData.subSequence(18, 20)) {
                binding.linCalibration.visibility = View.VISIBLE
                when (receivedData.subSequence(4, 6)) {
                    "00" -> binding.ivLeft.vtvConnectionState.text = "未激活"
                    "01" -> binding.ivLeft.vtvConnectionState.text = "非法设备"
                    "02" -> binding.ivLeft.vtvConnectionState.text = "授权失败"
                    "03" -> binding.ivLeft.vtvConnectionState.text = "连接正常"
                }
                if (!screenState){
                    when (receivedData.subSequence(8, 10)) {
                        "00" -> {
                            binding.btnSuspend.visibility = View.GONE
                            binding.btnStart.visibility = View.VISIBLE
                        }
                        "01" -> {
                            binding.btnSuspend.visibility = View.VISIBLE
                            binding.btnStart.visibility = View.GONE
                            binding.btnSuspend.isChecked = true
                        }
                        "02" -> {
                            binding.btnSuspend.visibility = View.GONE
                            binding.btnStart.visibility = View.VISIBLE
                            binding.btnStart.isChecked = true
                        }
                        "03" -> {
                            binding.btnRefresh.isChecked = true
                        }
                    }
                }

                when (receivedData.subSequence(10, 12)) {
                    "00" -> binding.ivLeft.vtvProbeState.text = "未连接"
                    "01" -> binding.ivLeft.vtvProbeState.text = "已连接"
                    "02" -> binding.ivLeft.vtvProbeState.text = "非法探头"
                }
                when (receivedData.subSequence(12, 14)) {
                    "00" -> binding.ivLeft.vtvProbe.text = "常温探头"
                    "01" -> binding.ivLeft.vtvProbe.text = "高温探头"
                    "02" -> binding.ivLeft.vtvProbe.text = "水下探头"
                    "03" -> binding.ivLeft.vtvProbe.text = "旋转探头"
                }
                when (receivedData.subSequence(14, 16)) {
                    "00" -> binding.ivLeft.vtvProbeType.text = "标准探头"
                    "01" -> binding.ivLeft.vtvProbeType.text = "笔试探头"
                    "02" -> binding.ivLeft.vtvProbeType.text = "X/Z多阵列探头"
                    "03" -> binding.ivLeft.vtvProbeType.text = "Z多阵列探头"
                    "06" -> binding.ivLeft.vtvProbeType.text = "X/Z柔性探头"
                    "05" -> binding.ivLeft.vtvProbeType.text = "Z柔性探头"
                }
                binding.ivLeft.vtvProbeNum.text = BinaryChange.hexToInt(receivedData.subSequence(16, 18) as String).toString()
                mSerialPortHelper.sendTxt(SerialPortDataMake.operateData(receivedData.subSequence(8, 10).toString()))
            }
        }
            //数据
        if (receivedData.startsWith("B101") && receivedData.length == 40) {
            var data = receivedData.substring(10, 34)
            Thread.sleep(5)
            LineDataRead.readMeterData(data, binding.lineChartBX, binding.lineChartBZ, binding.lineChart, punctationState)
            punctationState = false
        }
//        }
    }
}
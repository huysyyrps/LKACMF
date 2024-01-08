package com.example.lkacmf.fragment

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.lkacmf.R
import com.example.lkacmf.databinding.FragmentCalibrationBinding
import com.example.lkacmf.serialport.SerialPortConstant
import com.example.lkacmf.serialport.SerialPortDataMake
import com.example.lkacmf.util.BinaryChange
import com.example.lkacmf.util.LogUtil
import com.example.lkacmf.util.linechart.LineChartListener
import com.example.lkacmf.util.linechart.LineChartSetting
import com.example.lkacmf.util.linechart.LineDataRead
import kotlinx.android.synthetic.main.fragment_home.*

class CalibrationFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentCalibrationBinding? = null
    private val binding get() = _binding!!

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
        binding.btnStartCalibration.setOnClickListener(this)
        binding.btnStart.setOnClickListener(this)
        binding.btnSuspend.setOnClickListener(this)
        binding.btnStop.setOnClickListener(this)
        binding.btnRefresh.setOnClickListener(this)
        binding.btnScreen.setOnClickListener(this)
        binding.btnCount.setOnClickListener(this)
        binding.btnReset.setOnClickListener(this)

        LineChartSetting.SettingLineChart(requireActivity(), binding.lineChartBX, showX = true, scale = true)
        LineChartSetting.SettingLineChart(requireActivity(), binding.lineChartBZ, showX = true, scale = true)
        LineChartSetting.SettingLineChart(requireActivity(), binding.lineChart, showX = true, scale = true)
        LineChartListener.lineChartSetListener(binding.lineChartBX,false,binding.lineChartBX,binding.lineChartBZ,binding.lineChart)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnStartCalibration -> {
                binding.btnStart.visibility = View.GONE
                binding.linCalibration.visibility = View.VISIBLE
                SerialPortConstant.serialPortDataListener<CalibrationFragment>(this)
                SerialPortConstant.getSerialPortHelper().sendTxt(SerialPortDataMake.operateData("00"))
            }
            R.id.btnStart->{
                btnSuspend.isChecked = true
                binding.btnStart.visibility = View.GONE
                binding.btnSuspend.visibility = View.VISIBLE
                SerialPortConstant.getSerialPortHelper().sendTxt(SerialPortDataMake.operateData("01"))
            }
            R.id.btnStop -> {
                SerialPortConstant.getSerialPortHelper().sendTxt( SerialPortDataMake.operateData("00"))
            }
            R.id.btnSuspend->{
                btnStart.isChecked = true
                btnStart.visibility = View.VISIBLE
                btnSuspend.visibility = View.GONE
                SerialPortConstant.getSerialPortHelper().sendTxt( SerialPortDataMake.operateData("02"))
//                timer.cancel()
            }
            R.id.btnRefresh->{
                SerialPortConstant.getSerialPortHelper().sendTxt( SerialPortDataMake.operateData("02"))
                LineDataRead.readRefreshData(binding.lineChartBX, binding.lineChartBZ, binding.lineChart)
                SerialPortConstant.getSerialPortHelper().sendTxt( SerialPortDataMake.operateData("03"))
                SerialPortConstant.getSerialPortHelper().sendTxt( SerialPortDataMake.operateData("01"))
            }
            R.id.btnReset->{
                LineDataRead.backPlay(binding.lineChartBX, binding.lineChartBZ, binding.lineChart)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getBackData(receivedData: String) {
        LogUtil.e("TAG", receivedData)
        //参数
        if (receivedData.startsWith("B100")) {
            binding.btnStartCalibration.visibility = View.GONE
            binding.linCalibration.visibility = View.VISIBLE
            when (receivedData.subSequence(4, 6)) {
                "00" -> binding.ivLeft.vtvConnectionState.text = "未激活"
                "01" -> binding.ivLeft.vtvConnectionState.text = "非法设备"
                "02" -> binding.ivLeft.vtvConnectionState.text = "授权失败"
                "03" -> binding.ivLeft.vtvConnectionState.text = "连接正常"
            }
//            binding.tvQuantity.text = BinaryChange.hexToInt(receivedData.subSequence(6, 8) as String).toString()
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
            SerialPortConstant.getSerialPortHelper().sendTxt(SerialPortDataMake.operateData(receivedData.subSequence(8, 10).toString()))
        }
        //数据
        if (receivedData.startsWith("B101") && receivedData.length == 40) {
            var data = receivedData.substring(10, 34)
            Thread.sleep(5)
            LineDataRead.readMeterData(data, binding.lineChartBX, binding.lineChartBZ, binding.lineChart, false)
        }
    }
}
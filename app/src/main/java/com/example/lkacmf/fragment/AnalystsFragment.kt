package com.example.lkacmf.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.lkacmf.R
import com.example.lkacmf.serialport.SerialPortConstant
import com.example.lkacmf.serialport.SerialPortDataMake
import com.example.lkacmf.util.linechart.LineChartSetting
import com.example.lkacmf.util.linechart.LineDataRead
import kotlinx.android.synthetic.main.fragment_analysts.*

class AnalystsFragment : Fragment(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_analysts, container, false)
    }

    override fun onStart() {
        super.onStart()
        LineChartSetting.SettingLineChart(requireActivity(), lineChartAnaBX, true)
        LineChartSetting.SettingLineChart(requireActivity(), lineChartAnaBZ, true)
        LineChartSetting.SettingLineChart(requireActivity(), lineChartAna, true)
        btnBackPlay.setOnClickListener(this)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnBackPlay -> {
//                if (mSerialPortHelper.isOpen) {
//                    SerialPortDataMake.operateData("00")
//                }
                var mSerialPortHelper = SerialPortConstant.getMSerialPortHelper(requireActivity())
                SerialPortDataMake.operateData("00")
                LineDataRead.backPlay(lineChartAnaBX, lineChartAnaBZ, lineChartAna)
            }
        }
    }
}
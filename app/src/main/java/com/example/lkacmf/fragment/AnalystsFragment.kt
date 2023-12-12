package com.example.lkacmf.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.lkacmf.R
import com.example.lkacmf.databinding.FragmentAnalystsBinding
import com.example.lkacmf.databinding.FragmentHomeBinding
import com.example.lkacmf.serialport.SerialPortConstant
import com.example.lkacmf.serialport.SerialPortDataMake
import com.example.lkacmf.util.linechart.LineChartSetting
import com.example.lkacmf.util.linechart.LineDataRead
import kotlinx.android.synthetic.main.fragment_analysts.*

class AnalystsFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentAnalystsBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentAnalystsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        LineChartSetting.SettingLineChart(requireActivity(), lineChartAnaBX, true)
        LineChartSetting.SettingLineChart(requireActivity(), lineChartAnaBZ, false)
        LineChartSetting.SettingLineChart(requireActivity(), lineChartAna, true)
        LineDataRead.backPlay(lineChartAnaBX, lineChartAnaBZ, lineChartAna)
        binding.btnBackPlay.setOnClickListener(this)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnBackPlay -> {
                var mSerialPortHelper = SerialPortConstant.getMSerialPortHelper(requireActivity())
                SerialPortDataMake.operateData("00")
                LineDataRead.backPlay(lineChartAnaBX, lineChartAnaBZ, lineChartAna)
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
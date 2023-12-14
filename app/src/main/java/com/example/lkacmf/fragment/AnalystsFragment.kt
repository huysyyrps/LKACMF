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
        LineChartSetting.SettingLineChart(requireActivity(), binding.lineChartAnaBX, showX = true, scale = true)
        LineChartSetting.SettingLineChart(requireActivity(), binding.lineChartAnaBZ, showX = true, scale = true)
        LineChartSetting.SettingLineChart(requireActivity(), binding.lineChartAna, showX = true, scale = true)
        LineDataRead.backPlay(binding.lineChartAnaBX, binding.lineChartAnaBZ, binding.lineChartAna)
        binding.btnBackPlay.setOnClickListener(this)
        binding.btnReset.setOnClickListener(this)
//        binding.lineChartAnaBX.saveToGallery("mychart.jpg",85)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnBackPlay -> {
                var mSerialPortHelper = SerialPortConstant.getMSerialPortHelper(requireActivity())
                SerialPortDataMake.operateData("00")
                LineDataRead.backPlay(binding.lineChartAnaBX, binding.lineChartAnaBZ, binding.lineChartAna)
            }
            R.id.btnReset->{
                binding.lineChartAnaBX.fitScreen()
                binding.lineChartAnaBX.invalidate()
                binding.lineChartAnaBZ.fitScreen()
                binding.lineChartAnaBZ.invalidate()
                binding.lineChartAna.fitScreen()
                binding.lineChartAna.invalidate()
                LineChartSetting.mMatrix.let {
                    it.reset()
                }
                LineChartSetting.mSavedMatrix.let {
                    it.reset()
                }
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
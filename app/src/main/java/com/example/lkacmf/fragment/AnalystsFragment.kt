package com.example.lkacmf.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.lkacmf.R
import com.example.lkacmf.databinding.FragmentAnalystsBinding
import com.example.lkacmf.serialport.SerialPortConstant
import com.example.lkacmf.serialport.SerialPortDataMake
import com.example.lkacmf.util.Constant
import com.example.lkacmf.util.dialog.DialogUtil
import com.example.lkacmf.util.linechart.LineChartSetting
import com.example.lkacmf.util.linechart.LineDataRead
import com.example.lkacmf.util.mediaprojection.CaptureImage

class AnalystsFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentAnalystsBinding? = null
    private val binding get() = _binding!!
    var permissionTag = false
    private lateinit var mediaManager: MediaProjectionManager
    private var mMediaProjection: MediaProjection? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentAnalystsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        mediaManager = requireActivity().getSystemService(FragmentActivity.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        LineChartSetting.SettingLineChart(requireActivity(), binding.lineChartAnaBX, showX = true, scale = true)
        LineChartSetting.SettingLineChart(requireActivity(), binding.lineChartAnaBZ, showX = true, scale = true)
        LineChartSetting.SettingLineChart(requireActivity(), binding.lineChartAna, showX = true, scale = true)
        LineDataRead.backPlay(binding.lineChartAnaBX, binding.lineChartAnaBZ, binding.lineChartAna)
        binding.btnBackPlay.setOnClickListener(this)
        binding.btnReset.setOnClickListener(this)
        binding.btnImage.setOnClickListener(this)
//        binding.lineChartAnaBX.saveToGallery("mychart.jpg",85)
    }

    @RequiresApi(Build.VERSION_CODES.S)
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
            R.id.btnImage -> {
                val requestList = ArrayList<String>()
                requestList.add(Manifest.permission.READ_EXTERNAL_STORAGE)
                requestList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                requestList.add(Manifest.permission.ACCESS_COARSE_LOCATION)
                requestList.add(Manifest.permission.ACCESS_FINE_LOCATION)
                permissionTag = DialogUtil.requestPermission(activity = requireActivity(),requestList)
                if (permissionTag){
                    if (mMediaProjection == null) {
                        val captureIntent: Intent = mediaManager.createScreenCaptureIntent()
                        startActivityForResult(captureIntent, Constant.TAG_ONE)
                    } else {
                        mMediaProjection?.let {
                            CaptureImage().captureImages(
                                requireActivity(),
                                "image",
                                it
                            )
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("UseRequireInsteadOfGet")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Constant.TAG_ONE -> {
                    mMediaProjection = data?.let { mediaManager.getMediaProjection(resultCode, it) }
                    mMediaProjection?.let { CaptureImage().captureImages( requireActivity(), "image", it) }
                }
                Constant.TAG_TWO -> {
                    mMediaProjection = data?.let { mediaManager.getMediaProjection(resultCode, it) }
                    mMediaProjection?.let { CaptureImage().captureImages( requireActivity(), "form", it) }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
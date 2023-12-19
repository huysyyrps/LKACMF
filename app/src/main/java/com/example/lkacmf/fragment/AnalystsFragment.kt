package com.example.lkacmf.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.lkacmf.R
import com.example.lkacmf.databinding.FragmentAnalystsBinding
import com.example.lkacmf.serialport.DataManagement
import com.example.lkacmf.serialport.SerialPortConstant
import com.example.lkacmf.serialport.SerialPortDataMake
import com.example.lkacmf.util.Constant
import com.example.lkacmf.util.LogUtil
import com.example.lkacmf.util.dialog.DialogUtil
import com.example.lkacmf.util.linechart.LineChartSetting
import com.example.lkacmf.util.linechart.LineDataRead
import com.example.lkacmf.util.mediaprojection.CaptureImage
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener

//, View.OnTouchListener
class AnalystsFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentAnalystsBinding? = null
    private val binding get() = _binding!!
    var permissionTag = false
    private var startIndex = 0
    private var endIndex = 0
    private var rect:Rect = DataManagement.frameRect

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
        LineChartSetting.SettingLineChart(requireActivity(), binding.lineChartAnaBX, showX = true, scale = false)
        LineChartSetting.SettingLineChart(requireActivity(), binding.lineChartAnaBZ, showX = true, scale = false)
        LineChartSetting.SettingLineChart(requireActivity(), binding.lineChartAna, showX = true, scale = false)
        LineDataRead.backPlay(binding.lineChartAnaBX, binding.lineChartAnaBZ, binding.lineChartAna)
        binding.btnBackPlay.setOnClickListener(this)
        binding.btnReset.setOnClickListener(this)
        binding.btnImage.setOnClickListener(this)
//        binding.lineChartAnaBX.saveToGallery("mychart.jpg",85)
//        binding.lineChartAnaBX.setOnTouchListener(this)

//        region
        binding.lineChartAnaBX.onChartGestureListener = object : OnChartGestureListener {
            // 手势监听器
            override fun onChartGestureStart(event: MotionEvent, lastPerformedGesture: ChartTouchListener.ChartGesture) {
                // 按下
                if (event.pointerCount == 1) {
                    LogUtil.e("TAG", "按下")
                    rect.left = event?.x.toInt()
                    rect.top = event?.y.toInt()
                    //返回当前数据下标
                    val highlight = binding.lineChartAnaBX.getHighlightByTouchPoint(event.x, event.y)
                    val set = binding.lineChartAnaBX.data.getDataSetByIndex(highlight.dataSetIndex)
                    val e: Entry = binding.lineChartAnaBX.data.getEntryForHighlight(highlight)
                    startIndex = set.getEntryIndex(e)
//                    binding.lineChartAnaBX.invalidate()
                }
            }

            override fun onChartGestureDoubleStart(event: MotionEvent) {
                if (event.pointerCount >= 2) {
                    LogUtil.e("TAG", "按下TWO")
                    rect.setEmpty()
                    binding.lineChartAnaBX.invalidate()
                }
            }

            override fun onChartGestureEnd(event: MotionEvent, lastPerformedGesture: ChartTouchListener.ChartGesture) {
                // 抬起,取消
                if (event.pointerCount == 1) {
                    LogUtil.e("TAG", "抬起")
                    if(rect.left!=0&&rect.top!=0){
                        val highlight = binding.lineChartAnaBX.getHighlightByTouchPoint(event.x, event.y)
                        val set = binding.lineChartAnaBX.data.getDataSetByIndex(highlight.dataSetIndex)
                        val e: Entry = binding.lineChartAnaBX.data.getEntryForHighlight(highlight)
                        endIndex = set.getEntryIndex(e)
                        rect.setEmpty()
                        binding.lineChartAnaBX.invalidate()
                        LineDataRead.framePlay(startIndex, endIndex, binding.lineChartAnaBX, binding.lineChartAnaBZ, binding.lineChartAna)
                    }
                }
            }

            override fun onChartLongPressed(me: MotionEvent) {
                // 长按
                LogUtil.e("TAG", "长按")
            }
            override fun onChartDoubleTapped(me: MotionEvent) {
                // 双击
                LogUtil.e("TAG", "双击")
            }
            override fun onChartSingleTapped(me: MotionEvent) {
                // 单击
                LogUtil.e("TAG", "单击")
            }
            override fun onChartFling(me1: MotionEvent, me2: MotionEvent, velocityX: Float, velocityY: Float) {
                // 甩动
                LogUtil.e("TAG", "甩动")
            }
            override fun onChartScale(event: MotionEvent, scaleX: Float, scaleY: Float) {
                // 缩放
            }
            override fun onChartTranslate(me: MotionEvent, dX: Float, dY: Float) {
                // 移动
                LogUtil.e("TAG", "移动")
            }
            override fun onChartMove(event: MotionEvent) {
                if (event.pointerCount == 1) {
                    rect.right = event.x.toInt()
                    rect.bottom = event.y.toInt()
                    binding.lineChartAnaBX.invalidate()
                }
            }
        }
//        endregion


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
                //region Description
                //                binding.lineChartAnaBX.fitScreen()
//                binding.lineChartAnaBX.invalidate()
//                binding.lineChartAnaBZ.fitScreen()
//                binding.lineChartAnaBZ.invalidate()
//                binding.lineChartAna.fitScreen()
//                binding.lineChartAna.invalidate()
//                LineChartSetting.mMatrix.let {
//                    it.reset()
//                }
//                LineChartSetting.mSavedMatrix.let {
//                    it.reset()
//                }
                //endregion
                LineDataRead.backPlay(binding.lineChartAnaBX, binding.lineChartAnaBZ, binding.lineChartAna)
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

//    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
//        when (event?.action) {
//            MotionEvent.ACTION_DOWN -> {
//                if (event.pointerCount == 1) {
//                    downX = event?.x
//                    downY = event?.y
//                    rect.left = event?.x.toInt()
//                    rect.top = event?.y.toInt()
//                    //返回当前数据下标
//                    val highlight = binding.lineChartAnaBX.getHighlightByTouchPoint(event.x, event.y)
//                    val set = binding.lineChartAnaBX.data.getDataSetByIndex(highlight.dataSetIndex)
//                    val e: Entry = binding.lineChartAnaBX.data.getEntryForHighlight(highlight)
//                    startIndex = set.getEntryIndex(e)
//                    binding.lineChartAnaBX.invalidate()
//                }
//            }
//            MotionEvent.ACTION_MOVE -> {
//                if (event.pointerCount == 1) {
//                    rect.right = event?.x.toInt()
//                    rect.bottom = event?.y.toInt()
//                    binding.lineChartAnaBX.invalidate()
//                }
//            }
//            MotionEvent.ACTION_UP -> {
//                if (event.pointerCount == 1) {
//                    //返回当前数据下标
//                    val highlight = binding.lineChartAnaBX.getHighlightByTouchPoint(event.x, event.y)
//                    val set = binding.lineChartAnaBX.data.getDataSetByIndex(highlight.dataSetIndex)
//                    val e: Entry = binding.lineChartAnaBX.data.getEntryForHighlight(highlight)
//                    endIndex = set.getEntryIndex(e)
//                    rect.setEmpty()
//                    binding.lineChartAnaBX.invalidate()
//
//                    LineDataRead.framePlay(startIndex, endIndex, binding.lineChartAnaBX, binding.lineChartAnaBZ, binding.lineChartAna)
//                }
//            }
//        }
//        return true
//    }
}
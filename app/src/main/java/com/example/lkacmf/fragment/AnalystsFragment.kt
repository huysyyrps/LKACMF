package com.example.lkacmf.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
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
import com.example.lkacmf.MyApplication
import com.example.lkacmf.R
import com.example.lkacmf.databinding.FragmentAnalystsBinding
import com.example.lkacmf.serialport.DataManagement
import com.example.lkacmf.serialport.DataManagement.landBXList
import com.example.lkacmf.serialport.DataManagement.landBZList
import com.example.lkacmf.serialport.DataManagement.landList
import com.example.lkacmf.serialport.DataManagement.punctationList
import com.example.lkacmf.serialport.SerialPortConstant
import com.example.lkacmf.serialport.SerialPortDataMake
import com.example.lkacmf.util.Constant
import com.example.lkacmf.util.LogUtil
import com.example.lkacmf.util.dialog.DialogUtil
import com.example.lkacmf.util.linechart.LineChartSetting
import com.example.lkacmf.util.linechart.LineDataRead
import com.example.lkacmf.util.mediaprojection.CaptureImage
import com.example.lkacmf.view.BaseLineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
import kotlin.math.pow

//, View.OnTouchListener
class AnalystsFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentAnalystsBinding? = null
    private val binding get() = _binding!!
    var permissionTag = false
    private var startIndex = 0
    private var endIndex = 0
    private var rect:Rect = DataManagement.frameRect
    private var bitmapList:ArrayList<Bitmap> = ArrayList()

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
        LineChartSetting.SettingLineChart(requireActivity(), binding.lineChartGone, showX = true, scale = false)
        LineDataRead.backPlay(binding.lineChartAnaBX, binding.lineChartAnaBZ, binding.lineChartAna)
        binding.btnBackPlay.setOnClickListener(this)
        binding.btnReset.setOnClickListener(this)
        binding.btnImage.setOnClickListener(this)
        binding.btnReport.setOnClickListener(this)
        binding.btnCount.setOnClickListener(this)
        touchListener(binding.lineChartAnaBX)
        touchListener(binding.lineChartAnaBZ)
    }

    fun touchListener(view: BaseLineChart) {
        view.onChartGestureListener = object : OnChartGestureListener {
            override fun onChartGestureStart(event: MotionEvent, lastPerformedGesture: ChartTouchListener.ChartGesture) {
                // 按下
                if (event.pointerCount == 1) {
                    LogUtil.e("TAG", "按下")
                    rect.left = event?.x.toInt()
                    rect.top = event?.y.toInt()
                    //返回当前数据下标
                    val highlight = view.getHighlightByTouchPoint(event.x, event.y)
                    val set = view.data.getDataSetByIndex(highlight.dataSetIndex)
                    val e: Entry = view.data.getEntryForHighlight(highlight)
                    startIndex = set.getEntryIndex(e)
//                    binding.lineChartAnaBX.invalidate()
                }
            }

            override fun onChartGestureDoubleStart(event: MotionEvent) {
                if (event.pointerCount >= 2) {
                    LogUtil.e("TAG", "按下TWO")
                    rect.setEmpty()
                    view.invalidate()
                }
            }

            override fun onChartGestureEnd(event: MotionEvent, lastPerformedGesture: ChartTouchListener.ChartGesture) {
                // 抬起,取消
                if (event.pointerCount == 1) {
                    LogUtil.e("TAG", "抬起")
                    if ((rect.left != 0 && rect.top != 0) && (rect.right != 0 && rect.bottom != 0)) {
                        val highlight = view.getHighlightByTouchPoint(event.x, event.y)
                        val set = view.data.getDataSetByIndex(highlight.dataSetIndex)
                        val e: Entry = view.data.getEntryForHighlight(highlight)
                        endIndex = set.getEntryIndex(e)
                        rect.setEmpty()
                        view.invalidate()
                        LineDataRead.framePlay(startIndex, endIndex, binding.lineChartAnaBX, binding.lineChartAnaBZ, binding.lineChartAna)
                    }
                    rect.setEmpty()
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
                    view.invalidate()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnBackPlay -> {
                var mSerialPortHelper = SerialPortConstant.getMSerialPortHelper(requireActivity())
                SerialPortDataMake.operateData("00")
                LineDataRead.backPlay(binding.lineChartAnaBX, binding.lineChartAnaBZ, binding.lineChartAna)
            }
            R.id.btnReset -> {
                LineDataRead.backPlay(binding.lineChartAnaBX, binding.lineChartAnaBZ, binding.lineChartAna)
            }
            R.id.btnImage -> {
                val requestList = ArrayList<String>()
                requestList.add(Manifest.permission.READ_EXTERNAL_STORAGE)
                requestList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                requestList.add(Manifest.permission.ACCESS_COARSE_LOCATION)
                requestList.add(Manifest.permission.ACCESS_FINE_LOCATION)
                requestList.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
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
            R.id.btnReport -> {
                bitmapList.clear()
                if (punctationList.size==0){
                    var viewBX = binding.framChartAnaBX
                    viewBX.setDrawingCacheEnabled(true)
                    viewBX.buildDrawingCache()
                    var bitmapBX = Bitmap.createBitmap(viewBX.getDrawingCache())
                    var viewBZ = binding.framChartAnaBZ
                    viewBZ.setDrawingCacheEnabled(true)
                    viewBZ.buildDrawingCache()
                    var bitmapBZ = Bitmap.createBitmap(viewBZ.getDrawingCache())

                    var view = binding.framChartAna
                    view.setDrawingCacheEnabled(true)
                    view.buildDrawingCache()
                    var bitmap = Bitmap.createBitmap(view.getDrawingCache())
                    bitmapList.add(bitmapBX)
                    bitmapList.add(bitmapBZ)
                    bitmapList.add(bitmap)
                    DialogUtil.writeFormDataDialog(
                        requireActivity(),
                        bitmapList
                    )
                    return
                }
                for (i in 0 until punctationList.size){
                    if (i==0){
                        setlineChartAnaTData(landBXList.subList(0,punctationList[i]),landBZList.subList(0,punctationList[i]),landList.subList(0,punctationList[i]))
                    }else{
                        setlineChartAnaTData(landBXList.subList(punctationList[i-1],punctationList[i]),
                            landBZList.subList(punctationList[i-1],punctationList[i]),
                            landList.subList(punctationList[i-1],punctationList[i]))
                    }
                }
                if (endIndex<landBXList.size){
                    setlineChartAnaTData(landBXList.subList(punctationList[punctationList.size-1],landBXList.size),
                        landBZList.subList(punctationList[punctationList.size-1],landBZList.size),
                        landList.subList(punctationList[punctationList.size-1],landList.size))
                }
                val drawable = resources.getDrawable(R.drawable.ic_test)
                // 将drawable转为Bitmap对象
                val bitmap = (drawable as BitmapDrawable).bitmap
                bitmapList.add(bitmap)
                bitmapList.add(bitmap)
                bitmapList.add(bitmap)
                DialogUtil.writeFormDataDialog(
                    requireActivity(),
                    bitmapList
                )
            }
            R.id.btnCount -> {
                var countList = DataManagement.landBXList.subList(startIndex, endIndex)
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
                binding.tvDepth.text = "$formattedDx"
                var length = if (maxTopValue>maxBottonValue){
                    1.02*(maxTopValue-minValue)
                }else{
                    1.02*(maxBottonValue-minValue)
                }
                val formattedLength = String.format("%.2f", length)
                binding.tvLength.text = "$formattedLength"
                LogUtil.e("TAG","$formattedDx----$formattedLength")
            }
        }
    }

    private fun setlineChartAnaTData(landBXList: MutableList<Entry>,landBZList: MutableList<Entry>,landList: MutableList<Entry>) {
        binding.lineChartGone.clear()
        var lineSetBX = LineDataSet(landBXList, "")
        lineSetBX.setDrawValues(false)
        lineSetBX.setDrawCircles(false)
        lineSetBX.mode = LineDataSet.Mode.CUBIC_BEZIER
        lineSetBX.color = MyApplication.context.resources.getColor(R.color.theme_color)
        //将数据集添加到数据 ChartData 中
        val lineDataBX = LineData(lineSetBX)
        binding.lineChartGone.data = lineDataBX
        binding.lineChartGone.notifyDataSetChanged()
        binding.lineChartGone.invalidate()
        var viewBX = binding.cardChartGone
        viewBX.setDrawingCacheEnabled(true)
        viewBX.buildDrawingCache()
        var bitmapBX = Bitmap.createBitmap(viewBX.getDrawingCache())
        bitmapList.add(bitmapBX)

        binding.lineChartGone.clear()
        var lineSetBZ = LineDataSet(landBZList, "")
        lineSetBZ.setDrawValues(false)
        lineSetBZ.setDrawCircles(false)
        lineSetBZ.mode = LineDataSet.Mode.CUBIC_BEZIER
        lineSetBZ.color = MyApplication.context.resources.getColor(R.color.theme_color)
        //将数据集添加到数据 ChartData 中
        val lineDataBZ = LineData(lineSetBZ)
        binding.lineChartGone.data = lineDataBZ
        binding.lineChartGone.notifyDataSetChanged()
        binding.lineChartGone.invalidate()
        var viewBZ = binding.cardChartGone
        viewBZ.setDrawingCacheEnabled(true)
        viewBZ.buildDrawingCache()
        var bitmapBZ = Bitmap.createBitmap(viewBZ.getDrawingCache())
        bitmapList.add(bitmapBZ)

        binding.lineChartGone.clear()
        var lineSet = LineDataSet(landList, "")
        lineSet.setDrawValues(false)
        lineSet.setDrawCircles(false)
        lineSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        lineSet.color = MyApplication.context.resources.getColor(R.color.theme_color)
        //将数据集添加到数据 ChartData 中
        val lineData = LineData(lineSet)
        binding.lineChartGone.data = lineData
        binding.lineChartGone.notifyDataSetChanged()
        binding.lineChartGone.invalidate()
        var view = binding.cardChartGone
        view.setDrawingCacheEnabled(true)
        view.buildDrawingCache()
        var bitmap = Bitmap.createBitmap(view.getDrawingCache())
        bitmapList.add(bitmap)
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
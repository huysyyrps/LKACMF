package com.example.lkacmf.fragment

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.lkacmf.R
import com.example.lkacmf.activity.MainActivity
import com.example.lkacmf.databinding.FragmentHomeBinding
import com.example.lkacmf.entity.AcmfCode
import com.example.lkacmf.module.AcmfCodeContract
import com.example.lkacmf.presenter.AcmfCodePresenter
import com.example.lkacmf.serialport.SerialPortConstant
import com.example.lkacmf.serialport.SerialPortDataMake
import com.example.lkacmf.util.*
import com.example.lkacmf.util.dialog.DialogSureCallBack
import com.example.lkacmf.util.dialog.DialogUtil
import com.example.lkacmf.util.linechart.LineChartListener
import com.example.lkacmf.util.linechart.LineChartSetting
import com.example.lkacmf.util.linechart.LineDataRead
import com.example.lkacmf.util.popup.CustomBubbleAttachPopup
import com.example.lkacmf.util.popup.MaterialListData
import com.example.lkacmf.util.popup.PopupListData
import com.example.lkacmf.util.sp.BaseSharedPreferences
import kotlinx.android.synthetic.main.acticity_left.view.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.f1reking.serialportlib.SerialPortHelper
import me.f1reking.serialportlib.listener.ISerialPortDataListener
import java.util.*


class HomeFragment : Fragment(), View.OnClickListener, AcmfCodeContract.View {
    var index:Int = 0
    var punctationState = false
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var mSerialPortHelper: SerialPortHelper
    lateinit var acmfCodePresenter:AcmfCodePresenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onStart() {
        super.onStart()
        binding.btnStart.setOnClickListener(this)
        binding.btnSuspend.setOnClickListener(this)
        binding.btnStop.setOnClickListener(this)
        binding.btnRefresh.setOnClickListener(this)
        binding.btnPunctation.setOnClickListener(this)
        binding.btnDirection.setOnClickListener(this)
        binding.btnThinkness.setOnClickListener(this)
        binding.btnMaterial.setOnClickListener(this)
        acmfCodePresenter = AcmfCodePresenter(requireContext(), view = this)

        LineChartSetting.SettingLineChart(requireActivity(), lineChartBX, showX = true, scale = false)
        LineChartSetting.SettingLineChart(requireActivity(), lineChartBZ, showX = true, scale = false)
        LineChartSetting.SettingLineChart(requireActivity(), lineChart, showX = true, scale = false)
//        LineChartListener.lineChartSetListener(binding.lineChartBX, binding.lineChartBX, binding.lineChartBZ, binding.lineChart, this)
//        LineChartListener.lineChartSetListener(binding.lineChartBZ, binding.lineChartBX, binding.lineChartBZ, binding.lineChart, this)
//        LineChartListener.lineChartSetListener(binding.lineChart, binding.lineChartBX, binding.lineChartBZ, binding.lineChart, this)


        var mSerialPortHelper = SerialPortConstant.getSerialPortHelper()
        mSerialPortHelper.setISerialPortDataListener(object : ISerialPortDataListener {
            override fun onDataReceived(bytes: ByteArray?) {
                var receivedData = BinaryChange.byteToHexString(bytes!!)
                LogUtil.e("TAGHome", receivedData)
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
                Log.e("TAG", "onDataSend: " + bytes?.let { BinaryChange.byteToHexString(it) })
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getBackData(receivedData: String) {
//        if (!screenState){
        //参数
        if (receivedData.startsWith("B100")) {
            if (BinaryChange.proofData(receivedData.substring(0, 18)) == receivedData.subSequence(18, 20)) {
                when (receivedData.subSequence(4, 6)) {
                    "00" -> (activity as MainActivity).binding.mainLeft.vtvConnectionState.text = "未激活"
                    "01" -> (activity as MainActivity).binding.mainLeft.vtvConnectionState.text = "非法设备"
                    "02" -> (activity as MainActivity).binding.mainLeft.vtvConnectionState.text = "授权失败"
                    "03" -> (activity as MainActivity).binding.mainLeft.vtvConnectionState.text = "连接正常"
                }

                when (receivedData.subSequence(10, 12)) {
                    "00" -> (activity as MainActivity).binding.mainLeft.vtvProbeState.text = "未连接"
                    "01" -> (activity as MainActivity).binding.mainLeft.vtvProbeState.text = "已连接"
                    "02" -> (activity as MainActivity).binding.mainLeft.vtvProbeState.text = "非法探头"
                }
                when (receivedData.subSequence(12, 14)) {
                    "00" -> (activity as MainActivity).binding.mainLeft.vtvProbe.text = "常温探头"
                    "01" -> (activity as MainActivity).binding.mainLeft.vtvProbe.text = "高温探头"
                    "02" -> (activity as MainActivity).binding.mainLeft.vtvProbe.text = "水下探头"
                    "03" -> (activity as MainActivity).binding.mainLeft.vtvProbe.text = "旋转探头"
                }
                when (receivedData.subSequence(14, 16)) {
                    "00" -> (activity as MainActivity).binding.mainLeft.vtvProbeType.text = "标准探头"
                    "01" -> (activity as MainActivity).binding.mainLeft.vtvProbeType.text = "笔试探头"
                    "02" -> (activity as MainActivity).binding.mainLeft.vtvProbeType.text = "X/Z多阵列探头"
                    "03" -> (activity as MainActivity).binding.mainLeft.vtvProbeType.text = "Z多阵列探头"
                    "06" -> (activity as MainActivity).binding.mainLeft.vtvProbeType.text = "X/Z柔性探头"
                    "05" -> (activity as MainActivity).binding.mainLeft.vtvProbeType.text = "Z柔性探头"
                }
                (activity as MainActivity).binding.mainLeft.vtvProbeNum.text = BinaryChange.hexToInt(receivedData.subSequence(16, 18) as String).toString()
                SerialPortConstant.getSerialPortHelper().sendTxt(SerialPortDataMake.operateData(receivedData.subSequence(8, 10).toString()))
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.vtvSetting->{
                DialogUtil.settingDialog(requireActivity(), object:DialogSureCallBack{
                    @RequiresApi(Build.VERSION_CODES.O)
                    override fun sureCallBack(data: String) {
                        mSerialPortHelper.sendTxt(SerialPortDataMake.settingData())
                    }

                    override fun cancelCallBack(data: String) {
                    }
                })
            }
            R.id.btnStop -> {
                SerialPortConstant.getSerialPortHelper().sendTxt( SerialPortDataMake.operateData("00"))
            }
            R.id.btnStart -> {
                btnSuspend.isChecked = true
                btnStart.visibility = View.GONE
                btnSuspend.visibility = View.VISIBLE
//                timer.scheduleAtFixedRate(0, 10) {
//                    var x = BinaryChange.floatStringToHex(index.toFloat(),8)
//                    var y = BinaryChange.floatStringToHex(Random.nextInt(20, 50).toFloat(),8)
//                    var z = BinaryChange.floatStringToHex(Random.nextInt(20, 50).toFloat(),8)
//                    var data = "$x$y$z"
//                    if (data.length > 4) {
//                        LineDataRead.readMeterData(data, binding.lineChartBX, binding.lineChartBZ, binding.lineChart,punctationState)
//                        punctationState = false
//                    }
//                    index++
//                }
                SerialPortConstant.getSerialPortHelper().sendTxt(SerialPortDataMake.operateData("01"))
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
                btnStart.visibility = View.VISIBLE
                btnSuspend.visibility = View.GONE
//                LineDataRead.readRefreshData(binding.lineChartBX, binding.lineChartBZ, binding.lineChart)
                SerialPortConstant.getSerialPortHelper().sendTxt( SerialPortDataMake.operateData("03"))
                LineDataRead.readRefreshData(binding.lineChartBX, binding.lineChartBZ, binding.lineChart)
            }
            R.id.btnPunctation->{
                punctationState = true
            }
            //路径
            R.id.btnDirection -> {
                com.lxj.xpopup.XPopup.Builder(requireContext())
                    .hasShadowBg(false)
                    .isTouchThrough(true)
                    .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                    .atView(btnDirection)
                    .isCenterHorizontal(true)
                    .hasShadowBg(false) // 去掉半透明背景
                    .isClickThrough(true)
                    .asCustom(CustomBubbleAttachPopup(requireContext(),"popup", object : PopupPositionCallBack {
                        override fun backPosition(index: Int) {
                            PopupListData.setPopupListData()[index].title.showToast(requireContext())
                        }

                    }))
                    .show()
            }
            //材料
            R.id.btnMaterial -> {
                com.lxj.xpopup.XPopup.Builder(requireContext())
                    .hasShadowBg(false)
                    .isTouchThrough(true)
                    .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                    .atView(btnMaterial)
                    .isCenterHorizontal(true)
                    .hasShadowBg(false) // 去掉半透明背景
                    .isClickThrough(true)
                    .asCustom(CustomBubbleAttachPopup(requireContext(),"material", object :PopupPositionCallBack{
                        override fun backPosition(index: Int) {
                            MaterialListData.setMaterialListData()[index].title.showToast(requireContext())
                        }

                    }))
                    .show()
            }
            //图层
            R.id.btnThinkness -> {
                DialogUtil.setThinkness(requireActivity(),object: DialogSureCallBack {
                    override fun sureCallBack(thinkness: String) {
                        thinkness.showToast(requireContext())
                    }

                    override fun cancelCallBack(data: String) {
                    }

                })
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
//        mSerialPortHelper.close()
    }

    override fun setAcmfCode(acmfCode: AcmfCode?) {
        acmfCode?.activationCode?.showToast(requireContext())
    }

    override fun setAcmfCodeMessage(message: String?) {
        message?.showToast(requireContext())
    }
}
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
import com.example.lkacmf.databinding.FragmentHomeBinding
import com.example.lkacmf.serialport.SerialPortConstant
import com.example.lkacmf.serialport.SerialPortDataMake
import com.example.lkacmf.util.ActivationCode
import com.example.lkacmf.util.BinaryChange
import com.example.lkacmf.util.PopupPositionCallBack
import com.example.lkacmf.util.dialog.DialogSureCallBack
import com.example.lkacmf.util.dialog.DialogUtil
import com.example.lkacmf.util.linechart.LineChartSetting
import com.example.lkacmf.util.linechart.LineDataRead
import com.example.lkacmf.util.popup.CustomBubbleAttachPopup
import com.example.lkacmf.util.popup.MaterialListData
import com.example.lkacmf.util.popup.PopupListData
import com.example.lkacmf.util.showToast
import com.example.lkacmf.util.sp.BaseSharedPreferences
import kotlinx.android.synthetic.main.fragment_home.*
import me.f1reking.serialportlib.SerialPortHelper
import me.f1reking.serialportlib.listener.ISerialPortDataListener
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate
import kotlin.random.Random

class HomeFragment : Fragment(), View.OnClickListener {
    var index:Int = 0
    val timer = Timer()
    var activationStaing = false
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var mSerialPortHelper: SerialPortHelper
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
        binding.vtvSetting.setOnClickListener(this)
        binding.btnStart.setOnClickListener(this)
        binding.btnSuspend.setOnClickListener(this)
        binding.btnStop.setOnClickListener(this)
        binding.btnRefresh.setOnClickListener(this)
        binding.btnPunctation.setOnClickListener(this)
        binding.btnDirection.setOnClickListener(this)
        binding.btnThinkness.setOnClickListener(this)
        binding.btnMaterial.setOnClickListener(this)

        LineChartSetting.SettingLineChart(requireActivity(), lineChartBX, true)
        LineChartSetting.SettingLineChart(requireActivity(), lineChartBZ, false)
        LineChartSetting.SettingLineChart(requireActivity(), lineChart, true)
        SerialPortHelperHandler()
    }

    /**
     * 获取连接SerialPortHelper
     * 并监听连接状态
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun SerialPortHelperHandler() {
        mSerialPortHelper = SerialPortConstant.getSerialPortHelper(requireActivity())
        if (mSerialPortHelper.isOpen){
            timer.scheduleAtFixedRate(0, 1000) {
                mSerialPortHelper.sendTxt(SerialPortDataMake.haveActivationData())
            }
            mSerialPortHelper.setISerialPortDataListener(object : ISerialPortDataListener {
                override fun onDataReceived(bytes: ByteArray?) {
                    var receivedData = BinaryChange.byteToHexString(bytes!!)
                    //判断是否激活
                    if (receivedData.startsWith("B000")&&receivedData.length == 44&&!activationStaing){
                        if (BinaryChange.proofData(receivedData.substring(0, 42)) == receivedData.subSequence(42, 44)) {
                            timer.cancel()
                            var state = receivedData.substring(4, 6)
                            when(state){
                                "00"-> {
                                    activationStaing = true
                                    "未激活".showToast(requireContext())
                                    requireActivity().runOnUiThread {
                                        DialogUtil.noActivitionDialog(requireActivity(), object : DialogSureCallBack {
                                            override fun sureCallBack(data: String) {
                                                var activationCode = ActivationCode.makeCode(receivedData.substring(6, 30))
                                                BaseSharedPreferences.put("activationCode",activationCode)
                                                BaseSharedPreferences.put("deviceCode",receivedData.substring(6, 30))
                                                mSerialPortHelper.sendTxt(SerialPortDataMake.activationData(activationCode))
                                                activationStaing = false
                                            }
                                        })
                                    }
                                }
                                "01"-> "非法设备".showToast(requireActivity())
                                "02"-> {
                                    "授权失效".showToast(requireActivity())
//                                    var data = BaseSharedPreferences.get("activationCode","")+"~"+BaseSharedPreferences.get("deviceCode","")
//                                    val qrCode: Bitmap = CodeUtils.createQRCode(data, 600, null)
//                                    DialogUtil.empowerDialog(this@MainActivity,qrCode, object :DialogSureCallBack{
//                                        override fun sureCallBack() {
//                                            TODO("Not yet implemented")
//                                        }
//
//                                    })
                                }
                                "03"-> {
                                    "设备正常".showToast(requireActivity())
                                }
                            }
                        }
                    }
                    //参数
                    if (receivedData.startsWith("B100")&&receivedData.length == 20){
                        if (BinaryChange.proofData(receivedData.substring(0, 18)) == receivedData.subSequence(18, 20)) {
                            when(receivedData.subSequence(4, 6)){
                                "00"-> vtv_connection_state.text = "未激活"
                                "01"-> vtv_connection_state.text = "非法设备"
                                "02"-> vtv_connection_state.text = "授权失败"
                                "03"-> vtv_connection_state.text = "连接正常"
                            }
//                            tvQuantity.text = BinaryChange.hexToInt(receivedData.subSequence(6, 8) as String).toString()
                            when(receivedData.subSequence(8, 10)){
                                "00"-> {
                                    binding.btnStop.isChecked = true
                                }
                                "01"-> {
                                    binding.btnSuspend.visibility = View.VISIBLE
                                    binding.btnStart.visibility = View.GONE
                                    binding.btnSuspend.isChecked = true
                                }
                                "02"-> {
                                    binding.btnSuspend.visibility = View.GONE
                                    binding.btnStart.visibility = View.VISIBLE
                                    binding.btnStart.isChecked = true
                                }
                                "03"-> {
                                    binding.btnRefresh.isChecked = true
                                }
                            }
                            when(receivedData.subSequence(10, 12)){
                                "00"-> binding.vtvProbeState.text = "未连接"
                                "01"-> binding.vtvProbeState.text = "已连接"
                                "02"-> binding.vtvProbeState.text = "非法探头"
                            }
                            when(receivedData.subSequence(12, 14)){
                                "01"-> binding.vtvProbe.text = "常温探头"
                                "02"-> binding.vtvProbe.text = "高温探头"
                                "03"-> binding.vtvProbe.text = "水下探头"
                                "04"-> binding.vtvProbe.text = "旋转探头"
                            }
                            when(receivedData.subSequence(14, 16)){
                                "01"-> binding.vtvProbeType.text = "标准探头"
                                "02"-> binding.vtvProbeType.text = "笔试探头"
                                "03"-> binding.vtvProbeType.text = "X/Z多阵列探头"
                                "04"-> binding.vtvProbeType.text = "Z多阵列探头"
                                "05"-> binding.vtvProbeType.text = "X/Z柔性探头"
                                "06"-> binding.vtvProbeType.text = "Z柔性探头"
                            }
                            binding.vtvProbeNum.text = BinaryChange.hexToInt(receivedData.subSequence(16, 18) as String).toString()
                        }
                    }
                    //设置
                    if (receivedData.startsWith("B102")&&receivedData.length == 14){
                        if (BinaryChange.proofData(receivedData.substring(0, 12)) == receivedData.subSequence(12, 14)) {
                            BaseSharedPreferences.put("userEncode",receivedData.subSequence(4, 6))
                            BaseSharedPreferences.put("probeNumb",receivedData.subSequence(6, 8))
                            BaseSharedPreferences.put("probeRate",receivedData.subSequence(8, 10))
                            BaseSharedPreferences.put("currentWave",receivedData.subSequence(10, 12))
                        }
                    }
                }

                override fun onDataSend(bytes: ByteArray?) {
                    Log.e("MainActivity", "onDataSend: " + bytes?.let { BinaryChange.byteToHexString(it) })
                }
            })
        }
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
                })
            }
            R.id.btnStop->{
                SerialPortDataMake.operateData("00")
                timer.cancel()
            }
            R.id.btnStart->{
                btnSuspend.isChecked = true
                btnStart.visibility = View.GONE
                btnSuspend.visibility = View.VISIBLE
                timer.scheduleAtFixedRate(0, 50) {
//                    var s = "B101010200"
                    var x = BinaryChange.floatStringToHex(index.toFloat(),8)
                    var y = BinaryChange.floatStringToHex(Random.nextInt(20, 50).toFloat(),8)
                    var z = BinaryChange.floatStringToHex(Random.nextInt(20, 50).toFloat(),8)
                    var data = "$x$y$z"
                    if (data.length > 4) {
                        LineDataRead.readMeterData(data, lineChartBX, lineChartBZ, lineChart)
                    }
                    index++
                }
                SerialPortDataMake.operateData("01")
            }
            R.id.btnSuspend->{
                btnStart.isChecked = true
                btnStart.visibility = View.VISIBLE
                btnSuspend.visibility = View.GONE
                SerialPortDataMake.operateData("02")
                timer.cancel()
            }
            R.id.btnRefresh->{
                SerialPortDataMake.operateData("03")
                Thread.sleep(500)
                LineDataRead.readRefreshData(lineChartBX, lineChartBZ, lineChart)
            }
            R.id.btnPunctation->{
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

                })
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
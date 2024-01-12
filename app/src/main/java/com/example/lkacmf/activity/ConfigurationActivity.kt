package com.example.lkacmf.activity

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.afollestad.materialdialogs.MaterialDialog
import com.example.lkacmf.R
import com.example.lkacmf.databinding.ActivityConfigurationBinding
import com.example.lkacmf.entity.AcmfCode
import com.example.lkacmf.fragment.CalibrationFragment
import com.example.lkacmf.fragment.ConfigFragment
import com.example.lkacmf.fragment.RecommendFragment
import com.example.lkacmf.module.AcmfCodeContract
import com.example.lkacmf.presenter.AcmfCodePresenter
import com.example.lkacmf.serialport.SerialPortConstant
import com.example.lkacmf.serialport.SerialPortDataMake
import com.example.lkacmf.util.*
import com.example.lkacmf.util.dialog.DialogSureCallBack
import com.example.lkacmf.util.dialog.DialogUtil
import com.example.lkacmf.util.file.BaseFileUtil
import com.example.lkacmf.util.sp.BaseSharedPreferences
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.f1reking.serialportlib.listener.ISerialPortDataListener
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import java.lang.Float
import java.util.*
import kotlin.ByteArray
import kotlin.Int
import kotlin.String
import kotlin.getValue
import kotlin.lazy
import kotlin.let
@RequiresApi(Build.VERSION_CODES.O)
class ConfigurationActivity : BaseActivity(), View.OnClickListener, AcmfCodeContract.View {
    val timer = Timer()
    var dialog: MaterialDialog? = null
    var activationStaing = false//放置多次弹窗
    val fragmentList = arrayListOf<Fragment>()
    lateinit var acmfCodePresenter: AcmfCodePresenter
    lateinit var binding: ActivityConfigurationBinding

    val mSerialPortHelper = SerialPortConstant.getSerialPortHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfigurationBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        initViewPage()
        acmfCodePresenter = AcmfCodePresenter(this, view = this)
        binding.btnAddConfig.setOnClickListener(this)

        mSerialPortHelper.setISerialPortDataListener(object : ISerialPortDataListener {
            override fun onDataReceived(bytes: ByteArray?) {
                var receivedData = BinaryChange.byteToHexString(bytes!!)
                LogUtil.e("TAGActivity", receivedData)
                CoroutineScope(Dispatchers.Main).launch {
                    //设备状态
                    if (receivedData.length == 48) {
                        SerialPortConstant.timer.cancel()
                        getBackData(receivedData)
                    }
                    //设置
                    if (receivedData.length == 18) {
                        getBackData(receivedData)
                    }
                }
            }

            override fun onDataSend(bytes: ByteArray?) {
                Log.e("TAGActivity", "onDataSend: " + bytes?.let { BinaryChange.byteToHexString(it) })
            }
        })
//        SerialPortConstant.serialPortDataListener<ConfigurationActivity>(this)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initViewPage() {
        fragmentList.add(ConfigFragment())
        fragmentList.add(RecommendFragment())
        fragmentList.add(CalibrationFragment())
//        //设置adapter
        binding.viewpager.adapter = fragmentAdapter
        //设置viewpage的滑动方向
        binding.viewpager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        //禁止滑动
        // viewpager.isUserInputEnabled = false
        //设置显示的页面，0：是第一页
        //viewpager.currentItem = 1
        //设置缓存页
        binding.viewpager.offscreenPageLimit = 1
        binding.viewpager.isUserInputEnabled = false
        //同时设置多个动画
        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(100))
        compositePageTransformer.addTransformer(TransFormer())
        binding.viewpager.setPageTransformer(compositePageTransformer)
        //设置选中事件
        binding.viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> {
                        binding.tvTitle.text = resources.getString(R.string.configuration)
                        binding.btnAddConfig.visibility = View.VISIBLE
                    }
                    1 -> {
                        binding.tvTitle.text = resources.getString(R.string.recommend)
                        binding.btnAddConfig.visibility = View.GONE
                    }
                    2 -> {
                        binding.tvTitle.text = resources.getString(R.string.calibration)
                        (fragmentList[position] as CalibrationFragment).binding.btnPunctation.visibility = View.GONE
                        (fragmentList[position] as CalibrationFragment).binding.btnDirection.visibility = View.GONE
                        binding.btnAddConfig.visibility = View.GONE
                    }
                }
            }

        })
    }

    fun setViewPageItem(index:Int){
        binding.viewpager.currentItem = index
        if (index!=0){
            binding.btnAddConfig.visibility = View.GONE
        }
    }

    /**
     * viewPager adapter
     */
    private val fragmentAdapter: FragmentStateAdapter by lazy {
        object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return fragmentList.size
            }

            override fun createFragment(position: Int): Fragment {
                return fragmentList[position]
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getBackData(receivedData: String) {
        //授权激活状态
        if (receivedData.startsWith("B000") && !activationStaing) {
            SerialPortConstant.timer.cancel()
            if (BinaryChange.proofData(receivedData.substring(0, 42)) == receivedData.subSequence(42, 44)) {
                var state = receivedData.substring(4, 6)
                var activationCode = BinaryChange.hexToCap(ActivationCode.makeCode(receivedData.substring(6, 30)))
                BaseSharedPreferences.put("activationCode", BinaryChange.hexToCap(activationCode))
                BaseSharedPreferences.put("deviceCode", BinaryChange.hexToCap(receivedData.substring(6, 30)))
                when (state) {
                    "00" -> {
                        activationStaing = true
                        "未激活".showToast(this)
                        DialogUtil.noActivitionDialog(this, object : DialogSureCallBack {
                            override fun sureCallBack(data: String) {
                                SerialPortConstant.getSerialPortHelper().sendTxt(SerialPortDataMake.activationData(activationCode))
                                activationStaing = false
                            }

                            override fun cancelCallBack(data: String) {
                            }
                        })
                    }
                    "01" -> "非法设备".showToast(this)
                    "02" -> {
                        activationStaing = true
                        "授权失效".showToast(this)
                        var code = "${receivedData.substring(6, 30)}/$activationCode"
                        DialogUtil.empowerDialog(this, code, object : DialogSureCallBack {
                            override fun sureCallBack(data: String) {
                                val params = HashMap<String, String>()
                                params["received_data"] = receivedData.substring(6, 30)
                                params["activation_code"] = activationCode
                                params["use_date"] = ""
                                val gson = Gson()
                                val requestBody = RequestBody.create(
                                    "application/json; charset=utf-8".toMediaTypeOrNull(),
                                    gson.toJson(params)
                                )
                                acmfCodePresenter.getAcmfCode(requestBody)
                            }

                            override fun cancelCallBack(data: String) {
                                finish()
                            }
                        })
                    }
                    "03" -> {
                        "设备正常".showToast(this)
                        SerialPortConstant.timer.cancel()
                        (fragmentList[0] as ConfigFragment).getConfigurationList()
                    }
                }
            }
        }
        //设置
        if (receivedData.startsWith("B102")) {
            if (BinaryChange.proofData(receivedData.substring(0, 12)) == receivedData.subSequence(12, 14)) {
                BaseSharedPreferences.put("userEncode", receivedData.subSequence(4, 6))
                BaseSharedPreferences.put("probeNumb", receivedData.subSequence(6, 8))
                BaseSharedPreferences.put("probeRate", receivedData.subSequence(8, 10))
                BaseSharedPreferences.put("currentWave", receivedData.subSequence(10, 12))
            }

        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnAddConfig -> {
                DialogUtil.configurationDialog(this, object : DialogSureCallBack {
                    override fun sureCallBack(data: String) {
                        //确认
                        val dataList = data.split("/")
                        BaseFileUtil.saveConfig(dataList)
                        setViewPageItem(1)
                    }

                    override fun cancelCallBack(data: String) {
                        //保存配置
                        dialog = DialogUtil.configurationNameDialog(this@ConfigurationActivity, object : DialogSureCallBack {
                            override fun sureCallBack(configname: String) {
                                // 需要检查的文件名是否重名
                                BaseFileUtil.haveName(Constant.FILEPATH,"${configname}.txt", ConfigFragment(),data,this@ConfigurationActivity)
                            }

                            override fun cancelCallBack(data: String) {
                            }
                        })
                    }
                })
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun setAcmfCode(acmfCode: AcmfCode?) {
        if (acmfCode?.activationCode?.length == 60 &&
            acmfCode.activationCode.substring(0, 24) == BaseSharedPreferences.get("deviceCode", "") &&
            acmfCode.activationCode.substring(24, 40) == BaseSharedPreferences.get("activationCode", "")
        ) {
            var dateHex = acmfCode.activationCode.substring(40, 52)
            val hexA = dateHex.substring(0, 8)
            val hexB = dateHex.substring(4, 12)
            val intA = Integer.valueOf(hexA, 16)
            val intB = Integer.valueOf(hexB, 16)
            val countA = (intA.toFloat() - intB) / (intA + intB)
            val thicken: String = Integer.toHexString(Float.floatToIntBits(countA))
            val hexThicken = BinaryChange.addZeroForNum(thicken, 8)
            if (hexThicken == acmfCode.activationCode.substring(52, 60)) {
//                SerialPortConstant.sendData(SerialPortDataMake.empowerData(acmfCode.activationCode.substring(40, 60)))
                SerialPortConstant.getSerialPortHelper().sendTxt(SerialPortDataMake.empowerData(acmfCode.activationCode.substring(40, 60)))
            }
        }
    }

    override fun setAcmfCodeMessage(message: String?) {
        message?.showToast(this)
        activationStaing = false
    }

    override fun onDestroy() {
        super.onDestroy()
        mSerialPortHelper.sendTxt(SerialPortDataMake.operateData("00"))
    }
}
package com.example.lkacmf.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.example.lkacmf.R
import com.example.lkacmf.activity.ConfigurationActivity
import com.example.lkacmf.adapter.ConfigListAdapter
import com.example.lkacmf.databinding.FragmentConfigBinding
import com.example.lkacmf.entity.AcmfCode
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
import kotlinx.android.synthetic.main.activity_configuration.*
import kotlinx.android.synthetic.main.dialog_delect_image.*
import me.f1reking.serialportlib.SerialPortHelper
import me.f1reking.serialportlib.listener.ISerialPortDataListener
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import java.io.File
import java.lang.Float

class ConfigFragment : Fragment(), View.OnClickListener, AcmfCodeContract.View {
    var activationStaing = false//放置多次弹窗
    private var _binding: FragmentConfigBinding? = null
    private val binding get() = _binding!!
    lateinit var acmfCodePresenter: AcmfCodePresenter
    private lateinit var mSerialPortHelper: SerialPortHelper
    lateinit var activity:Activity
    private var pathList = ArrayList<String>()
    var dialog: MaterialDialog? = null
    var selectIndex = 0
    private lateinit var adapter: ConfigListAdapter
    lateinit var filePath : File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentConfigBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStart() {
        super.onStart()
        activity = requireActivity()
        filePath = File(activity.externalCacheDir.toString() + "/" + Constant.SAVE_CONFIGURATION_PATH + "/")
        acmfCodePresenter = AcmfCodePresenter(requireContext(), view = this)
        mSerialPortHelper = SerialPortConstant.getSerialPortHelper(activity)
        binding.btnSelect.setOnClickListener(this)

        if (mSerialPortHelper.isOpen) {
            mSerialPortHelper.setISerialPortDataListener(object : ISerialPortDataListener {
                override fun onDataReceived(bytes: ByteArray?) {
                    activity.runOnUiThread {
                        var receivedData = BinaryChange.byteToHexString(bytes!!)
                        LogUtil.e("TAG",receivedData)
                        //判断是否激活
                        if (receivedData.startsWith("B000") && receivedData.length == 48 && !activationStaing) {
                            if (BinaryChange.proofData(receivedData.substring(0, 42)) == receivedData.subSequence(42, 44)) {
                                var state = receivedData.substring(4, 6)
                                var activationCode = ActivationCode.makeCode(receivedData.substring(6, 30))
                                BaseSharedPreferences.put("activationCode", BinaryChange.hexToCap(activationCode))
                                BaseSharedPreferences.put("deviceCode", BinaryChange.hexToCap(receivedData.substring(6, 30)))
                                when (state) {
                                    "00" -> {
                                        activationStaing = true
                                        "未激活".showToast(requireContext())
                                        DialogUtil.noActivitionDialog(activity, object : DialogSureCallBack {
                                            override fun sureCallBack(data: String) {
                                                mSerialPortHelper.sendTxt(SerialPortDataMake.activationData(activationCode))
                                                activationStaing = false
                                            }

                                            override fun cancelCallBack(data: String) {
                                            }
                                        })
                                    }
                                    "01" -> "非法设备".showToast(activity)
                                    "02" -> {
                                        if (!activationStaing) {
                                            activationStaing = true
                                            "授权失效".showToast(activity)
                                            var code = "${receivedData.substring(6, 30)}/$activationCode"
                                            DialogUtil.empowerDialog(activity, code, object : DialogSureCallBack {
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
                                                    activity.finish()
                                                }
                                            })
                                        }
                                    }
                                    "03" -> {
                                        "设备正常".showToast(activity)
                                        SerialPortConstant.timer.cancel()
                                        getConfigurationList()
                                    }
                                }
                            }
                        }
                    }
                }

                override fun onDataSend(bytes: ByteArray?) {
                    Log.e("ConfigFragment", "onDataSend: " + bytes?.let { BinaryChange.byteToHexString(it) })
                }
            })
        }
    }

    /**
     * 获取配置列表
     */
    private fun getConfigurationList() {
        /**将文件夹下所有文件名存入数组*/
        if (filePath.list() == null) {
            binding.linData.visibility = View.GONE
            binding.linNoData.visibility = View.VISIBLE
            DialogUtil.configurationDialog(activity,object : DialogSureCallBack{
                override fun sureCallBack(data: String) {
                    //确认
                    val dataList = data.split("/")
                    BaseFileUtil.saveConfig(dataList)
                    (activity as ConfigurationActivity).setViewPageItem(1)
                }
                override fun cancelCallBack(data: String) {
                    //保存配置
                    dialog = DialogUtil.configurationNameDialog(activity,object : DialogSureCallBack{
                        override fun sureCallBack(configname: String) {
                            // 需要检查的文件名是否重名
                            BaseFileUtil.haveName(filePath,"${configname}.txt",this@ConfigFragment,data, activity as ConfigurationActivity)
                        }
                        override fun cancelCallBack(data: String) {
                        }
                    })
                }
            })
        }else{
            if (filePath.list().isNotEmpty()){
                binding.linData.visibility = View.VISIBLE
                binding.linNoData.visibility = View.GONE
                pathList = filePath.list().reversed() as ArrayList<String>

                val layoutManager = LinearLayoutManager(activity)
                binding.recyclerView.layoutManager = layoutManager
                adapter = ConfigListAdapter(pathList, selectIndex, activity, object : AdapterPositionCallBack {
                    override fun backPosition(index: Int) {
                        selectIndex = index
                        setConfig(filePath,index)
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    override fun backLongPosition(index: Int) {
                        DialogUtil.delectDialog(activity,object : DialogSureCallBack{
                            override fun sureCallBack(data: String) {
                                val file = File("${filePath}/${pathList[selectIndex]}")
                                file.delete()
                                pathList.removeAt(selectIndex)
                                if (selectIndex == 0) {
                                    //如果只有一条数据删除后显示暂无数据图片，隐藏listview
                                    if (pathList.size == 1) {
                                        binding.linData.visibility = View.GONE
                                        binding.linNoData.visibility = View.VISIBLE
                                    } else {
                                        setConfig(filePath,selectIndex)
                                    }
                                } else if (selectIndex == pathList.size - 1) {
                                    --selectIndex
                                    adapter.setSelectIndex(selectIndex)
                                    setConfig(filePath,selectIndex)
                                } else if (selectIndex < pathList.size - 1 && selectIndex > 0) {
                                    --selectIndex
                                    adapter.setSelectIndex(selectIndex)
                                    setConfig(filePath,selectIndex)
                                }
                                adapter.notifyDataSetChanged()
                            }
                            override fun cancelCallBack(data: String) {
                            }
                        })
                    }
                })
                binding.recyclerView.adapter = adapter
                setConfig(filePath,selectIndex)
                adapter.notifyDataSetChanged()
                binding.smartRefreshLayout.finishLoadMore()
                binding.smartRefreshLayout.finishRefresh()
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.S)
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnSelect->{
                var path = "${filePath}/${pathList[selectIndex]}"
                val text = File(path).readText()
                var textList = text.split("/")
                BaseFileUtil.saveConfig(textList)
                (activity as ConfigurationActivity).setViewPageItem(1)
            }
        }
    }

    fun setConfig(filePath: File, index: Int) {
        var path = "${filePath}/${pathList[index]}"
        val text = File(path).readText()
        var textList = text.split("/")
        binding.tvWorkpieceType.text = textList[0]
        binding.tvWorkpieceFrom.text = textList[1]
        binding.tvWorkpieceQuality.text = textList[2]
        binding.tvThickness.text = textList[3]
        binding.tvWidth.text = textList[4]
        binding.tvHeat.text = textList[5]
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
                mSerialPortHelper.sendTxt(SerialPortDataMake.empowerData(acmfCode.activationCode.substring(40, 60)))
            }
        }
    }

    override fun setAcmfCodeMessage(message: String?) {
        message?.showToast(activity)
        activationStaing = false
    }

    override fun onDestroy() {
        super.onDestroy()
        mSerialPortHelper.nativeClose()
        _binding = null
    }
}
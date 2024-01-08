package com.example.lkacmf.util.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.example.lkacmf.MyApplication
import com.example.lkacmf.R
import com.example.lkacmf.activity.ConfigurationActivity
import com.example.lkacmf.util.BinaryChange
import com.example.lkacmf.util.DateUtil
import com.example.lkacmf.util.pio.XwpfTUtil
import com.example.lkacmf.util.qr.QrCodeUtil
import com.example.lkacmf.util.showToast
import com.example.lkacmf.util.sp.BaseSharedPreferences
import com.permissionx.guolindev.PermissionX
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_configuration.*
import kotlinx.android.synthetic.main.dialog_configuration_name.*
import kotlinx.android.synthetic.main.dialog_delect_image.*
import kotlinx.android.synthetic.main.dialog_empower.*
import kotlinx.android.synthetic.main.dialog_form.*
import kotlinx.android.synthetic.main.dialog_form.btnFormCancel
import kotlinx.android.synthetic.main.dialog_form.btnFormSure
import kotlinx.android.synthetic.main.dialog_form.etDepthFactor
import kotlinx.android.synthetic.main.dialog_no_activation.*
import kotlinx.android.synthetic.main.dialog_setting.*
import kotlinx.android.synthetic.main.dialog_thinkness.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object DialogUtil {
    /**
     * 初始化重新扫描扫描dialog
     */
    @SuppressLint("StaticFieldLeak")
    private lateinit var dialog: MaterialDialog

    /**
     * 是否激活弹窗
     */
    fun noActivitionDialog(activity: Activity, callBack: DialogSureCallBack): MaterialDialog {
        dialog = MaterialDialog(activity)
            .cancelable(false)
            .show {
                customView(    //自定义弹窗
                    viewRes = R.layout.dialog_no_activation,//自定义文件
                    dialogWrapContent = true,    //让自定义宽度生效
                    scrollable = true,            //让自定义宽高生效
                    noVerticalPadding = true    //让自定义高度生效
                )
                cornerRadius(16f)
            }
        dialog.btnNoActivationCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.btnNoActivationSure.setOnClickListener {
            callBack.sureCallBack("")
        }
        return dialog
    }

    /**
     * 设备授权码
     */
    fun empowerDialog(activity: ConfigurationActivity, code:String, callBack: DialogSureCallBack): MaterialDialog {
        dialog = MaterialDialog(activity)
            .cancelable(false)
            .show {
                customView(    //自定义弹窗
                    viewRes = R.layout.dialog_empower,//自定义文件
                    dialogWrapContent = true,    //让自定义宽度生效
                    scrollable = true,            //让自定义宽高生效
                    noVerticalPadding = true    //让自定义高度生效
                )
                cornerRadius(16f)
            }
        val bitmap = QrCodeUtil.createQRCodeBitmap(code, 380, 380)
        dialog.ivQr.setImageBitmap(bitmap)
//        dialog.tvEmpowerCode.text = code
        dialog.btnEmPowerCancel.setOnClickListener {
            dialog.dismiss()
            callBack.sureCallBack("")
        }
        dialog.btnEmPowerSure.setOnClickListener {
            callBack.sureCallBack("")
        }
        return dialog
    }

    /**
     * 添加配置
     */
    fun configurationDialog(activity: Activity, callBack: DialogSureCallBack): MaterialDialog {
        dialog = MaterialDialog(activity)
            .cancelable(false)
            .show {
                customView(    //自定义弹窗
                    viewRes = R.layout.dialog_configuration,//自定义文件
                    dialogWrapContent = true,    //让自定义宽度生效
                    scrollable = true,            //让自定义宽高生效
                    noVerticalPadding = true    //让自定义高度生效
                )
                cornerRadius(16f)
            }
        val workpieceTypeSet : List<String> = LinkedList(listOf("母材", "焊缝"))
        dialog.nsWorkpieceType.attachDataSource(workpieceTypeSet) //设置下拉框要显示的数据集合
        dialog.nsWorkpieceType.setBackgroundResource(R.drawable.nice_spinner) //设置控件的形状和背景

        val workpieceFromSet: List<String> = LinkedList(listOf("管本体", "板本体", "管对接", "板对接", "角焊缝", "管座焊缝"))
        dialog.nsWorkpieceFrom.attachDataSource(workpieceFromSet)
        dialog.nsWorkpieceFrom.setBackgroundResource(R.drawable.nice_spinner)

        val workpieceQualitySet: List<String> = LinkedList(listOf("碳钢", "不锈钢", "铝", "钛"))
        dialog.nsWorkpieceQuality.attachDataSource(workpieceQualitySet)
        dialog.nsWorkpieceQuality.setBackgroundResource(R.drawable.nice_spinner)
        dialog.btnConfigSave.setOnClickListener {
            dialog.dismiss()
        }

        dialog.btnConfigSave.setOnClickListener {
            dialog.dismiss()
            var thickness = dialog.etThickness.text.toString()
            var width = dialog.etWidth.text.toString()
            var heat = dialog.etHeat.text.toString()
            if (thickness.trim { it <= ' ' } == "") {
                "焊缝厚度不能为空".showToast(activity)
                return@setOnClickListener
            }
            if (width.trim { it <= ' ' } == "") {
                "焊缝宽度不能为空".showToast(activity)
                return@setOnClickListener
            }
            if (heat.trim { it <= ' ' } == "") {
                "工件温度不能为空".showToast(activity)
                return@setOnClickListener
            }
            var configuration =  "${dialog.nsWorkpieceType.text}/${dialog.nsWorkpieceFrom.text}/${dialog.nsWorkpieceQuality.text}/$thickness/$width/$heat"
            callBack.cancelCallBack(configuration)
        }
        dialog.btnConfigSure.setOnClickListener {
            var thickness = dialog.etThickness.text.toString()
            var width = dialog.etWidth.text.toString()
            var heat = dialog.etHeat.text.toString()
            if (thickness.trim { it <= ' ' } == "") {
                "焊缝厚度不能为空".showToast(activity)
                return@setOnClickListener
            }
            if (width.trim { it <= ' ' } == "") {
                "焊缝宽度不能为空".showToast(activity)
                return@setOnClickListener
            }
            if (heat.trim { it <= ' ' } == "") {
                "工件温度不能为空".showToast(activity)
                return@setOnClickListener
            }
            var configuration =  "${dialog.nsWorkpieceType.text}/${dialog.nsWorkpieceFrom.text}/${dialog.nsWorkpieceQuality.text}/$thickness/$width/$heat"
            callBack.sureCallBack(configuration)
        }
        return dialog
    }

    /**
     * 删除配置
     */
    fun delectDialog(activity: Activity, callBack: DialogSureCallBack): MaterialDialog {
        dialog = MaterialDialog(activity).show {
            customView(    //自定义弹窗
                viewRes = R.layout.dialog_delect_image,//自定义文件
                dialogWrapContent = true,    //让自定义宽度生效
                scrollable = true,            //让自定义宽高生效
                noVerticalPadding = true    //让自定义高度生效
            )
            cornerRadius(16f)  //圆角
        }
        dialog.btnDelectCancel.setOnClickListener {
            callBack.cancelCallBack("")
            dialog.dismiss()
        }
        dialog.btnDelectSure.setOnClickListener {
            callBack.sureCallBack("")
            dialog.dismiss()
        }
        return dialog
    }

    /**
     * 配置名称
     */
    fun configurationNameDialog(activity: Activity, callBack: DialogSureCallBack): MaterialDialog {
        dialog = MaterialDialog(activity)
            .cancelable(false)
            .show {
                customView(    //自定义弹窗
                    viewRes = R.layout.dialog_configuration_name,//自定义文件
                    dialogWrapContent = true,    //让自定义宽度生效
                    scrollable = true,            //让自定义宽高生效
                    noVerticalPadding = true    //让自定义高度生效
                )
                cornerRadius(16f)
            }
        dialog.btnConfigNameCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.btnConfigNameSure.setOnClickListener {
            var configName = dialog.etConfigName.text.toString()
            if (configName.trim { it <= ' ' } == "") {
                callBack.sureCallBack(DateUtil.getNowDate())
            }else{
                callBack.sureCallBack(configName)
            }
        }
        return dialog
    }

    /**
    权限申请
     */
    @RequiresApi(Build.VERSION_CODES.S)
    fun requestPermission(activity: FragmentActivity, requestList: ArrayList<String>): Boolean {
        var permissionTag = false
        if (requestList.isNotEmpty()) {
            PermissionX.init(activity)
                .permissions(requestList)
                .onExplainRequestReason { scope, deniedList ->
                    val message = "需要您同意以下权限才能正常使用"
                    scope.showRequestReasonDialog(deniedList, message, "同意", "取消")
                }
                .request { allGranted, _, deniedList ->
                    if (allGranted) {
                        Log.e("TAG", "所有申请的权限都已通过")
                        permissionTag = true
                    } else {
                        Log.e("TAG", "您拒绝了如下权限：$deniedList")
                        activity.finish()
                    }
                }
        }
        return permissionTag
    }

    /**
     * 设备设置
     */
    fun settingDialog(activity: Activity, callBack: DialogSureCallBack): MaterialDialog {
        dialog = MaterialDialog(activity)
            .cancelable(false)
            .show {
                customView(    //自定义弹窗
                    viewRes = R.layout.dialog_setting,//自定义文件
                    dialogWrapContent = true,    //让自定义宽度生效
                    scrollable = true,            //让自定义宽高生效
                    noVerticalPadding = true    //让自定义高度生效
                )
                cornerRadius(16f)
            }
        when (BaseSharedPreferences.get("userEncode", "")) {
            "00" -> dialog.tabLayoutEncode.getTabAt(0)?.select()
            "01" -> dialog.tabLayoutEncode.getTabAt(1)?.select()
        }
        dialog.tvProbeNumb.text = BinaryChange.hexToInt(BaseSharedPreferences.get("probeNumb", "")).toString()
        dialog.sbProbeNumb.progress = BinaryChange.hexToInt(BaseSharedPreferences.get("probeNumb", ""))
        when (BaseSharedPreferences.get("probeRate", "")) {
            "00" -> dialog.tabLayoutRate.getTabAt(0)?.select()
            "01" -> dialog.tabLayoutRate.getTabAt(1)?.select()
            "02" -> dialog.tabLayoutRate.getTabAt(2)?.select()
        }
        when (BaseSharedPreferences.get("currentWave", "")) {
            "00" -> dialog.tabLayoutWave.getTabAt(0)?.select()
            "01" -> dialog.tabLayoutWave.getTabAt(1)?.select()
            "02" -> dialog.tabLayoutWave.getTabAt(2)?.select()
        }
        dialog.sbProbeNumb.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                dialog.tvProbeNumb.text = "$progress"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })

        dialog.btnSettingCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.btnSettingSure.setOnClickListener {
            when (dialog.tabLayoutEncode.selectedTabPosition) {
                0 -> BaseSharedPreferences.put("userEncoder", "00")
                1 -> BaseSharedPreferences.put("userEncoder", "01")
            }
            when (dialog.tabLayoutRate.selectedTabPosition) {
                0 -> BaseSharedPreferences.put("probeRate", "00")
                1 -> BaseSharedPreferences.put("probeRate", "01")
                2 -> BaseSharedPreferences.put("probeRate", "02")
            }
            when (dialog.tabLayoutWave.selectedTabPosition) {
                0 -> BaseSharedPreferences.put("currentWave", "00")
                1 -> BaseSharedPreferences.put("currentWave", "01")
                2 -> BaseSharedPreferences.put("currentWave", "02")
            }
            BaseSharedPreferences.put("probeNumb",BinaryChange.tenToHex((dialog.tvProbeNumb.text as String).toInt()))
            callBack.sureCallBack("")
            dialog.dismiss()
        }
        return dialog
    }

    /**
     * 设置图层
     */
    fun setThinkness(activity: Activity, callBack: DialogSureCallBack) {
        dialog = MaterialDialog(activity)
            .cancelable(false)
            .show {
                customView(    //自定义弹窗
                    viewRes = R.layout.dialog_thinkness,//自定义文件
                    dialogWrapContent = true,    //让自定义宽度生效
                    scrollable = true,            //让自定义宽高生效
                    noVerticalPadding = true    //让自定义高度生效
                )
                cornerRadius(16f)
            }
        dialog.btnThinknessCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.btnThinknessSure.setOnClickListener {
            var thinkness = dialog.etThinkness.text.toString()
            if (thinkness.trim { it <= ' ' } == "") {
                "图层厚度不能为空".showToast(activity)
                return@setOnClickListener
            }
            callBack.sureCallBack(thinkness)
            dialog.dismiss()
        }
    }

    /**
     * 生成报告
     */
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission")
    fun writeFormDataDialog(activity: Activity, bitmapList:ArrayList<Bitmap>) {
//        CoroutineScope(Dispatchers.Main)
//            .launch {
        dialog = MaterialDialog(activity)
            .cancelable(false)
            .show {
                customView(    //自定义弹窗
                    viewRes = R.layout.dialog_form,//自定义文件
                    dialogWrapContent = true,    //让自定义宽度生效
                    scrollable = true,            //让自定义宽高生效
                    noVerticalPadding = true    //让自定义高度生效
                )
                cornerRadius(16f)
            }
        dialog.btnFormCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.btnFormSure.setOnClickListener {
            var person = dialog.etPerson.text.toString()
            var code = dialog.etCode.text.toString()
            var depthFactor = dialog.etDepthFactor.text.toString()
            if (person.trim { it <= ' ' } == "") {
                "操作人员不能为空".showToast(activity)
                return@setOnClickListener
            }
            if (code.trim { it <= ' ' } == "") {
                "部件编号不能为空".showToast(activity)
                return@setOnClickListener
            }
            if (depthFactor.trim { it <= ' ' } == "") {
                "深度系数不能为空".showToast(activity)
                return@setOnClickListener
            }

            var date = DateUtil.timeFormatChange()
            //显示截图
            val dataMap: MutableMap<String, Any> = HashMap()
            dataMap["date"] = date
            dataMap["person"] = person
            dataMap["device"] = "ACMF-X2"
            dataMap["probe"] = "ACMF"
            dataMap["version"] = "V1.0.1"
            dataMap["encoder"] = "null"
            dataMap["level"] = "10"
            dataMap["material"] = "探头文件"
            dataMap["unitcode"] = "不见编号"
            dataMap["direction"] = "Z"
            dataMap["probenum"] = "1"
            dataMap["proberate"] = "20%"
            dataMap["depth"] = "1.2"

            val templetDocPath = activity.assets.open("acmf.docx")
            var saveFormState = XwpfTUtil.writeDocx(activity, templetDocPath, dataMap, bitmapList)
            if (saveFormState) {
                MyApplication.context.resources.getString(R.string.save_success).showToast(activity)
                dialog.dismiss()
            } else {
                MyApplication.context.resources.getString(R.string.save_fail).showToast(activity)
                dialog.dismiss()
            }
        }
    }

}
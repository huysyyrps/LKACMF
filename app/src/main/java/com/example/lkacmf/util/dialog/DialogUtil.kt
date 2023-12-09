package com.example.lkacmf.util.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.example.lkacmf.R
import com.example.lkacmf.activity.MainActivity
import com.example.lkacmf.util.BinaryChange
import com.example.lkacmf.util.showToast
import com.example.lkacmf.util.sp.BaseSharedPreferences
import com.permissionx.guolindev.PermissionX
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_empower.*
import kotlinx.android.synthetic.main.dialog_no_activation.*
import kotlinx.android.synthetic.main.dialog_setting.*
import kotlinx.android.synthetic.main.dialog_thinkness.*

object DialogUtil {
    /**
     * 初始化重新扫描扫描dialog
     */
    @SuppressLint("StaticFieldLeak")
    private lateinit var dialog: MaterialDialog

    /**
     * 是否激活弹窗
     */
    fun noActivitionDialog(activity: MainActivity, callBack: DialogSureCallBack): MaterialDialog {
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
    fun empowerDialog(activity: MainActivity, qrCode: Bitmap, callBack: DialogSureCallBack): MaterialDialog {
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
        dialog.ivQr.setImageBitmap(qrCode)
        dialog.btnEmPowerCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.btnEmPowerSure.setOnClickListener {
            callBack.sureCallBack("")
        }
        return dialog
    }

    /**
    权限申请
     */
    @RequiresApi(Build.VERSION_CODES.S)
    fun requestPermission(activity: AppCompatActivity, requestList: ArrayList<String>): Boolean {
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
     * 设备授权码
     */
    fun settingDialog(activity: MainActivity, callBack: DialogSureCallBack): MaterialDialog {
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
            BaseSharedPreferences.put("probeNumb",dialog.tvProbeNumb.text)
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
}
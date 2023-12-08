package com.example.lkacmf.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.lkacmf.R
import com.example.lkacmf.util.dialog.DialogUtil
import com.huawei.hms.hmsscankit.OnResultCallback
import com.huawei.hms.hmsscankit.RemoteView
import com.huawei.hms.ml.scan.HmsScan
import kotlinx.android.synthetic.main.activity_qr.*

class QrActivity : AppCompatActivity() {
    var mScreenWidth = 0
    var mScreenHeight = 0
    var tag = "first"
    var permissionTag = false
    private var remoteView: RemoteView? = null
    companion object {
        fun actionStart(context: Context) {
            val intent = Intent(context, QrActivity::class.java)
            context.startActivity(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr)
        val requestList = ArrayList<String>()
        requestList.add(Manifest.permission.CAMERA)
        requestList.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        permissionTag = DialogUtil.requestPermission(this,requestList)

        val dm = resources.displayMetrics
        val density = dm.density
        mScreenWidth = resources.displayMetrics.widthPixels
        mScreenHeight = resources.displayMetrics.heightPixels
        //当前demo扫码框的宽高是300dp
        val SCAN_FRAME_SIZE = 300
        val scanFrameSize = (SCAN_FRAME_SIZE * density).toInt()
        val rect = Rect()
        rect.left = mScreenWidth / 2 - scanFrameSize / 2
        rect.right = mScreenWidth / 2 + scanFrameSize / 2
        rect.top = mScreenHeight / 2 - scanFrameSize / 2
        rect.bottom = mScreenHeight / 2 + scanFrameSize / 2
        //初始化RemoteView，并通过如下方法设置参数:setContext()（必选）传入context、setBoundingBox()设置扫描区域、setFormat()设置识别码制式，设置完毕调用build()方法完成创建
        remoteView = RemoteView.Builder().setContext(this).setBoundingBox(rect).setFormat(HmsScan.QRCODE_SCAN_TYPE, HmsScan.DATAMATRIX_SCAN_TYPE).build()
        remoteView?.onCreate(savedInstanceState)
        val params = FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        frameLayout.addView(remoteView, params)
        //请求相机权限
        if (permissionTag) {
            // 授予权限后操作
            remoteView?.onStart()
        }
        remoteView?.setOnResultCallback(OnResultCallback { result ->
            if (tag == "first") {
                if (result != null && result.isNotEmpty() && result[0] != null) {
                    if (result[0].getOriginalValue() != null) {
                        val qrData = result[0].getOriginalValue()
                        Log.e("DefinedActivity", qrData)
                        tag = "second"
                    }
                }
            }
        })
    }
}
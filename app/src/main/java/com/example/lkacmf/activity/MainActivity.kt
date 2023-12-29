package com.example.lkacmf.activity
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.lkacmf.R
import com.example.lkacmf.databinding.ActivityMainBinding
import com.example.lkacmf.entity.VersionInfo
import com.example.lkacmf.fragment.AnalystsFragment
import com.example.lkacmf.fragment.HomeFragment
import com.example.lkacmf.fragment.UserInfoFragment
import com.example.lkacmf.module.VersionInfoContract
import com.example.lkacmf.presenter.VersionInfoPresenter
import com.example.lkacmf.util.BaseActivity
import com.example.lkacmf.util.BaseTelPhone
import com.example.lkacmf.util.dialog.DialogUtil
import com.example.lkacmf.util.showToast
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import constant.UiType
import listener.OnInitUiListener
import model.UiConfig
import model.UpdateConfig
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import update.UpdateAppUtils


class MainActivity : BaseActivity(), View.OnClickListener, VersionInfoContract.View  {
    private lateinit var binding: ActivityMainBinding
    private var version: String = "1.0.0"
    private lateinit var versionInfoPresenter: VersionInfoPresenter
    private lateinit var mFm: FragmentManager
    private val mFragmentList = ArrayList<Fragment>()
    private val mFragmentTagList = arrayOf("OneFragment", "TwoFragment", "ThreeFragment")
    private lateinit var mCurrentFragmen: Fragment  // 记录当前显示的Fragment

    companion object {
        fun actionStart(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        versionInfoPresenter = VersionInfoPresenter(this, view = this)
        mFragmentList.add(0, HomeFragment())
        mFragmentList.add(1, AnalystsFragment())
        mFragmentList.add(2, UserInfoFragment())
        mCurrentFragmen = mFragmentList[0]
        // 初始化首次进入时的Fragment
        mFm = supportFragmentManager;
        val transaction: FragmentTransaction = mFm.beginTransaction()
        transaction.add(R.id.frameLayout, mCurrentFragmen, mFragmentTagList[0])
        transaction.commitAllowingStateLoss()
        //tabLayout选择监听
        tabLayoutSelect()

        binding.imageView.setOnClickListener(this)
        binding.linImageList.setOnClickListener(this)
        binding.linFileList.setOnClickListener(this)
        binding.linVersionCheck.setOnClickListener(this)
        binding.linContactComp.setOnClickListener(this)
        binding.btnFinish.setOnClickListener(this)

        val requestList = ArrayList<String>()
        requestList.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        requestList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        requestList.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        requestList.add(Manifest.permission.ACCESS_FINE_LOCATION)
        requestList.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
        DialogUtil.requestPermission(this,requestList)
    }

    private fun tabLayoutSelect() {
        binding.tbLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0->{
                        switchFragment(mFragmentList[0], mFragmentTagList[0])
                        return
                    }
                    1->{
                        switchFragment(mFragmentList[1], mFragmentTagList[1])
                        return
                    }
                    2->{
                        switchFragment(mFragmentList[2], mFragmentTagList[2])
                        return
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.imageView -> {
                binding.drawerLayout.openDrawer(GravityCompat.START)
            }
            R.id.linImageList -> {
                ImageListActivity.actionStart(this)
            }
            R.id.linFileList -> {
                ////LKACMFFORM%2f"
//                val path = "%2fandroid%2fdata%2fcom.example.lkacmf%2fcache%2f"
////                val path = "%2fstorage%2femulated%2f0%2fLKACMFFORM%2f"
//                val uri =
//                    Uri.parse("content://com.android.externalstorage.documents/document/primary:$path")
////                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
////                intent.addCategory(Intent.CATEGORY_OPENABLE)
//                val intent = Intent(Intent.ACTION_GET_CONTENT)
//                intent.type = "*/*" //想要展示的文件类型
//                intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri)
//                startActivity(intent)

                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                //设置读写权限
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                //指定选择文本类型的文件
                intent.type = "*/*"
                startActivity(intent)
                //指定多类型查询
//                intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("DOCX"))
//                startActivityForResult(intent, 10402)

//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
//                    // 应用没有`MANAGE_EXTERNAL_STORAGE`权限，请求用户授予该权限
//                    LogUtil.e("TAG","0")
//                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
//                    val uri = Uri.fromParts("package", packageName, null)
//                    intent.data = uri
//                    startActivityForResult(intent, Constant.TAG_ONE)
//
//                } else {
//                    LogUtil.e("TAG","1")
//                    // 应用已具有`MANAGE_EXTERNAL_STORAGE`权限
//                    // 继续下一步操作
//                }
            }
            R.id.linVersionCheck -> {
                versionInfo()
            }
            R.id.linContactComp -> {
                BaseTelPhone.telPhone(this)
            }
            R.id.btnFinish -> {
                finish()
            }
        }
    }

    // 转换Fragment
    fun switchFragment(to: Fragment, tag: String?) {
        if (mCurrentFragmen !== to) {
            val transaction = mFm.beginTransaction()
            if (!to.isAdded) {
                // 没有添加过:
                // 隐藏当前的，添加新的，显示新的
                transaction.hide(mCurrentFragmen).add(R.id.frameLayout, to, tag).show(to)
            } else {
                // 隐藏当前的，显示新的
                transaction.hide(mCurrentFragmen).show(to)
            }
            mCurrentFragmen = to
            transaction.commitAllowingStateLoss()
        }
    }

    /**
     * 请求版本信息
     */
    private fun versionInfo() {
        val params = HashMap<String, String>()
        params["projectName"] = "济宁鲁科"
        params["actionName"] = "ACMF"
        params["appVersion"] = version
        params["channel"] = "default"
        params["appType"] = "android"
        params["clientType"] = "X2"
        params["phoneSystemVersion"] = "10.0.1"
        params["phoneType"] = "华为"
        val gson = Gson()
        val requestBody = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            gson.toJson(params)
        )
        versionInfoPresenter.getVersionInfo(requestBody)
    }

    override fun setVersionInfo(versionInfo: VersionInfo?) {
        val netVersion = versionInfo?.data?.version
        val netVersionArray = netVersion?.split(".")?.toTypedArray()
        val localVersionArray = version.split(".").toTypedArray()
        if (netVersionArray != null) {
            for (i in netVersionArray.indices) {
                if (netVersionArray[i].toInt() > localVersionArray[i].toInt()) {
                    if (versionInfo.data.updateFlag === 0) {
                        //无需SSH升级,APK需要升级时值为0
                        showUpDataDialog(versionInfo, 0)
                        return
                    } else if (versionInfo.data.updateFlag === 1) {
                        //SSH需要升级APK不需要升级
                        showUpDataDialog(versionInfo, 1)
                        return
                    } else if (versionInfo.data.updateFlag === 2) {
                        showUpDataDialog(versionInfo, 2)
                        return
                    }
                }
            }
        }
        R.string.last_version.showToast(this)
    }

    private fun showUpDataDialog(versionInfo: VersionInfo, i: Int) {
        val updateInfo: String = versionInfo.data.updateInfo
        val updataItem: Array<String> = updateInfo.split("~").toTypedArray()
        var updateInfo1 = ""
        if (updataItem != null && updataItem.isNotEmpty()) {
            for (j in updataItem.indices) {
                updateInfo1 = "$updateInfo1 \n ${updataItem[j]}"
            }
        }
        UpdateAppUtils
            .getInstance()
            .apkUrl(versionInfo.data.apkUrl)
            .updateConfig(UpdateConfig(alwaysShowDownLoadDialog = true))
            .uiConfig(
                UiConfig(
                    uiType = UiType.CUSTOM,
                    customLayoutId = R.layout.view_update_dialog_custom
                )
            )
            .setOnInitUiListener(object : OnInitUiListener {
                @SuppressLint("SetTextI18n")
                override fun onInitUpdateUi(
                    view: View?,
                    updateConfig: UpdateConfig,
                    uiConfig: UiConfig
                ) {
                    view?.findViewById<TextView>(R.id.tvUpdateTitle)?.text =
                        "${applicationContext.resources.getString(R.string.have_new_version)}${versionInfo.data.version}"
                    view?.findViewById<TextView>(R.id.tvVersionName)?.text =
                        "V${versionInfo.data.version}"
                    view?.findViewById<TextView>(R.id.tvUpdateContent)?.text =
                        updateInfo1
                    // do more...
                }
            })
            .update()
    }

    override fun setVersionInfoMessage(message: String?) {
        message?.let { it.showToast(this) }
    }
}
package com.example.lkacmf.activity

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.afollestad.materialdialogs.MaterialDialog
import com.example.lkacmf.R
import com.example.lkacmf.databinding.ActivityConfigurationBinding
import com.example.lkacmf.fragment.ConfigFragment
import com.example.lkacmf.fragment.RecommendFragment
import com.example.lkacmf.serialport.SerialPortConstant
import com.example.lkacmf.util.*
import com.example.lkacmf.util.dialog.DialogSureCallBack
import com.example.lkacmf.util.dialog.DialogUtil
import com.example.lkacmf.util.file.BaseFileUtil
import me.f1reking.serialportlib.SerialPortHelper

class ConfigurationActivity : AppCompatActivity(), View.OnClickListener {
    var dialog: MaterialDialog? = null
    val childFragments = arrayListOf<Fragment>()
    lateinit var binding: ActivityConfigurationBinding
//    private val mSerialPortHelper: SerialPortHelper by lazy { SerialPortConstant.getSerialPortHelper(this) }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfigurationBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        binding.btnAddConfig.setOnClickListener(this)
        initViewPage()

    }
    private fun initViewPage() {
        childFragments.add(ConfigFragment())
        childFragments.add(RecommendFragment())
        childFragments.add(ConfigFragment())
//        //设置adapter
        binding.viewpager.adapter = fragmentAdapter
        //设置viewpage的滑动方向
        binding.viewpager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        //禁止滑动
        // viewpager.isUserInputEnabled = false
        //设置显示的页面，0：是第一页
        //viewpager.currentItem = 1
        //设置缓存页
        binding.viewpager.offscreenPageLimit = childFragments.size
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
                    }
                    2 -> {
                        binding.tvTitle.text = resources.getString(R.string.calibration)
                    }
                }
            }

        })
    }

    fun setViewPageItem(index:Int){
        binding.viewpager.currentItem = index
    }

    /**
     * viewPager adapter
     */
    private val fragmentAdapter: FragmentStateAdapter by lazy {
        object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return childFragments.size
            }

            override fun createFragment(position: Int): Fragment {
                return childFragments[position]
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnAddConfig -> {
                DialogUtil.configurationDialog(this,object : DialogSureCallBack {
                    override fun sureCallBack(data: String) {
                        //确认
                        val dataList = data.split("/")
                        BaseFileUtil.saveConfig(dataList)
                    }
                    override fun cancelCallBack(data: String) {
                        //保存配置
                        dialog = DialogUtil.configurationNameDialog(this@ConfigurationActivity,object : DialogSureCallBack {
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

    override fun onDestroy() {
        super.onDestroy()
//        mSerialPortHelper.sendTxt("A10000000000000000A1")
    }
}
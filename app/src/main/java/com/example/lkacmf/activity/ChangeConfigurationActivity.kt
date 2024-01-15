package com.example.lkacmf.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.example.lkacmf.R
import com.example.lkacmf.adapter.ConfigListAdapter
import com.example.lkacmf.databinding.ActivityChangeConfigurationBinding
import com.example.lkacmf.util.AdapterPositionCallBack
import com.example.lkacmf.util.Constant
import com.example.lkacmf.util.dialog.DialogSureCallBack
import com.example.lkacmf.util.dialog.DialogUtil
import com.example.lkacmf.util.file.BaseFileUtil
import java.io.File

@RequiresApi(Build.VERSION_CODES.O)
class ChangeConfigurationActivity : AppCompatActivity(), View.OnClickListener {
    var dialog: MaterialDialog? = null
    var selectIndex = 0
    private lateinit var adapter: ConfigListAdapter
    val filePath : File = Constant.FILEPATH
    private var pathList = ArrayList<String>()
    lateinit var binding: ActivityChangeConfigurationBinding

    companion object {
        fun actionStart(context: Context) {
            val intent = Intent(context, ChangeConfigurationActivity::class.java)
            context.startActivity(intent)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeConfigurationBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        binding.btnSelect.setOnClickListener(this)
        getConfigurationList()
    }

    /**
     * 获取配置列表
     */
    fun getConfigurationList() {
        /**将文件夹下所有文件名存入数组*/
        if (filePath.list() == null||filePath.list().isEmpty()) {
            binding.linData.visibility = View.GONE
            binding.linNoData.visibility = View.VISIBLE
            DialogUtil.configurationDialog(this,object : DialogSureCallBack {
                override fun sureCallBack(data: String) {
                    //确认
                    val dataList = data.split("/")
                    BaseFileUtil.saveConfig(dataList)
                }
                override fun cancelCallBack(data: String) {
                    //保存配置
                    dialog = DialogUtil.configurationNameDialog(this@ChangeConfigurationActivity,object : DialogSureCallBack {
                        override fun sureCallBack(configname: String) {
                            // 需要检查的文件名是否重名
                            BaseFileUtil.haveNameActivity(filePath,"${configname}.txt",this@ChangeConfigurationActivity,data)
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

                val layoutManager = LinearLayoutManager(this)
                binding.recyclerView.layoutManager = layoutManager
                adapter = ConfigListAdapter(pathList, selectIndex, this, object : AdapterPositionCallBack {
                    override fun backPosition(index: Int) {
                        selectIndex = index
                        setConfig(filePath,index)
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    override fun backLongPosition(index: Int) {
                        DialogUtil.delectDialog(this@ChangeConfigurationActivity,object : DialogSureCallBack {
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
        binding.tvLayerThinkness.text = textList[6]
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnSelect->{
                var path = "${filePath}/${pathList[selectIndex]}"
                val text = File(path).readText()
                var textList = text.split("/")
                BaseFileUtil.saveConfig(textList)
                finish()
            }
        }
    }
}
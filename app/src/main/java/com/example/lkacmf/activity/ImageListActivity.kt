package com.example.lkacmf.activity

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.example.lkacmf.R
import com.example.lkacmf.adapter.ImageListAdapter
import com.example.lkacmf.databinding.ActivityImageListBinding
import com.example.lkacmf.util.AdapterPositionCallBack
import com.example.lkacmf.util.BaseActivity
import com.example.lkacmf.util.Constant
import com.example.lkacmf.util.StatusBarUtils
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import kotlinx.android.synthetic.main.dialog_delect_image.*
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import java.io.File

class ImageListActivity : BaseActivity() {
    var selectIndex = 0
    private var pathList = ArrayList<String>()
    private lateinit var dialog: MaterialDialog
    private lateinit var adapter: ImageListAdapter
    private lateinit var filePath: File
    private lateinit var binding: ActivityImageListBinding

    companion object {
        fun actionStart(context: Context) {
            val intent = Intent(context, ImageListActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtils.setWindowStatusBarColor(this, R.color.theme_color)
//        setContentView(R.layout.activity_image_list)
        binding = ActivityImageListBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager

        binding.smartRefreshLayout.setRefreshHeader(ClassicsHeader(this))
        binding.smartRefreshLayout.setRefreshFooter(ClassicsFooter(this)) //是否启用下拉刷新功能
        binding.smartRefreshLayout.setOnRefreshListener {
            getFileList()
        }

        binding.imageView.setOnLongClickListener {
            dialog = MaterialDialog(this).show {
                customView(    //自定义弹窗
                    viewRes = R.layout.dialog_delect_image,//自定义文件
                    dialogWrapContent = true,    //让自定义宽度生效
                    scrollable = true,            //让自定义宽高生效
                    noVerticalPadding = true    //让自定义高度生效
                )
                cornerRadius(16f)  //圆角
            }
            dialog.btnDelectCancel.setOnClickListener {
                dialog.dismiss()
            }
            dialog.btnDelectSure.setOnClickListener {
                if (selectIndex == 0) {
                    //如果只有一条数据删除后显示暂无数据图片，隐藏listview
                    if (pathList.size == 1) {
                        val file = File("${filePath}/${pathList[selectIndex]}")
                        file.delete()
                        pathList.removeAt(selectIndex)
                        binding.linNoData.visibility = View.VISIBLE
                        binding.linData.visibility = View.GONE
                    } else {
                        val file = File("${filePath}/${pathList[selectIndex]}")
                        file.delete()
                        pathList.removeAt(selectIndex)
                        adapter.notifyDataSetChanged()
                        var path = "${filePath}/${pathList[selectIndex]}"
                        setBitmap(path)
                    }
                } else if (selectIndex == pathList.size - 1) {
                    val file = File("${filePath}/${pathList[selectIndex]}")
                    file.delete()
                    pathList.removeAt(selectIndex)
                    --selectIndex
                    adapter.setSelectIndex(selectIndex)
                    adapter.notifyDataSetChanged()
                    var path = "${filePath}/${pathList[selectIndex]}"
                    setBitmap(path)
                } else if (selectIndex < pathList.size - 1 && selectIndex > 0) {
                    val file = File("${filePath}/${pathList[selectIndex]}")
                    file.delete()
                    pathList.removeAt(selectIndex)
                    --selectIndex
                    adapter.setSelectIndex(selectIndex)
                    adapter.notifyDataSetChanged()
                    var path = "${filePath}/${pathList[selectIndex]}"
                    setBitmap(path)
                }
                dialog.dismiss()
            }
            true
        }

        binding.ivImagelistBack.setOnClickListener {
            finish()
        }

        getFileList()
    }

    //获取数据列表
    private fun getFileList() {
        /**将文件夹下所有文件名存入数组*/
        filePath = File(this.externalCacheDir.toString()+ "/" + Constant.SAVE_IMAGE_PATH + "/")

        if (filePath==null||filePath.list()==null){
            binding.linNoData.visibility = View.VISIBLE
            binding.linData.visibility = View.GONE
            return
        }
        if (filePath.list().isEmpty()) {
            binding.linNoData.visibility = View.VISIBLE
            binding.linData.visibility = View.GONE
        } else {
            if (filePath.list().size > 1) {
                pathList = filePath.list().toList().reversed() as ArrayList<String>
            } else if (filePath.list().size == 1) {
                pathList.clear()
                pathList.add(filePath.list()[0])
            }
            binding.linNoData.visibility = View.GONE
            binding.linData.visibility = View.VISIBLE
            adapter = ImageListAdapter(pathList, selectIndex, this, object : AdapterPositionCallBack {
                    override fun backPosition(index: Int) {
                        selectIndex = index
                        var path = "${filePath}/${pathList[index]}"
                        setBitmap(path)
                    }

                override fun backLongPosition(longIndex: Int) {
                    TODO("Not yet implemented")
                }
            })
            binding.recyclerView.adapter = adapter
            var path = "${filePath}/${pathList[selectIndex]}"
            setBitmap(path)
            adapter.notifyDataSetChanged()
            binding.smartRefreshLayout.finishLoadMore()
            binding.smartRefreshLayout.finishRefresh()
        }
    }

    private fun setBitmap(path: String) {
        Luban.with(this)
            .load(path)
            .ignoreBy(100)
            .filter { path ->
                !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif"))
            }
            .setCompressListener(object : OnCompressListener {
                override fun onStart() {
                    // TODO 压缩开始前调用，可以在方法内启动 loading UI
                }

                override fun onSuccess(file: File) {
                    val bitmap = BitmapFactory.decodeFile(path)
                    binding.imageView.setImageBitmap(bitmap)
                    binding.tvFileName.text = pathList[selectIndex]
                }

                override fun onError(e: Throwable) {

                }
            }).launch()
    }
}
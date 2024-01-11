package com.example.lkacmf.util.file

import android.app.Activity
import com.example.lkacmf.activity.ConfigurationActivity
import com.example.lkacmf.fragment.ConfigFragment
import com.example.lkacmf.util.dialog.DialogSureCallBack
import com.example.lkacmf.util.showToast
import com.example.lkacmf.util.sp.BaseSharedPreferences
import java.io.File

object BaseFileUtil {
    fun haveName(filePath: File, fileToCheckName: String, fragment: ConfigFragment, data: String, activity: ConfigurationActivity) {
        //如果不存在  就mkdirs()创建此文件夹
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        if (filePath.exists()) {
            // 获取文件夹内容
            val filesInFolder = filePath.listFiles() ?: emptyArray<File>()
            for (file in filesInFolder) {
                if (file.name == fileToCheckName && !file.isDirectory) {
                    "存在同名文件".showToast(activity)
                    return
                }
            }
            val filePath = "${filePath}/$fileToCheckName"
            File(filePath).writeText(data)
            "保存成功".showToast(activity)
            fragment.dialog?.dismiss()
            activity.dialog?.dismiss()
            (activity as ConfigurationActivity).setViewPageItem(1)
            var dataList = data.split("/")
            saveConfig(dataList)
        }
    }
    fun saveConfig(dataList: List<String>) {
        BaseSharedPreferences.put("nsWorkpieceType",dataList[0])
        BaseSharedPreferences.put("nsWorkpieceFrom",dataList[1])
        BaseSharedPreferences.put("nsWorkpieceQuality",dataList[2])
        BaseSharedPreferences.put("ncThickness",dataList[3])
        BaseSharedPreferences.put("ncWidth",dataList[4])
        BaseSharedPreferences.put("ncHeat",dataList[5])
        BaseSharedPreferences.put("ncLayerThinkness",dataList[6])
    }
}
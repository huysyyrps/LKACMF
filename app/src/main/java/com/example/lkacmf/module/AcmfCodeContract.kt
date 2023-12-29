package com.example.lkacmf.module

import com.example.lkacmf.entity.AcmfCode
import com.example.lkacmf.network.BaseEView
import com.example.lkacmf.network.BasePresenter
import okhttp3.RequestBody

interface AcmfCodeContract {
    interface View : BaseEView<presenter?> {
        //获取版本信息
        @Throws(Exception::class)
        fun setAcmfCode(acmfCode: AcmfCode?)
        fun setAcmfCodeMessage(message: String?)
    }

    interface presenter : BasePresenter {
        //版本信息回调
        fun getAcmfCode(company: RequestBody?)
    }
}
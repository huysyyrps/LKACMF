package com.example.lkacmf.presenter

import android.content.Context
import com.example.lkacmf.R
import com.example.lkacmf.entity.AcmfCode
import com.example.lkacmf.module.AcmfCodeContract
import com.example.lkacmf.network.BaseObserverNoEntry
import com.example.lkacmf.network.RetrofitUtil
import com.example.lkacmf.util.NetStat
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.RequestBody


class AcmfCodePresenter constructor(context : Context, view: AcmfCodeContract.View)  : AcmfCodeContract.presenter {

    var context: Context = context
    var view: AcmfCodeContract.View = view


    override fun getAcmfCode(company: RequestBody?) {
        RetrofitUtil().getInstanceRetrofit()?.initRetrofitMain()?.getAcmfCode(company)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object : BaseObserverNoEntry<AcmfCode?>(
                context!!,
                context!!.resources.getString(R.string.handler_data)
            ) {

                override fun onSuccees(t: AcmfCode?) {
                    if (t?.state == 200) {
                        view.setAcmfCode(t)
                    } else {
                        view.setAcmfCodeMessage(context.resources.getString(R.string.date_error))
                    }
                }

                override fun onFailure(e: Throwable?, isNetWorkError: Boolean) {
                    if (NetStat.isNetworkConnected(context)) {
                        view.setAcmfCodeMessage("" + e!!.message)
                    } else {
                        view.setAcmfCodeMessage(context.resources.getString(R.string.net_error))
                    }
                }
            })
    }
}

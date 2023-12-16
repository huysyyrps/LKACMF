package com.example.lkacmf

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import com.example.lkacmf.util.mediaprojection.MediaService


class MyApplication : Application() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        //截图service
        startService(Intent(this, MediaService::class.java))
    }
}
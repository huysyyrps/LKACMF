package com.example.lkacmf.util

import com.example.lkacmf.MyApplication
import java.io.File

object Constant {
    const val TAG_ONE = 1
    const val TAG_TWO = 2
    const val PORT = "/dev/ttyS8"
    const val ACTIVATIONHEADER = "A0"
    const val DATAHAVEACTIVATIONINFO = "00"
    const val DATAACTIVATIONINFO = "01"

    const val DATASETTINGHEADER = "A1"
    const val SETTINGID = "02"
    const val OPERATEID = "00"

    const val TIMEOUT = 3000L

    const val SAVE_IMAGE_PATH = "LKACMFIMAGE"
    const val SAVE_FORM_PATH = "LKACMFFORM"
    const val SAVE_CONFIGURATION_PATH = "LKACMFCONFIGURATION"
    const val COMPPHONE = "0537-2638599"//闸门

    val FILEPATH = File(MyApplication.context.externalCacheDir.toString() + "/" + SAVE_CONFIGURATION_PATH + "/")
}
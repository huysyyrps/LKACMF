package com.example.lkacmf.util

import java.lang.Float
import kotlin.String

/**
 * 根据设备ID生成激活码
 */
object ActivationCode {
    fun makeCode(deviceId:String):String{
        if (deviceId.length == 24) {
//            val hexA = deviceId.substring(6, 8) + deviceId.substring(4, 6) + deviceId.substring(2, 4) + deviceId.substring(0, 2)
//            val hexB = deviceId.substring(14, 16) + deviceId.substring(12, 14) + deviceId.substring(10, 12) + deviceId.substring(8, 10)
//            val hexC = deviceId.substring(22, 24) + deviceId.substring(20, 22) + deviceId.substring(18, 20) + deviceId.substring(16, 18)
            val hexA = deviceId.substring(0, 8)
            val hexB = deviceId.substring(8, 16)
            val hexC = deviceId.substring(16, 24)

            val floatA = Float.intBitsToFloat(Integer.valueOf(hexA, 16))
            val floatB = Float.intBitsToFloat(Integer.valueOf(hexB, 16))
            val floatC = Float.intBitsToFloat(Integer.valueOf(hexC, 16))

            val countA = (floatA + floatB) / floatC.toFloat()
            val countB = (floatA + floatC) / floatB.toFloat()
//            val countC = (intB + intC) / intA.toFloat()

            val hexThickenA: String = StringToHex(countA)
            val hexThickenB: String = StringToHex(countB)
//            val hexThickenC: String = StringToHex(countC)
            return hexThickenA + hexThickenB
        }
        return ""
    }

    fun StringToHex(data: kotlin.Float): String {
        return Integer.toHexString(Float.floatToIntBits(data))
    }
}
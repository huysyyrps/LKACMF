package com.example.lkacmf.util

import java.util.*

object BinaryChange {
    /**
     * 10进制转16进制
     */
    fun tenToHex(data:Int) = addZeroForNum(Integer.toHexString(data),2)

    /**
     * 16进制转int
     */
    fun hexToInt(data:String):Int{
        return Integer.valueOf(data, 16)
    }
    /**
     * 字母转大写
     */
    fun hexToCap(data:String):String{
        return data.uppercase(Locale.getDefault())
    }
    /**
     * 校验
     */
    open fun proofData(hexString: String?): String? {
        var hexString = hexString
        if (hexString == null || hexString == "") {
            return null
        }
        hexString = hexString.trim { it <= ' ' }
        hexString = hexString.uppercase()
        val length = hexString.length / 2
        var ad = 0
        for (i in 0 until length) {
            val pos = i * 2
            ad += Integer.valueOf(hexString.substring(pos,pos+2),16)
        }
        var checkData = Integer.toHexString(ad)
        if (checkData.length > 2) {
            checkData = checkData.substring(checkData.length - 2, checkData.length)
        }
        if (checkData.length == 1) {
            checkData = "0$checkData"
        }
        return checkData.uppercase()
    }

    /**
     * byte转核心String
     */
    fun byteToHexString(bytes: ByteArray): String {
        val hexStringBuffer = StringBuilder()
        for (b in bytes) {
            val hexString = String.format("%02X", b)
            // 将转换后的十六进制字符串添加到字符串缓冲区中
            hexStringBuffer.append(hexString)
        }
        return hexStringBuffer.toString();
    }

    /**
     * 将16进制字符串转换为byte[]
     * @param str
     * @return
     */
    fun hexStringToBytes(str: String?): ByteArray {
        if (str == null || str.trim { it <= ' ' } == "") {
            return ByteArray(0)
        }
        val bytes = ByteArray(str.length / 2)
        for (i in 0 until str.length / 2) {
            val subStr = str.substring(i * 2, i * 2 + 2)
            bytes[i] = subStr.toInt(16).toByte()
        }
        return bytes
    }

    /**
     * 高位补零
     */
    fun addZeroForNum(str: String, strLength: Int): String {
        var str = str
        var strLen = str.length
        if (strLen < strLength) {
            while (strLen < strLength) {
                val sb = StringBuffer()
                sb.append("0").append(str);// 左补0
//                sb.append(str).append("0") //右补0
                str = sb.toString()
                strLen = str.length
            }
        }
        str = String.format(str).toUpperCase() //转为大写
        return str
    }

    /**
     * 将16进制转换为二进制
     * @param hexStr
     * @return
     */
    fun parseHexStr2Byte(hexStr: String): ByteArray? {
        if (hexStr.length < 1) return null
        val result = ByteArray(hexStr.length / 2)
        for (i in 0 until hexStr.length / 2) {
            val high = hexStr.substring(i * 2, i * 2 + 1).toInt(16)
            val low = hexStr.substring(i * 2 + 1, i * 2 + 2).toInt(
                16
            )
            result[i] = (high * 16 + low).toByte()
        }
        return result
    }

    /**
     * IEEE 754字符串转十六进制字符串
     * @param f
     * float转hex
     */
    fun floatStringToHex(f: Float,len:Int): String? {
        val i = java.lang.Float.floatToIntBits(f)
        return addZeroForNum(Integer.toHexString(i),len)
    }

    /**
     * IEEE 754字符串转十六进制字符串
     * @param f
     * float转hex
     */
    fun hexTofloat(s: String): Float {
        return java.lang.Float.intBitsToFloat(Integer.valueOf(s, 16))
    }
}
package com.example.lkacmf.util.qr

import android.graphics.Bitmap
import android.graphics.Color
import androidx.annotation.ColorInt
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import java.util.*

object QrCodeUtil {
    fun createQRCodeBitmap(
        content: String,
        width: Int,
        height: Int,
        character_set: String = "UTF-8",
        error_correction: String = "H",
        margin: String = "1",
        @ColorInt color_black: Int = Color.BLACK,
        @ColorInt color_white: Int = Color.WHITE,
    ): Bitmap? {
        /** 1.参数合法性判断  */
        if (width < 0 || height < 0) { // 宽和高都需要>=0
            return null
        }
        try {
            /** 2.设置二维码相关配置,生成BitMatrix(位矩阵)对象  */
            val hints: Hashtable<EncodeHintType, String> = Hashtable()
            if (character_set.isNotEmpty()) {
                hints[EncodeHintType.CHARACTER_SET] = character_set // 字符转码格式设置
            }
            if (error_correction.isNotEmpty()) {
                hints[EncodeHintType.ERROR_CORRECTION] = error_correction // 容错级别设置
            }
            if (margin.isNotEmpty()) {
                hints[EncodeHintType.MARGIN] = margin // 空白边距设置
            }
            val bitMatrix =
                QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints)

            /** 3.创建像素数组,并根据BitMatrix(位矩阵)对象为数组元素赋颜色值  */
            val pixels = IntArray(width * height)
            for (y in 0 until height) {
                for (x in 0 until width) {
                    if (bitMatrix[x, y]) {
                        pixels[y * width + x] = color_black // 黑色色块像素设置
                    } else {
                        pixels[y * width + x] = color_white // 白色色块像素设置
                    }
                }
            }
            /** 4.创建Bitmap对象,根据像素数组设置Bitmap每个像素点的颜色值,之后返回Bitmap对象  */
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
            return bitmap
        } catch (e: WriterException) {
            e.printStackTrace()
        }
        return null
    }
}
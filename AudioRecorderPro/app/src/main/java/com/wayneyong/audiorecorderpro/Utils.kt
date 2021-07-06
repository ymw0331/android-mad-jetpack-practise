package com.wayneyong.audiorecorderpro

import java.text.SimpleDateFormat
import java.util.*

class Utils {
    fun getDateUnixMillis(): Long {
        return System.currentTimeMillis()
    }

    fun getDateTimeReadable(): String {
        val df = Date(getDateUnixMillis())
        return SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.getDefault()).format(df)
    }
}
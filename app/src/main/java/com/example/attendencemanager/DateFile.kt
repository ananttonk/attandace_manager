package com.example.attendencemanager

import android.annotation.SuppressLint

import androidx.core.net.ParseException
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
fun String.parseDate(oldFormat: String="EEE MMM dd yyyy HH:mm:ss zz (zzzz)", newFormat: String): String? {
    val date: Date
    var str: String? = null
    try {
        date = SimpleDateFormat(oldFormat).parse(this) as Date
        str = SimpleDateFormat(newFormat).format(date)
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return str
}

@SuppressLint("SimpleDateFormat")
fun getCurrentDate(format:String): String {
    val simple = SimpleDateFormat(format)
    val result = Date(System.currentTimeMillis())
    return simple.format(result)
}
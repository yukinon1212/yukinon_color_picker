package com.yukinonlab.yukinoncolorpicker.utils

import android.util.Log

val TAG = "Ys Color"

fun log(vararg values: String) {
    Log.d(TAG, listOf(*values).toString())
}
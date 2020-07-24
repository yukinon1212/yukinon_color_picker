package com.yukinonlab.yukinoncolorpicker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.opencv.android.OpenCVLoader

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        OpenCVLoader.initDebug()
    }
}

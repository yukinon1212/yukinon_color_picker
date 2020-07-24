package com.yukinonlab.yukinoncolorpicker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.OpenCVLoader
import org.opencv.core.Core
import org.opencv.core.Mat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        OpenCVLoader.initDebug()  // ← OpenCVライブラリ読込
        initCamera()
    }

    fun initCamera() {
        // リスナ設定
        camera_view.setCvCameraViewListener(object : CameraBridgeViewBase.CvCameraViewListener2 {
            override fun onCameraViewStarted(width: Int, height: Int) { }

            override fun onCameraViewStopped() { }

            override fun onCameraFrame(inputFrame: CameraBridgeViewBase.CvCameraViewFrame?): Mat {
                // このメソッド内で画像処理. 今回はポジネガ反転.
                val mat = requireNotNull(inputFrame).rgba()
                Core.bitwise_not(mat, mat)
                return mat
            }
        })

        // プレビューを有効にする
        camera_view.setCameraPermissionGranted()
        camera_view.enableView()
    }
}

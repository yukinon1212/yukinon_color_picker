package com.yukinonlab.yukinoncolorpicker

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import com.yukinonlab.yukinoncolorpicker.utils.checkPermissions
import com.yukinonlab.yukinoncolorpicker.utils.log
import kotlinx.android.synthetic.main.activity_main.*
import org.opencv.android.OpenCVLoader
import java.util.concurrent.Executors



class MainActivity : AppCompatActivity() {

                    private lateinit var cameraProviderFuture : ListenableFuture<ProcessCameraProvider>

                    companion object {
                        private val REQUIRED_PERMISSIONS = arrayOf(
                            "android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE")
                        private const val REQUEST_CODE_FOR_PERMISSIONS =1234
                        private val cameraExecutor = Executors.newSingleThreadExecutor()
                    }

                    override fun onCreate(savedInstanceState: Bundle?) {
                        super.onCreate(savedInstanceState)
                        System.loadLibrary("opencv_java4")
                        setContentView(R.layout.activity_main)
                        initCamera()
                    }

                    override fun onRequestPermissionsResult(
                        requestCode: Int,
                        permissions: Array<out String>,
                        grantResults: IntArray
                    ) {
                        when (requestCode) {
                            REQUEST_CODE_FOR_PERMISSIONS -> {
                                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                                    // パーミッションが必要な処理
                                } else {
                    AlertDialog.Builder(this) // FragmentではActivityを取得して生成
                        .setTitle("権限の取得に失敗しました")
                        .setMessage("本アプリはカメラを使用します。\n" +
                                "再起動してアプリにカメラの使用を許可してください")
                        .setPositiveButton("終了") { _, _ ->
                            finish()
                        }
                        .show()
                }
            }
        }
    }

    private fun initCamera() {
        // パーミッションチェック
        if (checkPermissions(this, REQUIRED_PERMISSIONS)) {
            OpenCVLoader.initDebug()  // ← OpenCVライブラリ読込
            startCamera()
            log("" +
                    "test aaaaa")
        } else {
//          カメラパーミッションがなかった際の処理
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_FOR_PERMISSIONS)
        }
    }

    private fun startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(this))
    }

    private fun bindPreview(cameraProvider : ProcessCameraProvider) {
        val preview : Preview = Preview.Builder()
            .build()
        val imageAnalysis = ImageAnalysis.Builder().build()
        imageAnalysis.setAnalyzer(cameraExecutor, MyImageAnalyzer(this))

        val cameraSelector : CameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        preview.setSurfaceProvider(previewView.createSurfaceProvider())

        var camera = cameraProvider.bindToLifecycle(this as LifecycleOwner, cameraSelector, preview, imageAnalysis)
    }
}

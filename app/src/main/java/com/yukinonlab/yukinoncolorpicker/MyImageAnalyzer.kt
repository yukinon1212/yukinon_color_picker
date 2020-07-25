package com.yukinonlab.yukinoncolorpicker

import android.app.Activity
import android.graphics.Bitmap
import android.view.Surface
import android.widget.ImageView
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.yukinonlab.yukinoncolorpicker.utils.log
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc


class MyImageAnalyzer(private var activity: Activity): ImageAnalysis.Analyzer {

    companion object {
        var matPrevious: Mat? = null

    }
    override fun analyze(image: ImageProxy) {
/* Create cv::mat(RGB888) from image(NV21) */

        val previewView = activity.findViewById<androidx.camera.view.PreviewView>(R.id.previewView)
        val outputView = activity.findViewById<ImageView>(R.id.outputView)
        val matOrg = getMatFromImage(image)

        /* Fix image rotation (it looks image in PreviewView is automatically fixed by CameraX???) */
        val mat = fixMatRotation(matOrg)

        log("[analyze] width = " + image.width + ", height = " + image.height +
                "Rotation = " + previewView.getDisplay().getRotation())
        log("[analyze] mat width = " + matOrg.cols() + ", mat height = " + matOrg.rows())

        /* Do some image processing */
        val matOutput = Mat(mat.rows(), mat.cols(), mat.type())
        if (matPrevious == null) matPrevious = mat
        Core.absdiff(mat, matPrevious, matOutput)
        matPrevious = mat

        /* Draw something for test */
        Imgproc.rectangle(matOutput, Rect(10, 10, 100, 100), Scalar(255.0, 0.0, 0.0))
        Imgproc.putText(matOutput, "leftTop", Point(10.0, 10.0), 1, 1.0, Scalar(255.0, 0.0, 0.0))

        /* Convert cv::mat to bitmap for drawing */
        val bitmap =
            Bitmap.createBitmap(matOutput.cols(), matOutput.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(matOutput, bitmap)

        /* Display the result onto ImageView */
        activity.runOnUiThread{ outputView.setImageBitmap(bitmap) }

        /* Close the image otherwise, this function is not called next time */
        image.close()
    }

    private fun getMatFromImage(image: ImageProxy): Mat {
        /* https://stackoverflow.com/questions/30510928/convert-android-camera2-api-yuv-420-888-to-rgb */
        val yBuffer = image.planes[0].buffer
        val uBuffer = image.planes[1].buffer
        val vBuffer = image.planes[2].buffer
        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()
        val nv21 = ByteArray(ySize + uSize + vSize)
        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)
        val yuv = Mat(image.height + image.height / 2, image.width, CvType.CV_8UC1)
        yuv.put(0, 0, nv21)
        val mat = Mat()
        Imgproc.cvtColor(yuv, mat, Imgproc.COLOR_YUV2RGB_NV21, 3)
        return mat
    }

    private fun fixMatRotation(matOrg: Mat): Mat {
        val mat: Mat
        val previewView = activity.findViewById<androidx.camera.view.PreviewView>(R.id.previewView)
        when (previewView.getDisplay().getRotation()) {
            Surface.ROTATION_0 -> {
                mat = Mat(matOrg.cols(), matOrg.rows(), matOrg.type())
                Core.transpose(matOrg, mat)
                Core.flip(mat, mat, 1)
            }
            Surface.ROTATION_90 -> mat = matOrg
            Surface.ROTATION_270 -> {
                mat = matOrg
                Core.flip(mat, mat, -1)
            }
            else -> {
                mat = Mat(matOrg.cols(), matOrg.rows(), matOrg.type())
                Core.transpose(matOrg, mat)
                Core.flip(mat, mat, 1)
            }
        }
        return mat
    }
}

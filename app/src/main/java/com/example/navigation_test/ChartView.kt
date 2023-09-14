package com.example.navigation_test

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class ChartView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val PROPORTION = 1.0f

    private var mBitmap: Bitmap
    private val mPaint = Paint()
    private val textPaint = Paint()
    private val mCanvas = Canvas()

    private val res = context.resources

    private var X_Axis: Int = 0
    private var Y_Axis: Int = 0
    private var mLastX: Float = 0f
    private var mNextX: Float = 0f
    private var mLastY: Float = 0f
    private var mNextY: Float = 0f
    private var mSpeed: Float = 0f
    private var mMaxX: Int = 0

    private var mIHR: String? = null

    private val Mask = ShapeDrawable(RectShape())

    init {
        X_Axis = res.getDimensionPixelSize(R.dimen.Max_X_Axis)
        Y_Axis = res.getDimensionPixelSize(R.dimen.Max_Y_Axis)

        mLastX = 0f
        mNextX = 0f
        mLastY = 0f
        mSpeed = 8f
        mMaxX = X_Axis

        Mask.paint.color = Color.Black.toArgb()
        mPaint.flags = Paint.ANTI_ALIAS_FLAG
        mPaint.color = Color.Green.toArgb()
        mPaint.style = Paint.Style.FILL_AND_STROKE
        mPaint.strokeWidth = 2f
        mPaint.textSize = 50f
        mPaint.strokeJoin = Paint.Join.ROUND

        textPaint.flags = Paint.ANTI_ALIAS_FLAG
        textPaint.color = Color.Green.toArgb()
        textPaint.style = Paint.Style.STROKE
        textPaint.strokeWidth = 2f
        textPaint.textSize = 5f

        mBitmap = Bitmap.createBitmap(X_Axis, Y_Axis, Bitmap.Config.RGB_565)
        mCanvas.setBitmap(mBitmap)
        mCanvas.drawColor(Color.Black.toArgb())
    }

    fun clearChart() {
        val canvas = mCanvas
        Mask.setBounds(0, 0, X_Axis, Y_Axis)
        Mask.draw(canvas)
        invalidate()
        mLastX = 0f
        mLastY = 0f
    }

    fun setIHR(IHR: String) {
        mIHR = IHR
    }

    fun setX_Axis(xValue: Int) {
        X_Axis = xValue
        mBitmap.recycle()
        mBitmap = Bitmap.createBitmap(X_Axis, Y_Axis, Bitmap.Config.RGB_565)
        mCanvas.setBitmap(mBitmap)
        mCanvas.drawColor(Color.Black.toArgb())
        mMaxX = X_Axis
    }


    override fun onDraw(canvas: Canvas) {
        synchronized(this) {
            if (mBitmap != null) {
                canvas.drawBitmap(mBitmap, 0f, 0f, null)
            }
        }
    }

    fun waveDraw(rawData: ByteArray) {
        synchronized(this) {
            if (mBitmap != null) {
                val canvas = mCanvas
                val paint = mPaint
                var Mask_End: Int

                var Mask_Start: Int = mLastX.toInt()
                Mask_End = (mLastX + rawData.size * mSpeed).toInt()

                if (Mask_End < mMaxX) {
                    Mask.setBounds(Mask_Start, 0, Mask_End, Y_Axis)
                    Mask.draw(canvas)
                } else {
                    Mask.setBounds(Mask_Start, 0, mMaxX, Y_Axis)
                    Mask.draw(canvas)
                    Mask_End = Mask_End - mMaxX
                    Mask.setBounds(0, 0, Mask_End, Y_Axis)
                    Mask.draw(canvas)
                }

                canvas.drawLine(Mask_End + 1f, 0f, Mask_End + 1f, Y_Axis.toFloat(), paint)

                for (rawDatum in rawData) {
                    mNextX = mLastX + mSpeed
                    if (mNextX >= mMaxX) {
                        mNextX -= mMaxX
                    }
                    mNextY = Y_Axis - ((rawDatum.toInt() and 0xFF) * PROPORTION)
                    canvas.drawLine(mLastX, mLastY, mNextX, mNextY, paint)
                    mLastX = mNextX
                    mLastY = mNextY
                }

                invalidate()
            }
        }
    }
}

@Composable
fun ChartViewCompose(data: FloatArray) {
    val context = LocalView.current.context
    val chartView = ChartView(context, null)
    val coroutineScope = rememberCoroutineScope()

//    AndroidView(
//        factory = { chartView },
//        modifier = Modifier
//            .fillMaxSize()
//            .aspectRatio(2f)
//            .background(Color.Black)
//            .border(2.dp, Color.Red)
//    ) { view ->
//        // 使用協程執行畫圖的程式碼
//        coroutineScope.launch {
//            // 每64個浮點數就觸發繪圖一次，剩下不足64個的浮點數就略過
//            for (i in 0 until data.size / 32) {
//                val rawData = floatArrayToByteArray(data.sliceArray(i * 32 until (i + 1) * 32 - 1))
//                view.waveDraw(rawData)
//                Log.d("ChartViewCompose", "waveDraw: $rawData")
//
//                // 添加延遲，例如延遲1000毫秒（可根據需求調整）
//                delay(1000)
//            }
//        }
//    }
    Canvas(modifier = Modifier
        .fillMaxSize()
        .aspectRatio( 2f)
        .background(Color.Black), onDraw = {
    })
}

fun floatArrayToByteArray(floatArray: FloatArray): ByteArray {
    val byteArray = ByteArray(floatArray.size * 4) // 一個浮點數佔4個字節

    for (i in floatArray.indices) {
        val floatBits = java.lang.Float.floatToIntBits(floatArray[i])
        byteArray[i * 4] = (floatBits and 0xFF).toByte()
        byteArray[i * 4 + 1] = (floatBits shr 8 and 0xFF).toByte()
        byteArray[i * 4 + 2] = (floatBits shr 16 and 0xFF).toByte()
        byteArray[i * 4 + 3] = (floatBits shr 24 and 0xFF).toByte()
    }

    return byteArray
}
package com.example.navigation_test

import android.graphics.Bitmap
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel

class ChartViewModel : ViewModel() {
    private var _mbListData = mutableMapOf<String, Pair<Float, Float>>()

    private var _mBitmap = mutableMapOf<String, Bitmap>()

    init {
        _mbListData = mutableMapOf()
        _mBitmap = mutableMapOf()
    }

    fun updateData(key: String, value: Pair<Float, Float>) {
        val currentMap = _mbListData.toMutableMap()
        currentMap[key] = value
        _mbListData = currentMap
    }

    fun initKey(listOfKey: List<String>) {
        _mbListData.clear()
        for (key in listOfKey) {
            _mbListData[key] = Pair(0f, 0f)
            _mBitmap[key] = Bitmap.createBitmap(873, 442, Bitmap.Config.ARGB_8888)
        }
    }

    fun getListData(key: String): Pair<Float, Float> {
        return _mbListData[key] ?: Pair(0f, 0f)
    }

    fun getBitmap(key: String): Bitmap {
        return _mBitmap[key] ?: Bitmap.createBitmap(873, 442, Bitmap.Config.ARGB_8888)
    }


    fun drawDiagram(data: ByteArray, userId: String) {
        val proportion = 1f
        val speed = 10f
        val mask = ShapeDrawable(RectShape())
        val mCanvas = android.graphics.Canvas(getBitmap(userId))
        val paint = android.graphics.Paint()
        paint.strokeWidth = 2f
        paint.color = Color.Green.toArgb()
        var nextX = 0f
        var nextY = 0f
        val canvasWidth = 873f
        val canvasHeight = 442f
        val maskStart = getListData(userId).first
        var maskEnd = (getListData(userId).first + (data.size * speed))

        if (maskEnd < canvasWidth) {
            mask.setBounds(maskStart.toInt(), 0, maskEnd.toInt(), canvasHeight.toInt())
            mask.draw(mCanvas)
        } else {
            mask.setBounds(maskStart.toInt(), 0, canvasWidth.toInt(), canvasHeight.toInt())
            mask.draw(mCanvas)
            maskEnd -= canvasWidth
            mask.setBounds(0, 0, maskEnd.toInt(), canvasHeight.toInt())
            mask.draw(mCanvas)
        }
        mCanvas.drawLine(
            maskEnd + 1f,
            0f,
            maskEnd + 1f,
            canvasHeight,
            paint
        )
        for (rawDatum in data) {
            nextX = getListData(userId).first + speed
            if (nextX >= canvasWidth) {
                nextX = 0f
            } else {
                nextY = canvasHeight - (((rawDatum.toInt() + 30) and 0xFF) * proportion)
                mCanvas.drawLine(
                    getListData(userId).first,
                    getListData(userId).second,
                    nextX,
                    nextY,
                    paint
                )
            }
            updateData(userId, Pair(nextX, nextY))
        }
    }
}
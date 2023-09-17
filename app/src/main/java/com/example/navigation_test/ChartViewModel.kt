package com.example.navigation_test

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.lifecycle.ViewModel

class ChartViewModel : ViewModel() {
    private var mlastX: Float = 0f

    private var mlastY: Float = 0f

    fun updateLastXY(x: Float, y: Float) {
        mlastX = x
        mlastY = y
    }

    fun getmLastX(): Float {
        return mlastX
    }

    fun getmLastY(): Float {
        return mlastY
    }
}
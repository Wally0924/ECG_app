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
            _mBitmap[key] = Bitmap.createBitmap(872, 440, Bitmap.Config.ARGB_8888)
        }
    }
    //大圖用
    fun initUsrIdKey(userId : String){
        _mbListData[userId] = Pair(0f, 0f)
        _mBitmap[userId] = Bitmap.createBitmap(872, 440, Bitmap.Config.ARGB_8888)
    }
    //小圖用
    fun initUsrIdData(userId : String){
        _mbListData[userId] = Pair(0f, 0f)
        _mBitmap[userId] = Bitmap.createBitmap(872, 440, Bitmap.Config.ARGB_8888)
    }

    fun getListData(key: String): Pair<Float, Float> {
        return _mbListData[key] ?: Pair(0f, 0f)
    }

    fun getBitmap(key: String): Bitmap {
        return _mBitmap[key] ?: Bitmap.createBitmap(873, 440, Bitmap.Config.ARGB_8888)
    }
}
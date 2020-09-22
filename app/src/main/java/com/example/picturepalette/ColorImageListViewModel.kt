package com.example.picturepalette

import android.graphics.Color
import androidx.lifecycle.ViewModel

class ColorImageListViewModel(): ViewModel() {
    private var colorList = arrayOfNulls<Color>(5)

    fun getColor(index: Int): Color? {
        return colorList[index]
    }

    fun setColor(index: Int, color: Color) {
        colorList[index] = Color()
    }
}
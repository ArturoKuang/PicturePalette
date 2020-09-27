package com.example.picturepalette

import android.graphics.Color
import androidx.lifecycle.ViewModel

class ColorPaletteListViewModel() : ViewModel() {
    public var colorList = MutableList<Color>(5) {(Color())}
}
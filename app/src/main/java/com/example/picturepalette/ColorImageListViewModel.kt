package com.example.picturepalette

import android.graphics.Color
import androidx.lifecycle.ViewModel

class ColorImageListViewModel(): ViewModel() {
    public var colorList = List<Color>(5) {(Color())}
}
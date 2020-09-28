package com.example.picturepalette

import android.graphics.Color
import java.util.*

class Sample constructor(
    val colors: IntArray
) {
    var hsvColors = mutableListOf<FloatArray>()
    var sampleColors = mutableListOf<Color>()
    private val random = Random()

    private fun convertToHSV() {
        for (color in colors) {
            var hsv = FloatArray(3)
            Color.HSVToColor(color, hsv)
            hsvColors.add(hsv)
        }
    }

    private fun lerp(a: FloatArray, b: FloatArray, f: Float): FloatArray {
        val result = FloatArray(3)
        for((index, value) in a.withIndex()) {
            result[index] = lerp(a[index], b[index], f)
        }
        return result
    }

    private fun lerp(a: Float, b: Float, f: Float): Float {
        return (a * (1 - f)) + (b * f)
    }

    public fun generateColor(): Color {
        var colorResult = hsvColors[0]
        for(i in 1 until hsvColors.size) {
            colorResult = lerp(colorResult, hsvColors[i], random.nextFloat())
        }

        return Color.valueOf(Color.HSVToColor(colorResult))
    }


    public fun generateColors(): List<Color> {
        var colorsResult = mutableListOf<Color>()
        for (i in 0..5) {
            colorsResult.add(generateColor())
        }
        return colorsResult
    }
}
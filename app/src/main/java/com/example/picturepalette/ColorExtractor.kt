package com.example.picturepalette

import android.graphics.Color
import java.util.*


class ColorExtractor constructor(
    colors: List<Color>,
    numColorsGenerated: Int,
    maxIteration: Int
) {

    private var hsvColors = mutableListOf<FloatArray>()
    private var cluster = mutableMapOf<FloatArray, FloatArray>()
    private val random = Random()

    private class Centroid() {
        var coordinates =  mutableListOf<FloatArray>()
    }

    private fun distance(colorA: FloatArray, colorB: FloatArray): Double {
        var sum: Double = 0.0
        for((index, value) in colorA.withIndex()) {
            sum += diffSq(colorA[index], colorB[index])
        }
        return sum
    }

    private fun diffSq(a: Float, b: Float): Float {
        return (b - a) * (b - a)
    }

    private fun getRandomCentroids(): List<Centroid> {
        val max = hsvColors.maxByOrNull {
            it.sum()
        }

        val min = hsvColors.minByOrNull {
            it.sum()
        }

        for(i in 0..maxIteration) {

        }
    }

    private fun nearestCenter(): Centroid {

    }

    private fun assignToCluster() {

    }

    private fun average(): Centroid {

    }

    private fun relocateCentroid(): List<Centroid> {

    }

    fun extract(): List<Color> {

    }
}
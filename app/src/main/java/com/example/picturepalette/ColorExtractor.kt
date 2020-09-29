package com.example.picturepalette

import android.graphics.Color
import java.util.*

class ColorExtractor(
    private val colors: List<FloatArray>,
    private val maxIteration: Int,
    private val numOfColors: Int
) {
    private var clusters = mutableMapOf<Centroid, MutableList<FloatArray>>()
    private val random = Random()
    private var centroids = listOf<Centroid>()

    init {
        centroids = randomCentroids()
    }

    private class Centroid() {
        var coordinates = FloatArray(3)
    }

    private fun randomCentroids(): List<Centroid> {
        val max = colors.maxByOrNull {
            it.sum()
        }

        val min = colors.minByOrNull {
            it.sum()
        }

        var centroids = mutableListOf<Centroid>()
        for (i in 1..numOfColors) {
            centroids.add(randomCentroid(min, max))
        }
        return centroids
    }

    private fun randomCentroid(min: FloatArray?, max: FloatArray?): Centroid {
        var centroid = Centroid()
        if (min == null || max == null) {
            return centroid
        }

        for ((index, value) in min.withIndex()) {
            val randomValue = random.nextFloat() * ((max[index] - min[index]) + min[index])
            centroid.coordinates[index] = randomValue
        }
        return centroid
    }

    private fun assignToCluster() {
        for(color in colors) {
            val centroid = nearestCentroid(color)
            if (centroid != null) {
                val clusterColors = clusters.getOrPut(centroid) {
                    mutableListOf<FloatArray>()
                }
                clusterColors.add(color)
                clusters[centroid] = clusterColors
            }
        }
    }

    private fun nearestCentroid(color: FloatArray): Centroid? {
        var nearest: Centroid? = null
        var minDistance: Double = Double.MAX_VALUE

        for(centroid in centroids) {
            val currentDistance = distance(color, centroid.coordinates)
            if (currentDistance < minDistance) {
                nearest = centroid
                minDistance = currentDistance
            }
        }

        return nearest
    }

    private fun distance(colorA: FloatArray, colorB: FloatArray): Double {
        var sum: Double = 0.0
        for ((index, value) in colorA.withIndex()) {
            sum += diffSq(colorA[index], colorB[index])
        }
        return sum
    }

    private fun diffSq(a: Float, b: Float): Float {
        return (b - a) * (b - a)
    }

    private fun average(): Centroid {

    }

    private fun relocateCentroid(): List<Centroid> {

    }

    fun extract(): List<Color> {
        val centroids = randomCentroids()
        assignToCluster()
        return listOf(Color())
    }
}
package com.example.picturepalette

import android.graphics.Color
import java.util.*

class ColorExtractor(
    private val colors: List<FloatArray>,
    private val maxIterations: Int,
    private val numOfColors: Int
) {

    private val random = Random()
    private var centroids = mutableListOf<Centroid>()

    private class Centroid() {
        var coordinates = FloatArray(3)
    }

    fun extract(): List<Color> {
        var clusters = mutableMapOf<Centroid, MutableList<FloatArray>>()
        var lastCluster = mutableMapOf<Centroid, MutableList<FloatArray>>()
        centroids = randomCentroids()

        for(i in 0 until maxIterations) {
            val isLastIteration = i == maxIterations - 1
            assignToCluster(clusters)
            val shouldTerminate = isLastIteration || clusters == lastCluster
            lastCluster = clusters
            if(shouldTerminate) {
                break
            }

            relocateCentroid(clusters)
            clusters = mutableMapOf()
        }

        var extractedColors = mutableListOf<Color>()
        for(cluster in clusters) {
            val keyColor =  Color.valueOf(Color.HSVToColor(cluster.key.coordinates))
            extractedColors.add(keyColor)
        }
        return extractedColors
    }

    private fun randomCentroids(): MutableList<Centroid> {
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

    private fun assignToCluster(clusters: MutableMap<Centroid, MutableList<FloatArray>>) {
        for (color in colors) {
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

        for (centroid in centroids) {
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

    private fun relocateCentroid(clusters: MutableMap<Centroid, MutableList<FloatArray>>) {
        for ((index, value) in centroids.withIndex()) {
            val newCentroid: Centroid? = average(value, clusters)
            if (newCentroid != null) {
                centroids[index] = newCentroid
            }
        }
    }

    private fun average(
        centroid: Centroid,
        clusters: MutableMap<Centroid, MutableList<FloatArray>>
    ): Centroid? {
        val colors: MutableList<FloatArray> = clusters[centroid] ?: return null
        var average = FloatArray(3)
        for (color in colors) {
            for (i in color.indices) {
                average[i] += color[i]
            }
        }

        for (i in average.indices) {
            average[i] = average[i] / colors.size
        }

        val newCentroid = Centroid()
        newCentroid.coordinates = average
        return newCentroid
    }
}
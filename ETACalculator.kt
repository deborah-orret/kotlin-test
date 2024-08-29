package com.byteboard.d1_kotlin

import kotlin.math.*

class ETACalculator(private val metroGraph: MetroGraph) {

    companion object {
        private const val EARTH_RADIUS_KM = 6371.0

        fun haversine(location1: Pair<Double, Double>, location2: Pair<Double, Double>): Double {
            val (lat1, lon1) = location1.map(::radians)
            val (lat2, lon2) = location2.map(::radians)
            val dlon = lon2 - lon1
            val dlat = lat2 - lat1
            val a = sin(dlat / 2).pow(2) + cos(lat1) * cos(lat2) * sin(dlon / 2).pow(2)
            val c = 2 * atan2(sqrt(a), sqrt(1 - a))
            return EARTH_RADIUS_KM * c
        }

        private fun Pair<Double, Double>.map(transform: (Double) -> Double): Pair<Double, Double> {
            return Pair(transform(this.first), transform(this.second))
        }

        private fun radians(degrees: Double): Double {
            return degrees * Math.PI / 180.0
        }
    }

    private fun nearestStation(coordinates: Pair<Double, Double>): Pair<String?, Double> {
        var stationName: String? = null
        var minDistance = Double.POSITIVE_INFINITY

        for (station in metroGraph.stations.values) {
            val distance = haversine(coordinates, station.coordinates)
            if (distance < minDistance) {
                minDistance = distance
                stationName = station.name
            }
        }

        return Pair(stationName, minDistance)
    }

    fun calculateEta(location1: Pair<Double, Double>, location2: Pair<Double, Double>): Double {
        val (nearestStation1, additionalDistance1) = nearestStation(location1)
        val (nearestStation2, additionalDistance2) = nearestStation(location2)

        val walkingTime = haversine(location1, location2) / 5.0 * 60.0 // Assuming walking speed of 5 km/h
        if (nearestStation1 == null || nearestStation2 == null) {
            return walkingTime
        }

        val metroTime = metroGraph.shortestPath(nearestStation1, nearestStation2).first + (additionalDistance1 + additionalDistance2) / 5.0 * 60.0

        return min(walkingTime, metroTime)
    }
}

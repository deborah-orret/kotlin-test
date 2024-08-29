package com.byteboard.d1_kotlin

import com.byteboard.d1_kotlin.entities.StationEntity

class MetroGraph {
    private val graph: MutableMap<String, MutableMap<String, Double>> = mutableMapOf()
    internal val stations: MutableMap<String, StationEntity> = mutableMapOf()

    fun addStation(station: StationEntity) {
        if (station.name !in stations) {
            stations[station.name] = station
            graph[station.name] = mutableMapOf()
        }
    }

    fun addConnection(startStation: String, endStation: String, time: Double) {
        if (startStation in graph && endStation in graph) {
            graph[startStation]!![endStation] = time
            graph[endStation]!![startStation] = time
        } else {
            throw Exception("Invalid connection: One or more stations not in graph.")
        }
    }

    fun shortestPath(startStation: String, endStation: String): Pair<Double, List<String>?> {
        val distances = graph.keys.associateWith { Double.POSITIVE_INFINITY }.toMutableMap()
        distances[startStation] = 0.0

        val previous: MutableMap<String, String?> = graph.keys.associateWith { null }.toMutableMap()

        val priorityQueue = sortedSetOf(compareBy<Pair<Double, String>> { it.first }.thenBy { it.second })

        priorityQueue.add(Pair(0.0, startStation))

        while (priorityQueue.isNotEmpty()) {
            val (currentDistance, currentStation) = priorityQueue.first()
            priorityQueue.remove(priorityQueue.first())

            if (currentStation == endStation) {
                val path = mutableListOf<String>()
                var step: String? = endStation
                while (step != null) {
                    path.add(step)
                    step = previous[step]
                }
                return Pair(currentDistance, path.reversed())
            }

            if (currentDistance > distances[currentStation]!!) continue

            for ((neighbor, time) in graph[currentStation]!!) {
                val distance = currentDistance + time
                if (distance < distances[neighbor]!!) {
                    distances[neighbor] = distance
                    previous[neighbor] = currentStation
                    priorityQueue.add(Pair(distance, neighbor))
                }
            }
        }
        return Pair(Double.POSITIVE_INFINITY, null)
    }
}
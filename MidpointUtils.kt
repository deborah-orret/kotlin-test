package com.byteboard.d1_kotlin

import com.byteboard.d1_kotlin.entities.ActivityEntity
import com.byteboard.d1_kotlin.entities.StationEntity
import com.byteboard.d1_kotlin.entities.UserRequestEntity
import java.io.File

class MidpointUtils {
    fun loadMetroGraph(): MetroGraph {
        val graph = MetroGraph()

        // Load stations into metro graph
        File("data/metro_stations.csv").bufferedReader().use { reader ->
            reader.lineSequence()
                .drop(1) // Skip header line
                .map { it.split(",") }
                .forEach { row ->
                    val name = row[0]
                    val coordinates = Pair(row[1].toDouble(), row[2].toDouble())
                    val station = StationEntity(name, coordinates)
                    graph.addStation(station)
                }
        }

        // Load station connections into graph
        File("data/metro_timetable.csv").bufferedReader().use { reader ->
            reader.lineSequence()
                .drop(1) // Skip header line
                .map { it.split(",") }
                .forEach { row ->
                    val startStation = row[0]
                    val endStation = row[1]
                    val time = row[2].toDouble()
                    if (startStation in graph.stations && endStation in graph.stations) {
                        graph.addConnection(startStation, endStation, time)
                    } else {
                        throw Exception("Stations $startStation and/or $endStation not found in the graph.")
                    }
                }
        }

        return graph
    }

    fun loadUserRequests(): List<UserRequestEntity> {
        val userRequests = mutableListOf<UserRequestEntity>()
        File("data/user_requests.csv").bufferedReader().use { reader ->
            reader.lineSequence()
                .drop(1) // Skip header line
                .map { it.split(",") }
                .forEach { row ->
                    userRequests.add(
                        UserRequestEntity(
                            userId = row[0],
                            user1Location = Pair(row[1].toDouble(), row[2].toDouble()),
                            user2Location = Pair(row[3].toDouble(), row[4].toDouble()),
                            activityType = row[5],
                            rating = row[6].toDouble(),
                            priceCategory = row[7].toDouble(),
                            timeNeeded = row[8].toDouble()
                        )
                    )
                }
        }
        return userRequests
    }

    fun loadActivities(): List<ActivityEntity> {
        val activities = mutableListOf<ActivityEntity>()
        File("data/activities.csv").bufferedReader().use { reader ->
            reader.lineSequence()
                .drop(1) // Skip header line
                .map { it.split(",") }
                .forEach { row ->
                    activities.add(
                        ActivityEntity(
                            name = row[0],
                            coordinates = Pair(row[1].toDouble(), row[2].toDouble()),
                            activityType = row[3],
                            rating = row[4].toDouble(),
                            priceCategory = row[5].toDouble(),
                            timeNeeded = row[6].toDouble()
                        )
                    )
                }
        }
        return activities
    }
}
package com.byteboard.d1_kotlin

import com.byteboard.d1_kotlin.entities.ActivityEntity
import com.byteboard.d1_kotlin.entities.SuggestionEntity
import com.byteboard.d1_kotlin.entities.UserRequestEntity

fun main() {
    // Load data
    val (activities, userRequests, metroGraph) = loadMidpointUtils()

    // Filter activities based on user preferences
    val filteredActivities = filterActivities(activities, userRequests)

    // Run tasks and print results
    println(runTask1(filteredActivities, userRequests, metroGraph))
    println(runTask2(filteredActivities, userRequests, metroGraph))
    println(runTask3(filteredActivities, userRequests, metroGraph))
}

fun loadMidpointUtils(): Triple<List<ActivityEntity>, List<UserRequestEntity>, MetroGraph> {
    // Initialize the utility class to load data from CSV files
    val midpointUtils = MidpointUtils()

    // Load data for metro graph, user requests, and activities
    val metroGraph = midpointUtils.loadMetroGraph()
    val userRequests = midpointUtils.loadUserRequests()
    val activities = midpointUtils.loadActivities()

    return Triple(activities, userRequests, metroGraph)
}

fun runTask1(activities: List<ActivityEntity>, userRequests: List<UserRequestEntity>, metroGraph: MetroGraph): String {
    // Task 1: Filter activities based on user preferences
    val etaCalculator = ETACalculator(metroGraph)
    val firstUserRequest = userRequests.firstOrNull()

    // If a user request is available, find and return the best activity suggestion
    return firstUserRequest?.let {
        val suggestion = suggestMidpointActivityTask1(activities, etaCalculator, it)
        "Task 1 Result:\n" + (suggestion?.let { s -> formatSuggestion(s) } ?: "No suitable activity found.")
    } ?: "Task 1: No user requests available."
}

fun runTask2(activities: List<ActivityEntity>, userRequests: List<UserRequestEntity>, metroGraph: MetroGraph): String {
    // Task 2: Implement logic to suggest a midpoint activity considering travel time fairness
    val etaCalculator = ETACalculator(metroGraph)
    val firstUserRequest = userRequests.firstOrNull()

    // If a user request is available, find and return the best activity suggestion
    return firstUserRequest?.let {
        val suggestion = suggestMidpointActivityTask2(
            it.user1Location,
            it.user2Location,
            activities,
            etaCalculator
        )
        "Task 2 Result:\n" + (suggestion?.let { s -> formatSuggestion(s) } ?: "No suitable activity found.")
    } ?: "Task 2: No user requests available."
}

fun runTask3(activities: List<ActivityEntity>, userRequests: List<UserRequestEntity>, metroGraph: MetroGraph): String {
    // Task 3: Implement logic to suggest activities for groups
    val etaCalculator = ETACalculator(metroGraph)
    val firstUserGroupRequest = userRequests.firstOrNull()

    // If a group request is available, find and return the best activity suggestion
    return firstUserGroupRequest?.let {
        val userLocations = listOf(it.user1Location, it.user2Location) // Add more locations as needed
        val suggestion = suggestMidpointActivityTask3(userLocations, activities, etaCalculator)
        "Task 3 Result:\n" + (suggestion?.let { s -> formatSuggestion(s) } ?: "No suitable activity found.")
    } ?: "Task 3: No user requests available."
}

fun suggestMidpointActivityTask1(
    activities: List<ActivityEntity>,
    etaCalculator: ETACalculator,
    preferences: UserRequestEntity
): SuggestionEntity? {
    // Logic to find the best activity among the filtered list
    var bestSuggestion: SuggestionEntity? = null
    var minMaxEta = Double.MAX_VALUE

    for (activity in activities) {
        val user1Eta = etaCalculator.calculateEta(preferences.user1Location, activity.coordinates)
        val user2Eta = etaCalculator.calculateEta(preferences.user2Location, activity.coordinates)

        val maxEta = maxOf(user1Eta, user2Eta)

        // Update the best suggestion if the current activity has a lower max ETA
        if (maxEta < minMaxEta) {
            minMaxEta = maxEta
            bestSuggestion = SuggestionEntity(activity, user1Eta, user2Eta)
        }
    }

    return bestSuggestion
}

fun suggestMidpointActivityTask2(
    user1Location: Pair<Double, Double>,
    user2Location: Pair<Double, Double>,
    activities: List<ActivityEntity>,
    etaCalculator: ETACalculator,
): SuggestionEntity? {
    // Logic to find the activity that minimizes the maximum ETA for both users
    var bestSuggestion: SuggestionEntity? = null
    var minMaxEta = Double.MAX_VALUE

    for (activity in activities) {
        val user1Eta = etaCalculator.calculateEta(user1Location, activity.coordinates)
        val user2Eta = etaCalculator.calculateEta(user2Location, activity.coordinates)

        val maxEta = maxOf(user1Eta, user2Eta)

        // Update the best suggestion if the current activity has a lower max ETA
        if (maxEta < minMaxEta) {
            minMaxEta = maxEta
            bestSuggestion = SuggestionEntity(activity, user1Eta, user2Eta)
        }
    }

    return bestSuggestion
}

fun suggestMidpointActivityTask3(
    userLocations: List<Pair<Double, Double>>,
    activities: List<ActivityEntity>,
    etaCalculator: ETACalculator,
): SuggestionEntity? {
    // Calculate a "group midpoint" as the average of all user locations
    val avgLocation = Pair(
        userLocations.map { it.first }.average(),
        userLocations.map { it.second }.average()
    )

    var bestSuggestion: SuggestionEntity? = null
    var minMaxEta = Double.MAX_VALUE

    for (activity in activities) {
        // Calculate the ETA from the group midpoint to the activity
        val avgEta = etaCalculator.calculateEta(avgLocation, activity.coordinates)

        // Calculate the ETA from each user location to the activity
        val etas = userLocations.map { etaCalculator.calculateEta(it, activity.coordinates) }
        val maxEta = etas.maxOrNull() ?: Double.MAX_VALUE

        // Combine the average ETA and the maximum user ETA to find the best suggestion
        val combinedEta = maxOf(avgEta, maxEta)

        // Update the best suggestion if the current activity has a lower combined ETA
        if (combinedEta < minMaxEta) {
            minMaxEta = combinedEta
            bestSuggestion = SuggestionEntity(activity, avgEta, maxEta)
        }
    }

    return bestSuggestion
}

fun filterActivities(activities: List<ActivityEntity>, userRequests: List<UserRequestEntity>): List<ActivityEntity> {
    val preferences = userRequests.firstOrNull() ?: return activities

    // Filter activities based on user preferences
    val filteredActivities = activities.filter { activity ->
        activity.activityType == preferences.activityType &&
                activity.rating >= preferences.rating &&
                activity.priceCategory <= preferences.priceCategory &&
                activity.timeNeeded <= preferences.timeNeeded
    }

    // If no activities meet the criteria, filter only by activity type
    return filteredActivities.ifEmpty {
        activities.filter { it.activityType == preferences.activityType }
    }
}

fun formatSuggestion(suggestion: SuggestionEntity): String {
    return "Suggested activity: ${suggestion.activity.name}\n" +
            "Distance from User 1: ${suggestion.user1Eta} minutes\n" +
            "Distance from User 2: ${suggestion.user2Eta} minutes"
}

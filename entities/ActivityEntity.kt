package com.byteboard.d1_kotlin.entities

data class ActivityEntity(
    val name: String,
    val coordinates: Pair<Double, Double>,
    val activityType: String,
    val rating: Double,
    val priceCategory: Double,
    val timeNeeded: Double
) {
    override fun toString(): String {
        return "Activity($name)"
    }
}

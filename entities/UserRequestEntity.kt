package com.byteboard.d1_kotlin.entities

data class UserRequestEntity(
    val userId: String,
    val user1Location: Pair<Double, Double>,
    val user2Location: Pair<Double, Double>,
    val activityType: String,
    val rating: Double,
    val priceCategory: Double,
    val timeNeeded: Double
) {
    override fun toString(): String {
        return "UserRequest($userId)"
    }
}

package com.byteboard.d1_kotlin.entities

data class StationEntity(
    val name: String,
    val coordinates: Pair<Double, Double>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is StationEntity) return false
        return name == other.name
    }

    override fun toString(): String {
        return "Station($name)"
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + coordinates.hashCode()
        return result
    }
}
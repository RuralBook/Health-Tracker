package com.tobias.healthtracker.data

data class UserGoals(
    var waterGoal: Double,
    var waterUser: Double ,

    var kalorienGoal: Double,
    var kalorienUser: Double,

    var workoutGoal: Double,
    var workoutUser: Double
)

data class UserKalorienGoals(
    var kalorienGoal: Double,
    var kalorienUser: Double
)

data class UserKalorienEaten(
    var name: String,
    var time: String,
    var kalorien: Double
)

data class UserWaterGoals(
    var waterGoal: Double,
    var waterUser: Double
)

data class UserWaterDrunken(
    var name: String,
    var time: String,
    var liters: Double
)

data class UserWorkoutGoals(
    var workoutGoal: Double,
    var workoutUser: Double
)

data class UserWorkouts(
    var id: Int,
    var time: String,
)
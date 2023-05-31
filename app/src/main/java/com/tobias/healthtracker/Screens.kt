package com.tobias.healthtracker

sealed class Screens(val route: String) {
    object DashboardScreen: Screens("main_screen")
    object WaterScreen: Screens("water_screen")

}
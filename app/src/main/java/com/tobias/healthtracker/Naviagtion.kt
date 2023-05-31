package com.tobias.healthtracker

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun Navigation(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screens.DashboardScreen.route){
        composable(route = Screens.DashboardScreen.route){

        }
    }
}

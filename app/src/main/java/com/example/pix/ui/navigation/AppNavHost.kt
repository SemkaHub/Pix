package com.example.pix.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.pix.ui.screen.grid.PictureGridScreen
import com.example.pix.ui.screen.picture.PictureDetailScreen

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Destination.PictureGrid.route) {
        composable(Destination.PictureGrid.route) {
            PictureGridScreen(
                onPictureClick = { pictureId ->
                    navController.navigate(Destination.PictureDetails.route + "/$pictureId")
                }
            )
        }

        composable(
            Destination.PictureDetails.route + "/{pictureId}",
            arguments = listOf(navArgument("pictureId") {
                type = NavType.StringType
            })
        ) {
            PictureDetailScreen(navController = navController)
        }
    }
}
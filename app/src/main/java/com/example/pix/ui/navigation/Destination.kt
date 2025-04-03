package com.example.pix.ui.navigation

sealed class Destination(val route: String) {

    data object PictureGrid : Destination(GRID)
    data object PictureDetails : Destination(DETAILS)

    companion object {
        private const val GRID = "route_picture_grid"
        private const val DETAILS = "route_picture_details"
    }
}
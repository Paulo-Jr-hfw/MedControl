package com.app.medcontrol.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

sealed class BottomNavItem(val title: String, val icon: ImageVector, val route: String) {
    object Home : BottomNavItem("Home", Icons.Default.Home, "home_screen")
    object Meds : BottomNavItem("Remédios", Icons.Default.List, "medicamentos")
    object Sinais : BottomNavItem("Sinais", Icons.Default.Favorite, "sinais")
}

@Composable
fun NavigationMenu(navController: NavController, usuarioId: Int) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Meds,
        BottomNavItem.Sinais
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            val isSelected = currentRoute == item.route

            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = isSelected,
                onClick = {
                    val routeWithQuery = "${item.route}?usuarioId=$usuarioId"

                    if (currentRoute != routeWithQuery) {
                        navController.navigate(routeWithQuery) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}
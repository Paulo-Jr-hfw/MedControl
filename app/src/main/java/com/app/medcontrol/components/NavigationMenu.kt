package com.app.medcontrol.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

sealed class BottomNavItem(val title: String, val icon: ImageVector, val route: String) {
    object Home : BottomNavItem("Home", Icons.Default.Home, "home_screen")
    object Meds : BottomNavItem("Remédios", Icons.AutoMirrored.Filled.List, "medicamentos")
    object Sinais : BottomNavItem("Sinais", Icons.Default.Favorite, "sinais")

    object Historico : BottomNavItem("Histórico", Icons.Default.History, "historico")
}

@Composable
fun NavigationMenu(navController: NavController, usuarioId: Int) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Meds,
        BottomNavItem.Sinais,
        BottomNavItem.Historico
    )

    Surface(
        modifier = Modifier
            .padding(
                vertical = 10.dp
            )
            .clip(
                RoundedCornerShape(
                    topStart = 28.dp,
                    topEnd = 28.dp
                )
            ),

        color = Color.White.copy(alpha = 0.25f),

        border = BorderStroke(
            1.dp,
            Color.White.copy(alpha = 0.5f)
        ),

        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        NavigationBar(
            modifier = Modifier.height(72.dp),
            containerColor = Color.Transparent,
            tonalElevation = 0.dp
        ) {
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
}
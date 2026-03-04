package com.app.medcontrol.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.medcontrol.screen.LoginScreen
import com.app.medcontrol.screen.Paciente.HomeScreen
import com.app.medcontrol.screen.cadastrouser.CadastroUserScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = Routes.Login.route) {

        composable(Routes.Login.route) {
            LoginScreen(
                onNavigateToCadastro = { navController.navigate(Routes.CadastroUser.route) },
                onNavigateHome = {
                    navController.navigate(Routes.HomeScreen.route) {
                        popUpTo(Routes.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.CadastroUser.route) {
            CadastroUserScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.HomeScreen.route) {
            HomeScreen( medicamentos = emptyList())
        }
    }
}
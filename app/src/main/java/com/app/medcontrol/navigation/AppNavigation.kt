package com.app.medcontrol.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.app.medcontrol.screen.cadastromed.CadastroMedScreen
import com.app.medcontrol.screen.cadastrosinais.CadastroSinaisScreen
import com.app.medcontrol.screen.cadastrouser.CadastroUserScreen
import com.app.medcontrol.screen.login.LoginScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.Login.route
    ) {

        composable(Routes.Login.route) {
            LoginScreen(
                onNavigateToCadastro = { navController.navigate(Routes.CadastroUser.route) },
                onNavigateHome = { id ->
                    navController.navigate("${Routes.HomeScreen.route}/$id") {
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

        composable(route = "${Routes.CadastroMed.route}/{usuarioId}",
            arguments = listOf(navArgument("usuarioId") { type = NavType.IntType })
        ) {
            CadastroMedScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = "${Routes.CadastroSinais.route}/{usuarioId}",
            arguments = listOf(navArgument("usuarioId") { type = NavType.IntType })
        ){
            CadastroSinaisScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "${Routes.HomeScreen.route}/{usuarioId}",
            arguments = listOf(navArgument("usuarioId") { type = NavType.IntType })
        ) {backStackEntry ->
            val idLogado = backStackEntry.arguments?.getInt("usuarioId") ?: 0

            MainContainer(
                usuarioId = idLogado,
                onNavigateToGlobalRoute = { rotaGlobal ->
                    navController.navigate(rotaGlobal)
                }
            )
        }
    }
}
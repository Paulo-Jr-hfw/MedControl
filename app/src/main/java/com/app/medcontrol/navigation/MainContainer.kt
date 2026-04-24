package com.app.medcontrol.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.app.medcontrol.components.NavigationMenu
import com.app.medcontrol.screen.Paciente.PacienteHomeScreen
import com.app.medcontrol.screen.medicamento.MedicamentoScreen
import com.app.medcontrol.screen.sinais.SinaisScreen


@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun MainContainer(
    usuarioId: Int,
    onNavigateToGlobalRoute: (String) -> Unit
) {

    val internalNavController = rememberNavController()
    val queryArg = "?usuarioId={usuarioId}"

    Scaffold(
        bottomBar = {
            NavigationMenu(
                navController = internalNavController,
                usuarioId = usuarioId
            )
        }
    ) { paddingValues ->
        NavHost(
            navController = internalNavController,
            startDestination = "${Routes.HomeScreen.route}$queryArg",
            modifier = Modifier.padding(paddingValues)
        ) {

            composable(
                route = "${Routes.HomeScreen.route}$queryArg",
                arguments = listOf(
                    navArgument("usuarioId") {
                        type = NavType.IntType
                        defaultValue = usuarioId
                    }
                )
            ) { backStackEntry ->
                val idRecuperado = backStackEntry.arguments?.getInt("usuarioId") ?: usuarioId

                PacienteHomeScreen(
                    onNavigateToCadastroMed = {
                        onNavigateToGlobalRoute("${Routes.CadastroMed.route}/$idRecuperado")
                    },
                    onNavigateToCadastroSinais = {
                        onNavigateToGlobalRoute("${Routes.CadastroSinais.route}/$idRecuperado")
                    }
                )
            }

            composable(
                route = "${Routes.Medicamentos.route}$queryArg",
                arguments = listOf(navArgument("usuarioId") { type = NavType.IntType })
            ) {
                MedicamentoScreen(
                    onNavigateToCadastro = {
                        onNavigateToGlobalRoute("${Routes.CadastroMed.route}/$usuarioId")
                    },
                    onNavigateToDetalhes = { idMed ->
                        onNavigateToGlobalRoute("${Routes.Detalhes.route}/$idMed/$usuarioId")
                    }
                )
            }

            composable(
                route = "${Routes.Sinais.route}$queryArg",
                arguments = listOf(navArgument("usuarioId") { type = NavType.IntType })
            ) {
                SinaisScreen(
                    onNavigateToManual = {
                        onNavigateToGlobalRoute("${Routes.CadastroSinais.route}/$usuarioId")
                    }
                )
            }
        }
    }
}
package com.app.medcontrol.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.app.medcontrol.components.MeshBackground
import com.app.medcontrol.components.NavigationMenu
import com.app.medcontrol.screen.acompanhante.AcompanhanteHomeScreen
import com.app.medcontrol.screen.historico.LogGeralScreen
import com.app.medcontrol.screen.medicamento.MedicamentoScreen
import com.app.medcontrol.screen.sinais.SinaisScreen
import com.app.medcontrol.ui.theme.LavenderLight
import com.app.medcontrol.ui.theme.MedControlTheme
import com.app.medcontrol.ui.theme.PurpleBase
import com.app.medcontrol.ui.theme.VioletDeep

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun AcompanhanteContainer(
    usuarioId: Int,
    onNavigateToGlobalRoute: (String) -> Unit
) {
    val internalNavController = rememberNavController()
    val queryArg = "?usuarioId={usuarioId}"

    MedControlTheme(isCompanion = true) {
        MeshBackground(
            baseColor = PurpleBase,
            topSpotColor = LavenderLight,
            bottomSpotColor = VioletDeep
        ) {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                NavigationMenu(
                    navController = internalNavController,
                    usuarioId = usuarioId,
                    isAcompanhante = true
                )
            }
        ) { paddingValues ->

            NavHost(
                navController = internalNavController,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = paddingValues.calculateBottomPadding()),
                startDestination = "${Routes.AcompanhanteHome.route}$queryArg"
            ) {

                composable(
                    route = "${Routes.AcompanhanteHome.route}$queryArg",
                    arguments = listOf(
                        navArgument("usuarioId") {
                            type = NavType.IntType
                            defaultValue = usuarioId
                        }
                    )
                ) {
                    AcompanhanteHomeScreen(
                        onLogout = {
                            onNavigateToGlobalRoute(Routes.Login.route)
                        }
                    )
                }

                composable(
                    route = "${Routes.Medicamentos.route}$queryArg",
                    arguments = listOf(navArgument("usuarioId") { type = NavType.IntType })
                ) {
                    MedicamentoScreen(
                        isReadOnly = true,
                        onNavigateToCadastro = { /* Desabilitado */ },
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
                        isReadOnly = true,
                        onNavigateToManual = { /* Desabilitado */ }
                    )
                }

                composable(
                    route = "${Routes.Historico.route}$queryArg",
                    arguments = listOf(navArgument("usuarioId") { type = NavType.IntType })
                ) {
                    LogGeralScreen(
                        onVoltar = { internalNavController.popBackStack() }
                    )
                }
            }
        }
    }
}
}

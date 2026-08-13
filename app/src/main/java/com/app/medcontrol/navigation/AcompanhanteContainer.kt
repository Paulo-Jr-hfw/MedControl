package com.app.medcontrol.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.app.medcontrol.components.MeshBackground
import com.app.medcontrol.components.NavigationMenu
import com.app.medcontrol.screen.acompanhante.AcompanhanteHomeScreen
import com.app.medcontrol.screen.acompanhante.AcompanhanteHomeScreenViewModel
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
    val viewModel: AcompanhanteHomeScreenViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    
    val pacienteId = if (uiState.estaVinculado) uiState.pacienteId else 0

    MedControlTheme(isCompanion = true) {
        MeshBackground(
            baseColor = PurpleBase,
            topSpotColor = LavenderLight,
            bottomSpotColor = VioletDeep
        ) {
            Scaffold(
                containerColor = Color.Transparent,
                bottomBar = {
                    if (uiState.estaVinculado) {
                        NavigationMenu(
                            navController = internalNavController,
                            pacienteId = pacienteId,
                            isAcompanhante = true
                        )
                    }
                }
            ) { paddingValues ->

                NavHost(
                    navController = internalNavController,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = paddingValues.calculateBottomPadding()),
                    startDestination = "${Routes.AcompanhanteHome.route}${Routes.Login.pacienteIdArg}"
                ) {

                    composable(
                        route = "${Routes.AcompanhanteHome.route}${Routes.Login.pacienteIdArg}",
                        arguments = listOf(
                            navArgument("pacienteId") {
                                type = NavType.IntType
                                defaultValue = 0 
                            }
                        )
                    ) {
                        AcompanhanteHomeScreen(
                            viewModel = viewModel,
                            onLogout = {
                                onNavigateToGlobalRoute(Routes.Login.route)
                            }
                        )
                    }

                    composable(
                        route = "${Routes.Medicamentos.route}${Routes.Login.pacienteIdArg}",
                        arguments = listOf(
                            navArgument("pacienteId") {
                                type = NavType.IntType
                                defaultValue = pacienteId
                            }
                        )
                    ) {
                        MedicamentoScreen(
                            isReadOnly = true,
                            onNavigateToCadastro = { /* Desabilitado */ },
                            onNavigateToDetalhes = { idMed ->
                                onNavigateToGlobalRoute("${Routes.Detalhes.route}/$idMed/$pacienteId")
                            }
                        )
                    }

                    composable(
                        route = "${Routes.Sinais.route}${Routes.Login.pacienteIdArg}",
                        arguments = listOf(
                            navArgument("pacienteId") {
                                type = NavType.IntType
                                defaultValue = pacienteId
                            }
                        )
                    ) {
                        SinaisScreen(
                            isReadOnly = true,
                            onNavigateToManual = { /* Desabilitado */ }
                        )
                    }

                    composable(
                        route = "${Routes.Historico.route}${Routes.Login.pacienteIdArg}",
                        arguments = listOf(
                            navArgument("pacienteId") {
                                type = NavType.IntType
                                defaultValue = pacienteId
                            }
                        )
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

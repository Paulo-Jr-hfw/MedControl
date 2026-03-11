package com.app.medcontrol.navigation

sealed class Routes (val route: String) {
    object Login: Routes("login")
    object CadastroUser: Routes("cadastro_user")
    object HomeScreen: Routes("home_screen")

    object CadastroMed: Routes("cadastro_med")


}
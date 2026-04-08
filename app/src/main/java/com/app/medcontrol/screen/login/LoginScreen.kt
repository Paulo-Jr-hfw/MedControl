package com.app.medcontrol.screen.login

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.medcontrol.R
import com.app.medcontrol.components.PerfilSelect
import com.app.medcontrol.model.TipoUsuario

@Composable
fun LoginScreen(
    viewModel: LoginScreenViewModel = hiltViewModel(),
    onNavigateToCadastro: () -> Unit,
    onNavigateHome: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var tipoSelecionado by remember { mutableStateOf(TipoUsuario.PACIENTE) }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(uiState.sucesso){
        if (uiState.sucesso) {
            val idUsuario = uiState.usuarioLogado?.usuarioId ?: 0

            onNavigateHome(idUsuario)
            viewModel.resetSucessoLogin()
        }
    }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                }
                .padding(16.dp)) {
            HeaderIncial(tipo = tipoSelecionado)

            Spacer(modifier = Modifier.height(24.dp))

            PerfilSelect(
                tipoSelecionado = tipoSelecionado,
                onTipoSelected = { tipoSelecionado = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            UserLogin(
                tipo = tipoSelecionado,
                email = uiState.email,
                senha = uiState.senha,
                onLogin = {viewModel.onLogin(tipoSelecionado)},
                onEmailChange = viewModel::onEmailChange,
                onSenhaChange = viewModel::onPasswordChange,
                loginError = uiState.loginError,
                mensagemErro = uiState.mensagemErro,
                onNavigateToCadastroUser = onNavigateToCadastro,
                isLoading = uiState.isLoading
            )

    }
}

@Composable
fun HeaderIncial(modifier: Modifier = Modifier, tipo: TipoUsuario) {
    Column(modifier = modifier) {
        Icon(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = null,
        )
        Text(text = "MEDCONTROL")
        Text(text = "Controle de medicamentos inteligente")

    }
}

@Composable
fun UserLogin(tipo: TipoUsuario,
              modifier: Modifier = Modifier,
              email: String,
              senha: String,
              loginError: Boolean,
              isLoading: Boolean,
              onLogin: () -> Unit,
              onEmailChange: (String) -> Unit,
              onSenhaChange: (String) -> Unit,
              mensagemErro: String,
              onNavigateToCadastroUser: () -> Unit) {

    val primaryColor = if (tipo == TipoUsuario.PACIENTE) Color(0xFF4CAF50) else Color(0xFF673AB7)
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        AnimatedContent(
            targetState = tipo,
            transitionSpec = {
                (fadeIn(animationSpec = tween(400)) + slideInVertically())
                    .togetherWith(fadeOut(animationSpec = tween(400)) + slideOutVertically())
            },
            label = "TextTransition"
        ) { targetTipo ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = if (targetTipo == TipoUsuario.PACIENTE)
                        Icons.Default.Person else Icons.Default.SupervisorAccount,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = primaryColor
                )
                Text(
                    text = if (targetTipo == TipoUsuario.PACIENTE)
                        "Acesso Paciente" else "Acesso Acompanhante",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (targetTipo == TipoUsuario.PACIENTE)
                        "Gerencie seus medicamentos e sinais vitais"
                    else
                        "Acompanhe a saúde do seu paciente",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("E-mail") },
            isError = loginError,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) } ),
            shape = RoundedCornerShape(12.dp),
            supportingText = {
                if(loginError) Text(text = mensagemErro, color = Color.Red)
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = senha,
            onValueChange = onSenhaChange,
            label = { Text("Senha") },
            isError = loginError,
            supportingText = null,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() } ),
            shape = RoundedCornerShape(12.dp),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(24.dp))

        val animatedButtonColor by animateColorAsState(
            targetValue = primaryColor,
            animationSpec = tween(400),
            label = "ButtonColor"
        )

        Button(
            onClick = onLogin,
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = animatedButtonColor),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text(
                    text = if (tipo == TipoUsuario.PACIENTE) "ENTRAR COMO PACIENTE" else "ENTRAR COMO ACOMPANHANTE",
                    fontWeight = FontWeight.Bold
                )
            }
        }

        TextButton(onClick = { onNavigateToCadastroUser() }) {
            Text("Não possui cadastro? Crie sua conta aqui", color = Color.Gray)
        }
    }
}

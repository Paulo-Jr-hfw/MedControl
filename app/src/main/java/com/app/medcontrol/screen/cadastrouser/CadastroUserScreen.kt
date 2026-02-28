package com.app.medcontrol.screen.cadastrouser

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.medcontrol.R
import com.app.medcontrol.components.PerfilSelect

@Composable
fun CadastroUserScreen(
    viewModel: CadastroUserScreenViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(uiState.sucesso) {
        if (uiState.sucesso) {
            onNavigateBack()
        }
    }

    Scaffold(
        bottomBar = {
            Button(
                onClick = { viewModel.onSalvarUsuario() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("CRIAR CONTA", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState)
        ) {
            HeaderUser()

            PerfilSelect(
                tipoSelecionado = uiState.tipoSelecionado,
                onTipoSelected = { viewModel.onTipoSelected(it) }
            )
            Spacer(modifier = Modifier.height(24.dp))

            CadastroUserForm(
                nomeUser = uiState.nome,
                onNomeUserChange = viewModel::onNomeUserChange,
                nomeUserErro = uiState.nomeErro != null,
                email = uiState.email,
                onEmailChange = viewModel::onEmailChange,
                emailErro = uiState.emailErro != null,
                emailMensagemErro = uiState.emailErro ?: "E-mail inválido",
                senha = uiState.senha,
                onSenhaChange = viewModel::onSenhaChange,
                senhaErro = uiState.senhaErro != null
            )

        }
    }
}

@Composable
fun HeaderUser() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_launcher_foreground), // Substitua pela sua Logo
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Cadastro MedControl",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Crie sua conta para começar",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
}

@Composable
fun CadastroUserForm(
    nomeUser: String,
    onNomeUserChange: (String) -> Unit,
    nomeUserErro: Boolean,
    email: String,
    onEmailChange: (String) -> Unit,
    emailErro: Boolean,
    senha: String,
    onSenhaChange: (String) -> Unit,
    senhaErro: Boolean,
    emailMensagemErro: String = "E-mail inválido"
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = nomeUser,
            onValueChange = {
                onNomeUserChange(it)
            },
            label = { Text("Insira seu Primeiro nome") },
            modifier = Modifier.fillMaxWidth(),
            isError = nomeUserErro,
            supportingText = {
                if (nomeUserErro) {
                    Text(text = "O nome é obrigatório", color = MaterialTheme.colorScheme.error)
                }
            }
        )
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("E-mail") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = emailErro,
            supportingText = {
                if (emailErro) {
                    Text(text = "Email inválido", color = MaterialTheme.colorScheme.error)
                }
            }
        )
        OutlinedTextField(
            value = senha,
            onValueChange = onSenhaChange,
            label = { Text("Senha") },
            isError = senhaErro,
            modifier = Modifier.fillMaxWidth(),
            supportingText = {
                if (senhaErro) {
                    Text(text = "Senha inválida", color = MaterialTheme.colorScheme.error)
                }
            }

        )
    }
}
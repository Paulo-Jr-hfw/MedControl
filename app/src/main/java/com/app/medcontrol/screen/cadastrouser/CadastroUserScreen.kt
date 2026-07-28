package com.app.medcontrol.screen.cadastrouser

import android.widget.Toast
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.medcontrol.R
import com.app.medcontrol.components.GlassCard
import com.app.medcontrol.components.MeshBackground
import com.app.medcontrol.components.PerfilSelect
import com.app.medcontrol.model.TipoUsuario
import com.app.medcontrol.ui.theme.CompanionPrimary
import com.app.medcontrol.ui.theme.LavenderLight
import com.app.medcontrol.ui.theme.LimeLight
import com.app.medcontrol.ui.theme.MintBase
import com.app.medcontrol.ui.theme.PatientPrimary
import com.app.medcontrol.ui.theme.PureWhite
import com.app.medcontrol.ui.theme.PurpleBase
import com.app.medcontrol.ui.theme.TextSecondary
import com.app.medcontrol.ui.theme.TurquoiseDeep
import com.app.medcontrol.ui.theme.VioletDeep

@Composable
fun CadastroUserScreen(
    viewModel: CadastroUserScreenViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    LaunchedEffect(uiState.sucesso) {
        if (uiState.sucesso) {
            Toast.makeText(context, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show()
            onNavigateBack()
            viewModel.resetSucesso()
        }
    }

    val isPaciente = uiState.tipoSelecionado == TipoUsuario.PACIENTE

    MeshBackground(
        baseColor = if (isPaciente) MintBase else PurpleBase,
        topSpotColor = if (isPaciente) LimeLight else LavenderLight,
        bottomSpotColor = if (isPaciente) TurquoiseDeep else VioletDeep
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize().imePadding(),
            containerColor = Color.Transparent,
            bottomBar = {
                val buttonColor = if (isPaciente) PatientPrimary else CompanionPrimary
                Button(
                    onClick = { viewModel.onSalvarUsuario() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !uiState.isLoading,
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = buttonColor
                    )
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(color = PureWhite, modifier = Modifier.size(24.dp))
                    } else {
                        Text("CRIAR CONTA", fontWeight = FontWeight.Bold, color = PureWhite)
                    }
                }
            }
        ) { paddingValues ->
            val focusManager = LocalFocusManager.current
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = {
                            focusManager.clearFocus()
                        })
                    }
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

                GlassCard {
                    Column(modifier = Modifier.padding(8.dp)) {
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
                
                Spacer(modifier = Modifier.height(24.dp))
            }
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
            painter = painterResource(id = R.drawable.logo_app),
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
            color = TextSecondary
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
    val focusManager = LocalFocusManager.current

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = nomeUser,
            onValueChange = {
                onNomeUserChange(it)
            },
            label = { Text("Insira seu Primeiro nome") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) } ),
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
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) } ),
            isError = emailErro,
            supportingText = {
                if (emailErro) {
                    Text(text = emailMensagemErro, color = MaterialTheme.colorScheme.error)
                }
            }
        )
        OutlinedTextField(
            value = senha,
            onValueChange = onSenhaChange,
            label = { Text("Senha") },
            isError = senhaErro,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() } ),
            supportingText = {
                if (senhaErro) {
                    Text(text = "Senha inválida", color = MaterialTheme.colorScheme.error)
                }
            }

        )
    }
}
package com.app.medcontrol.screen.cadastromed

import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.app.medcontrol.components.SelecaoHorarios


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CadastroMedScreen(
    viewModel: CadastroMedScreenViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.onImagemUriChange(uri)
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is CadastroMedScreenViewModel.UiEvent.CadastroSucesso -> {
                    Toast.makeText(context, "Medicamento salvo!", Toast.LENGTH_SHORT).show()
                    onNavigateBack()
                }

                is CadastroMedScreenViewModel.UiEvent.ShowError -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    LaunchedEffect(uiState.listaHorarios.size) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().imePadding(),
        bottomBar = {
            BotaoSalvar(
                onClick = { viewModel.onSalvaMedicamento() },
                enabled = !uiState.isLoading,
                modifier = Modifier.padding(16.dp)
            )
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
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            FotoCadastro(
                uri = uiState.imagemUri,
                onImagemUriChange = {
                    galleryLauncher.launch("image/*")
                })
            Spacer(modifier = Modifier.height(16.dp))

            FormCadastroMed(
                nome = uiState.nomeMed,
                onNomeChange = viewModel::onNomeMedChange,
                nomeErro = uiState.nomeMedErro,
                dosagem = uiState.dosagem,
                onDosagemChange = viewModel::onDosagemChange,
                dosagemErro = uiState.dosagemErro,
                instrucoes = uiState.instrucoes,
                onInstrucoesChange = viewModel::onInstrucoesChange
            )

            Spacer(modifier = Modifier.height(16.dp))

            SelecaoHorarios(
                horarios = uiState.listaHorarios,
                onAdicionarHorario = { viewModel.onAdicionarHorario() },
                onAtualizarHorario = viewModel::onAtualizarHorario,
                onRemoverHorario = viewModel::onRemoverHorario,
                indexEditando = uiState.indexHorarioEditando,
                onSetEditando = viewModel::setEditando
            )
        }
    }
}

@Composable
fun FotoCadastro(
    uri: Uri?,
    onImagemUriChange: () -> Unit
) {
    Card(
        onClick = onImagemUriChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (uri == null) PlaceholderFoto() else ImagemSelecionada(uri)
        }
    }
}

@Composable
fun FormCadastroMed(
    nome: String,
    onNomeChange: (String) -> Unit,
    nomeErro: Boolean,
    dosagem: String,
    onDosagemChange: (String) -> Unit,
    dosagemErro: Boolean,
    instrucoes: String,
    onInstrucoesChange: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = nome,
            onValueChange = {
                onNomeChange(it)
            },
            label = { Text("Nome do medicamento") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) } ),
            isError = nomeErro,
            supportingText = {
                if (nomeErro) {
                    Text(text = "O nome é obrigatório", color = MaterialTheme.colorScheme.error)
                }
            }
        )
        OutlinedTextField(
            value = dosagem,
            onValueChange = onDosagemChange,
            label = { Text("Dosagem") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) } ),
            isError = dosagemErro,
            supportingText = {
                if (dosagemErro) {
                    Text(text = "A dosagem é obrigatória", color = MaterialTheme.colorScheme.error)
                }
            }
        )
        OutlinedTextField(
            value = instrucoes,
            onValueChange = onInstrucoesChange,
            label = { Text("Instruções") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() } ),
            minLines = 3,
            maxLines = 5
        )
    }
}


@Composable
private fun PlaceholderFoto() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Default.AddAPhoto, null, Modifier.size(48.dp))
        Spacer(Modifier.height(8.dp))
        Text("TIRAR FOTO DO MEDICAMENTO")
        Text("Toque para adicionar uma foto")
    }
}

@Composable
private fun ImagemSelecionada(uri: Uri) {
    AsyncImage(
        model = uri,
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun BotaoSalvar(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    enabled: Boolean
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(if (enabled) "SALVAR" else "SALVANDO...")
    }
}


package com.app.medcontrol.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.app.medcontrol.R
import com.app.medcontrol.model.Medicamento
import java.time.LocalTime

@Composable
fun MedicamentoItem (medicamento: Medicamento) {
    Card(
        modifier = Modifier.padding(8.dp)
    ) {
        Row {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = medicamento.nome)
            Text(text = medicamento.dosagem)
            Text(text = medicamento.instrucoes)
            Text(text = medicamento.horario.toString())
        }
            Card(
                modifier = Modifier
                    .size(100.dp)
                    .padding(8.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                AsyncImage(
                    model = medicamento.imagemUri,
                    contentDescription = null,
                    placeholder = painterResource(R.drawable.ic_launcher_foreground),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun MedicamentoItemPreview() {
    val medicamentoTeste = Medicamento(
        id = 1,
        nome = "Dipirona",
        dosagem = "500mg",
        instrucoes = "Tomar após as refeições",
        horario = listOf(LocalTime.of(8, 0), LocalTime.of(20, 0)),
        imagemUri = null
    )
    MedicamentoItem(medicamento = medicamentoTeste)
}
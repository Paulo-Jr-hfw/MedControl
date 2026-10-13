# 💊 MedControl - Gestão Inteligente de Medicamentos e Saúde

> **MedControl** é um aplicativo Android nativo desenvolvido para transformar a rotina de adesão a tratamentos médicos e monitoramento de saúde, conectando pacientes e cuidadores através de uma experiência intuitiva, reativa e altamente confiável.

[![Android](https://img.shields.io/badge/PLATFORM-ANDROID-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/)<br>
[![Kotlin](https://img.shields.io/badge/LANGUAGE-KOTLIN-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)<br>
[![Jetpack Compose](https://img.shields.io/badge/UI-JETPACK%20COMPOSE-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)<br>
[![Hilt](https://img.shields.io/badge/DEPENDENCY%20INJECTION-HILT-546E7A?style=for-the-badge&logo=dagger&logoColor=white)](https://dagger.dev/hilt/)<br>
[![Status](https://img.shields.io/badge/STATUS-COMPLETO-2E7D32?style=for-the-badge&logo=checkmarx&logoColor=white)](#)

---

## 📌 O Problema

A baixa adesão a tratamentos medicamentosos e a falta de acompanhamento adequado são grandes desafios na área da saúde. Os principais entraves enfrentados por pacientes e cuidadores incluem:

- ⏳ **Esquecimento e Atraso nos Horários:** Dificuldade em gerenciar rotinas complexas com múltiplos medicamentos e dosagens ao longo do dia.
- 👨‍👩‍👧 **Ausência de Monitoramento por Cuidadores:** Familiares e acompanhantes não possuem visibilidade em tempo real sobre a confirmação da tomada dos remédios.
- 🔌 **Perda de Agendamentos Pós-Reinício:** Aplicativos comuns perdem seus alarmes configurados quando o smartphone é desligado ou reiniciado.
- 📊 **Fragmentação dos Sinais Vitais:** Dificuldade em registrar e centralizar medições importantes (pressão, glicemia, temperatura) junto com o histórico de medicação.

---

## 💡 A Solução

O **MedControl** foi idealizado para superar essas barreiras com uma arquitetura moderna, robusta e resiliente no ecossistema Android:

- 🔔 **Sistema de Lembretes Cirúrgicos & Resilientes:** Alarmes de alta precisão via `AlarmManager` (`setExactAndAllowWhileIdle`), funcionando mesmo em modo de economia (*Doze Mode*), acompanhados por um `BootReceiver` que restaura os lembretes após o reinício do dispositivo.
- 👥 **Perfis Distintos (Paciente & Acompanhante):** Módulo de autenticação e vínculo de perfis, permitindo que cuidadores acompanhem a rotina do paciente.
- 🩺 **Monitoramento de Sinais Vitais:** Registro centralizado de métricas corporais com suporte à integração com o **Health Connect**.
- ⏱️ **Mural de Logs Reativo (Timeline):** Linha do tempo atualizada dinamicamente com base no status da dose (*Concluído*, *Atrasado*, *Esquecido*).
- 🎨 **Interface Premium & Fluida:** UI moderna construída integralmente com Jetpack Compose, suporte a Glassmorphism e animações reativas baseadas em estados.

---

## ✨ Funcionalidades Principais

### 🎯 Gestão de Medicamentos & Doses
- **Cadastro Completo:** Nome, dosagem, horários personalizados e foto do medicamento (via Coil).
- **Confirmação Imediata:** Registro de consumo rápido com cancelamento automático de alarmes ativos.
- **Validação Inteligente:** Validação de formatos e verificação de duplicidade diretamente no banco local.

### 🔔 Lembretes Avançados & Resiliência
- **Alarmes Exatos:** `AlarmManager` configurado para disparos garantidos no horário correto.
- **Ciclo de Tolerância (9h):** O `AlarmReceiver` repete o lembrete periodicamente. Se o limite de 9 horas for atingido sem confirmação, a dose é categorizada automaticamente como *Esquecida*.
- **Persistência Pós-Boot:** O `BootReceiver` intercepta o evento `ACTION_BOOT_COMPLETED`, reagendando automaticamente todas as doses do dia.

### 📈 Histórico & Linha do Tempo
- **Mural Reativo (Timeline):** Alimentado de forma assíncrona pelo `LogRepository`, registrando sucessos, atrasos e esquecimentos.
- **Gravação Persistente:** Histórico detalhado armazenado no banco via `HistoricoMedicamentoEntity`.

### 👥 Perfis & Módulo Acompanhante
- **Navegação Customizada:** Interfaces adaptadas ao perfil logado (Paciente vs. Acompanhante/Cuidador).
- **Vínculo entre Perfis:** Mecanismo de associação via `VinculoAcompanhanteEntity` e casos de uso (`VincularAcompanhanteUseCase`, `DesvincularUseCase`).

---

## 🛠️ Arquitetura & Tecnologias

O projeto adota os padrões recomendados pelo **Modern Android Development (MAD)** com princípios de **Clean Architecture** e **MVVM**.

### Stack Tecnológica

- **Linguagem:** [Kotlin](https://kotlinlang.org/) (Coroutines & Flow)
- **Interface Gráfica:** [Jetpack Compose](https://developer.android.com/jetpack/compose) + Material Design 3
- **Injeção de Dependências:** [Dagger Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
- **Persistência Local:** [Room Database](https://developer.android.com/training/data-storage/room) com KSP
- **Agendamentos & Background:** `AlarmManager` + `BroadcastReceiver` + `NotificationManager`
- **Saúde & Biometria:** [Health Connect API](https://developer.android.com/guide/topics/connectivity/health-connect)
- **Sessão & Preferências:** [Jetpack DataStore](https://developer.android.com/topic/libraries/architecture/datastore)
- **Carregamento de Imagens:** [Coil](https://coil-kt.github.io/coil/)

---

## 📸 Screenshots
*Imagens da interface conforme o desenvolvimento avançar.*

---

## 🚀 Como Executar o Projeto

### Pré-requisitos

- **Android Studio:** Jellyfish / Ladybug (ou versão mais recente)
- **JDK:** 11 ou 17
- **SDK Mínima (`minSdk`):** 26 (Android 8.0 Oreo)
- **SDK Compilação (`compileSdk`):** 36

### Passo a Passo

1. **Clonar o Repositório:** git clone https://github.com/Paulo-Jr-hfw/MedControl.git
2. **Abrir no Android Studio:** Abra o Android Studio e escolha a opção Open e selecione a pasta do projeto.
3. **Sincronizar o Gradle:** Aguarde o término do download de dependências e sincronização do Gradle.
4. **Executar no Emulador ou Dispositivo Físico:** Seleciona um dispositivo compatível e Pressione o botão **Run (Shift + F10)** no Android Studio

## 👨‍💻 Desenvolvedor
[Paulo Junior] - [LinkedIn](https://www.linkedin.com/in/paulo-junior-8b64b633b/)

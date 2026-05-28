# 💊 MedControl - Controle de Medicamentos
> ![Em Desenvolvimento](https://img.shields.io/badge/STATUS-EM_DESENVOLVIMENTO-yellow?style=for-the-badge)

O **MedControl** é um aplicativo Android desenvolvido para auxiliar pacientes e cuidadores no controle rigoroso da ingestão de medicamentos e monitoramento de sinais vitais.
O foco deste projeto é aplicar as melhores práticas de desenvolvimento Android moderno, focando em arquitetura, performance e experiência do usuário.

---

## ✨ Funcionalidades Implementadas (MVP Core)

### 🎯 Core & Gestão de Medicamentos
- [x] **Gestão de Medicamentos:** Cadastro completo de remédios contendo nome, dosagem, horários e suporte a captura/vínculo de fotos (via Coil).
- [x] **Fluxo de Autenticação:** Telas de Login e Registo robustas com distinção de perfis entre Paciente e Acompanhante/Cuidador.
- [x] **Validação Inteligente:** Validação de formatos em tempo real (Patterns) e verificação de duplicidade de registos diretamente no banco de dados.

### 🔔 Sistema Avançado de Lembretes & Resiliência
- [x] **Alarmes Cirúrgicos (Exatos):** Uso do `AlarmManager` com `setExactAndAllowWhileIdle` para garantir notificações pontuais, mesmo com o dispositivo em modo de economia de energia (*Doze Mode*).
- [x] **Ciclo de Vida de Tolerância (9h):** Mecânica inteligente no `AlarmReceiver` que repete alertas hora a hora. Caso o limite de 9 horas de atraso seja atingido sem a confirmação do utilizador, a dose é marcada automaticamente como *Esquecida*.
- [x] **Persistência Pós-Reinicialização:** Implementação de um `BootReceiver` que intercepta o reinício do telemóvel (`ACTION_BOOT_COMPLETED`), reagendando automaticamente todas as doses diárias pendentes ou já atrasadas.

### 📈 Histórico & Linha do Tempo
- [x] **Confirmação de Ingestão:** Botão de ação rápida para registar o consumo imediato da dose, cancelando o alarme ativo daquele horário.
- [x] **Mural Reativo de Logs (Timeline):** Linha do tempo dinâmica alimentada por um `LogRepository`, que regista de forma assíncrona o sucesso, atrasos ou esquecimentos de cada medicamento.
- [x] **Histórico de Consumo:** Gravação persistente do planeamento vs. execução real através da tabela `HistoricoMedicamentoEntity`.

### 🎨 UI/UX & Arquitetura Premium
- [x] **Arquitetura MVVM Rígida:** Separação clara de responsabilidades com o uso de ViewModels, tratamento de erros centralizado e fluxos de dados assíncronos.
- [x] **Reatividade com Kotlin Flows:** Telas atualizadas instantaneamente através da combinação de múltiplos fluxos do Room (`getDosesPendentesFlow`, `getTotalDosesDoDia`), convertidos em estados de UI seguros via `stateIn`.
- [x] **Animações Fluidas:** Transições de ecrã premium usando `updateTransition`, `AnimatedContent` e alinhamentos dinâmicos (`BiasAlignment`).
- [x] **Injeção de Dependências:** Desacoplamento completo de componentes e repositórios utilizando Dagger Hilt.

---

## 🚀 Funcionalidades Planejadas

- [ ] **Modo Acompanhante:** Sincronização em tempo real entre dois usuários (Paciente e Cuidador).
- [ ] **Alertas Remotos:** Possibilidade do acompanhante enviar lembretes urgentes.
- [ ] **Design:** Refatorar design do app para melhorar experiência do utilizador.

---

## 🛠️ Tecnologias Utilizadas

- **Linguagem:** [Kotlin](https://kotlinlang.org/)
- **Interface UI:** [Jetpack Compose](https://developer.android.com/jetpack/compose) (Declarative UI)
- **Arquitetura:** MVVM + Clean Architecture Principles
- **Injeção de Dependências:** [Dagger Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
- **Persistência de Dados:** [Room Database](https://developer.android.com/training/data-storage/room)
- **Animações:** Compose Animation (UpdateTransition, AnimatedContent)
- **Carregamento de Imagens:** [Coil](https://coil-kt.github.io/coil/)
- **Agendamentos:** `AlarmManager` (BroadcastReceivers dedicados para eventos de alarme e Boot do sistema)
- **Trabalho em Segundo Plano:** [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager)
- **Backend/Sync:** (A definir: Firebase)

---

## 📸 Screenshots (Em breve)
*Imagens da interface conforme o desenvolvimento avançar.*

---

## 👨‍💻 Desenvolvedor
[Paulo Junior] - [LinkedIn](https://www.linkedin.com/in/paulo-junior-8b64b633b/)

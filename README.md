# 💊 MedControl - Controle de Medicamentos
> ![Em Desenvolvimento](https://img.shields.io/badge/STATUS-EM_DESENVOLVIMENTO-yellow?style=for-the-badge)

O **MedControl** é um aplicativo Android desenvolvido para auxiliar pacientes e cuidadores no controle rigoroso da ingestão de medicamentos e monitoramento de sinais vitais.
O foco deste projeto é aplicar as melhores práticas de desenvolvimento Android moderno, focando em arquitetura, performance e experiência do usuário (UX).

---

## ✨ Funcionalidades Implementadas (MVP Core)

- [x] **Fluxo de Autenticação:** Telas de Login e Cadastro com distinção entre perfis (Paciente/Acompanhante).
- [x] **Arquitetura Reativa:** Implementação de ViewModels com estados observáveis e tratamento de erros em tempo real.
- [x] **Animações Premium:** Transições de perfil fluidas usando `updateTransition` e `BiasAlignment`.
- [x] **Persistência Local:** Banco de dados Room configurado com suporte a tipos complexos (LocalTime) via TypeConverters.
- [x] **Validação Inteligente:** Validação de formato de e-mail (Patterns) e verificação de duplicidade no banco de dados.
- [x] **Injeção de Dependência:** Configuração completa com Dagger Hilt para desacoplamento de código.

---

## 🚀 Funcionalidades Planejadas

- [ ] **Gestão de Medicamentos:** Cadastro completo com nome, dosagem, instruções e fotos.
- [ ] **Sistema de Lembretes:** Notificações inteligentes baseadas em horários personalizados.
- [ ] **Confirmação de Ingestão:** Registro de confirmação para cada dose tomada.
- [ ] **Histórico Completo:** Log de dia, hora e medicamento ingerido.
- [ ] **Modo Acompanhante:** Sincronização em tempo real entre dois usuários (Paciente e Cuidador).
- [ ] **Sinais Vitais:** Registro manual e integração com dispositivos wearable (Galaxy Watch).
- [ ] **Alertas Remotos:** Possibilidade do acompanhante enviar lembretes urgentes.

---

## 🛠️ Tecnologias Utilizadas

- **Linguagem:** [Kotlin](https://kotlinlang.org/)
- **Interface UI:** [Jetpack Compose](https://developer.android.com/jetpack/compose) (Declarative UI)
- **Arquitetura:** MVVM (Model-View-ViewModel)
- **Injeção de Dependências:** [Dagger Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
- **Persistência de Dados:** [Room Database](https://developer.android.com/training/data-storage/room)
- **Animações:** Compose Animation (UpdateTransition, AnimatedContent)
- **Carregamento de Imagens:** [Coil](https://coil-kt.github.io/coil/)
- **Trabalho em Segundo Plano:** [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager)
- **Backend/Sync:** (A definir: Firebase)

---

## 📸 Screenshots (Em breve)
*Imagens da interface conforme o desenvolvimento avançar.*

---

## 👨‍💻 Desenvolvedor
[Paulo Junior] - [LinkedIn](https://www.linkedin.com/in/paulo-junior-8b64b633b/)

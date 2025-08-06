# 💪 GymFlow - App de Gerenciamento de Treinos

## ✨ Funcionalidades

### ✅ Autenticação
- Login com Firebase Authentication

### ✅ Treinos (`Treino`)
- Listagem de treinos do usuário (Meus Treinos)
- Listagem de treinos sugeridos
- Criação de novo treino
- Edição de nome e descrição do treino
- Exclusão de treino com confirmação

### ✅ Exercícios (`Exercicio`)
- Relacionamento 1:N com treino
- Listagem de exercícios por treino
- Criação de novo exercício com imagem (upload opcional)
- Edição de nome, observações e imagem do exercício
- Exclusão de exercício com confirmação

---

## 🛠️ Tecnologias Utilizadas

- **Kotlin**
- **Android Jetpack**
  - ViewModel, LiveData, StateFlow
  - ViewBinding
- **Firebase**
  - Authentication
  - Firestore
  - Storage (para imagens de exercícios)
- **Material Design 3**
- **Koin** para injeção de dependência
- **Coil** para carregamento de imagens

---

## 📂 Estrutura do Projeto

```bash
com.osiel.gymflow
│
├── data                  # Implementações dos repositórios
├── domain
│   ├── model             # Modelos de domínio (Treino, Exercicio)
│   └── repository        # Interfaces dos repositórios
├── presentation
│   ├── adapter           # Adapters para RecyclerView
│   ├── auth              # Telas de login e autenticação
│   ├── home              # Tela inicial com listagem de treinos
│   ├── profile           # perfil incompleto do  usuário
│   ├── newworkout        # Tela de criação de treino
│   ├── workoutdetail     # Tela de detalhes de um treino (e exercícios)
│   └── viewmodel         # ViewModels com lógica de UI
├── di                    # Módulo Koin (injeção de dependência)
└── MainActivity.kt       # Host de navegação

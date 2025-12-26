# Patas Unidas 🐾

O **Patas Unidas** é uma aplicação móvel Android dedicada à adoção de animais. O sistema permite que utilizadores visualizem animais disponíveis, criem perfis para novos animais e façam a gestão das suas publicações, facilitando o encontro entre animais de estimação e potenciais adotantes.

## 🚀 Tecnologias Utilizadas

* **Kotlin**: Linguagem de programação moderna para Android.
* **Jetpack Compose**: Toolkit moderno para a construção de interfaces nativas declarativas.
* **Retrofit**: Cliente HTTP para consumo da API REST.
* **Navigation Compose**: Gestão de rotas e navegação entre ecrãs (Feed, Perfil, Detalhes, etc.).
* **Coil**: Biblioteca para carregamento de imagens de forma eficiente.
* **SharedPreferences**: Utilizado para a gestão local de tokens de autenticação.

## 📋 Funcionalidades

O projeto implementa as seguintes funcionalidades baseadas nas regras de negócio:

* **Autenticação**: Registo de novos utilizadores e login com validação de credenciais via API.
* **Feed de Animais**: Listagem dinâmica de animais disponíveis para adoção.
* **Gestão de Animais**: 
    * Criação de novos perfis de animais com fotos e descrição.
    * Edição de informações de animais já cadastrados.
    * Remoção de publicações feitas pelo utilizador.
* **Perfil do Utilizador**: Visualização dos dados do utilizador e dos animais publicados por ele.
* **Detalhes do Animal**: Ecrã dedicado com informações completas e contacto do responsável pelo animal.

## ⚙️ Arquitetura do Projeto

O app segue o padrão **MVVM (Model-View-ViewModel)** para garantir a separação de responsabilidades:

* **Data**: Contém os modelos de dados (DTOs) e a interface da API.
* **Features**: Organizado por funcionalidades (auth, feed, profile, animal_profile), contendo os ecrãs (Compose) e os respetivos ViewModels.
* **Navigation**: Define o gráfico de navegação e as rotas da aplicação.
* **Utils**: Componentes auxiliares como o gestor de tokens e respostas de rede.

## 🛠️ Como Executar

### Pré-requisitos
* Android Studio Ladybug ou superior.
* Java 17 ou 21.
* Dispositivo Android ou Emulador com API 24 (Android 7.0) ou superior.

### Passos
1.  Clone este repositório.
2.  Abra o projeto no Android Studio.
3.  Aguarde a sincronização das dependências do **Gradle**.
4.  Configure o endereço base da API no ficheiro `RetrofitClient.kt` se necessário.
5.  Execute a aplicação (Run).

## 📄 Regras de Negócio
O sistema foi desenvolvido respeitando os seguintes princípios:
* Apenas utilizadores autenticados podem publicar novos animais.
* Um utilizador só pode editar ou excluir animais que ele próprio publicou.
* O catálogo de animais é acessível para visualização geral no feed.

---
Desenvolvido por **ceciliopf**.

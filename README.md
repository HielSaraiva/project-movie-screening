# Sistema de Exibição de Filmes

## Índice

1. [Visão Geral do Projeto](#visão-geral-do-projeto)
2. [Estrutura do Projeto e Organização de Arquivos](#estrutura-do-projeto-e-organização-de-arquivos)
3. [Tecnologias Utilizadas](#tecnologias-utilizadas)
4. [Recursos e Funcionalidades](#recursos-e-funcionalidades)
5. [Executando as Aplicações](#executando-as-aplicações)
      - [Requisitos](#requisitos)
      - [macOS](#macos)
      - [Windows](#windows)
6. [Considerações Finais](#considerações-finais)

---

## Visão Geral do Projeto

O projeto Sistema de Exibição de Filmes é uma aplicação JavaFX desenvolvida para a disciplina de Sistemas Operacionais. Ele simula o gerenciamento de uma sala de cinema utilizando conceitos de sincronização de processos e threads, com foco especial na implementação de semáforos.

A aplicação demonstra como utilizar semáforos para controlar o acesso concorrente a recursos compartilhados - neste caso, assentos em uma sala de cinema - evitando condições de corrida e garantindo a integridade dos dados.

## Estrutura do Projeto e Organização de Arquivos

```
project-movie-screening/
├── pom.xml                     # Arquivo de configuração Maven
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── module-info.java  # Definições de módulos do Java
│   │   │   └── edu/
│   │   │       └── ifce/
│   │   │           ├── App.java             # Classe principal da aplicação
│   │   │           ├── PrimaryController.java    # Controlador da tela principal
│   │   │           └── SecondaryController.java  # Controlador da tela secundária
│   │   └── resources/
│   │       └── edu/
│   │           └── ifce/
│   │               ├── primary.fxml         # Layout da tela principal
│   │               └── secondary.fxml       # Layout da tela secundária
└── target/                     # Arquivos compilados (gerados durante o build)
```

## Tecnologias Utilizadas

- **Java 23.0.2**: Linguagem de programação principal
- **JavaFX 21**: Framework para interface gráfica
- **Maven**: Gerenciamento de dependências e build
- **FXML**: Descrição de interface de usuário
- **API de Concorrência do Java**: Para implementação de semáforos e threads

## Recursos e Funcionalidades

- **Simulação de Sala de Cinema**: Visualização interativa dos assentos disponíveis e ocupados
- **Controle de Acesso Concorrente**: Implementação de semáforos para evitar que dois clientes reservem o mesmo assento simultaneamente
- **Demonstração Visual de Processos**: Visualização em tempo real da execução de threads e operações de semáforo
- **Estatísticas de Desempenho**: Métricas sobre tempo de espera, utilização de recursos e possíveis deadlocks

## Executando as Aplicações

### Requisitos

- JDK 23.0.2 ou superior
- Apache Maven 3.6 ou superior
- Configuração adequada das variáveis de ambiente JAVA_HOME e PATH

### macOS

1. **Instale o JDK 23**
   ```zsh
   brew install openjdk@23
   ```

2. **Instale o Maven**
   ```zsh
   brew install maven
   ```

3. **Clone o repositório**
   ```zsh
   git clone [URL_DO_REPOSITÓRIO]
   cd project-movie-screening
   ```

4. **Compile o projeto**
   ```zsh
   mvn clean compile
   ```

5. **Execute a aplicação**
   ```zsh
   mvn javafx:run
   ```

### Windows

1. **Instale o JDK 23**
   - Baixe o instalador do [site oficial da Oracle](https://www.oracle.com/java/technologies/javase/jdk23-archive-downloads.html)
   - Execute o instalador e siga as instruções
   - Configure a variável de ambiente JAVA_HOME

2. **Instale o Maven**
   - Baixe o [Apache Maven](https://maven.apache.org/download.cgi)
   - Extraia o arquivo para um diretório de sua escolha
   - Adicione o diretório bin do Maven ao PATH do sistema

3. **Clone o repositório**
   ```cmd
   git clone [URL_DO_REPOSITÓRIO]
   cd project-movie-screening
   ```

4. **Compile o projeto**
   ```cmd
   mvn clean compile
   ```

5. **Execute a aplicação**
   ```cmd
   mvn javafx:run
   ```

## Considerações Finais

Este projeto demonstra conceitos importantes de Sistemas Operacionais, como:

- **Sincronização de processos**: Através da utilização de semáforos
- **Controle de acesso a recursos compartilhados**: Gerenciamento de assentos do cinema
- **Prevenção de deadlocks**: Implementação de estratégias para evitar impasses
- **Interface gráfica**: Visualização dos processos em tempo real

O código foi estruturado para facilitar o entendimento desses conceitos, utilizando práticas modernas de programação Java e aproveitando os recursos do JavaFX para criar uma interface intuitiva.

---

Desenvolvido como projeto para a disciplina de Sistemas Operacionais - IFCE 2025.1

# Sistema de Exibição de Filmes

---

## Índice
1. [Visão Geral](#visão-geral)
2. [Estrutura do Projeto](#estrutura-do-projeto)
3. [Tecnologias Utilizadas](#tecnologias-utilizadas)
4. [Guia de Instalação](#guia-de-instalação)
    - [Pré-requisitos](#pré-requisitos)
    - [Instalação no Windows](#instalação-no-windows)
    - [Instalação no macOS](#instalação-no-macos)
5. [Como Executar](#como-executar)
    - [Executando com Maven](#executando-com-maven)
    - [Executando o JAR com JavaFX](#executando-o-jar-com-javafx)
6. [Funcionalidades](#funcionalidades)
7. [Funcionalidades Especiais](#funcionalidades-especiais)
8. [Considerações Finais](#considerações-finais)

---

## Visão Geral
O projeto **Sistema de Exibição de Filmes** é uma aplicação JavaFX para simulação de uma sala de cinema, demonstrando conceitos de sincronização de processos e threads com semáforos.

## Estrutura do Projeto
```
project-movie-screening/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── module-info.java
│   │   │   └── edu/
│   │   │       └── ifce/
│   │   │           ├── App.java
│   │   │           ├── PrimaryController.java
│   │   │           └── SecondaryController.java
│   │   └── resources/
│   │       └── edu/
│   │           └── ifce/
│   │               ├── primary.fxml
│   │               └── secondary.fxml
│   │       └── audios/
│   │       └── imgs/
└── target/
```

## Tecnologias Utilizadas
- **Java 21 LTS**
- **JavaFX 21**
- **Maven**
- **FXML**
- **API de Concorrência do Java**

---

## Guia de Instalação

### Pré-requisitos
- JDK 21 ou superior
- Apache Maven 3.6 ou superior
- JavaFX SDK 21 ([Download oficial](https://jdk.java.net/javafx21/))

### Instalação no Windows
1. **Instale o JDK 21**
   - Baixe do [site oficial da Oracle](https://www.oracle.com/java/technologies/javase/jdk23-archive-downloads.html)
   - Instale e configure a variável de ambiente `JAVA_HOME`.
2. **Instale o Maven**
   - Baixe do [site oficial](https://maven.apache.org/download.cgi)
   - Extraia e adicione o diretório `bin` ao `PATH`.
3. **Instale o JavaFX SDK**
   - Baixe e extraia o JavaFX SDK 21 ([link](https://jdk.java.net/javafx21/)), por exemplo em `C:\javafx-sdk-21`.
   - Crie a variável de ambiente `PATH_TO_FX` apontando para a pasta `lib` do JavaFX:
     - No Windows, pesquise por "variáveis de ambiente" e adicione:
       - Nome: `PATH_TO_FX`
       - Valor: `C:\javafx-sdk-21\lib`
4. **Clone o repositório**
   ```cmd
   git clone https://github.com/HielSaraiva/project-movie-screening.git
   cd project-movie-screening
   ```

### Instalação no macOS
1. **Instale o JDK 21**
   ```zsh
   brew install openjdk@21
   ```
2. **Instale o Maven**
   ```zsh
   brew install maven
   ```
3. **Instale o JavaFX SDK**
   - Baixe e extraia o JavaFX SDK 21 ([link](https://jdk.java.net/javafx21/)), por exemplo em `/opt/javafx-sdk-21`.
   - Crie a variável de ambiente `PATH_TO_FX` no seu shell (adicione ao `~/.zshrc` ou `~/.bash_profile`):
     ```zsh
     export PATH_TO_FX=/opt/javafx-sdk-21/lib
     ```
4. **Clone o repositório**
   ```zsh
   git clone https://github.com/HielSaraiva/project-movie-screening.git
   cd project-movie-screening
   ```

---

## Como Executar

### Executando com Maven
- **Windows:**
  ```cmd
  mvn clean javafx:run
  ```
- **macOS:**
  ```zsh
  mvn clean javafx:run
  ```

### Executando o JAR com JavaFX
Se quiser rodar o `.jar` diretamente (fora do Maven), use o comando abaixo:

- **Windows (PowerShell ou CMD):**
  ```powershell
  java --module-path "$env:PATH_TO_FX" --add-modules javafx.controls,javafx.fxml,javafx.media -jar target/project-movie-screening-1.0.jar
  ```
- **macOS/Linux:**
  ```zsh
  java --module-path "$PATH_TO_FX" --add-modules javafx.controls,javafx.fxml,javafx.media -jar target/project-movie-screening-1.0.jar
  ```
> **Atenção:**
> - Sempre ajuste o caminho do JavaFX se necessário.
> - Clicar duas vezes no `.jar` **não funciona** para aplicações JavaFX puras.

---

## Distribuição Rápida (Pasta dist)

Para facilitar a execução, o projeto já inclui:
- O **JDK do Java**
- O **SDK do JavaFX**
- O arquivo **.jar** pronto para uso

todos dentro da pasta `dist`.

### Como rodar no Windows (PowerShell)
Abra o PowerShell na pasta `dist` e execute:

```powershell
java --module-path "$env:PATH_TO_FX" --add-modules javafx.controls,javafx.fxml,javafx.media -jar project-movie-screening-1.0.jar
```

> **Dica:**
> - Certifique-se de que a variável de ambiente `PATH_TO_FX` aponte para a pasta `lib` do JavaFX dentro da pasta `dist`.
> - Exemplo: `D:\caminho\para\dist\javafx-sdk-21.0.2\lib`

---

## Funcionalidades
- Simulação de sala de cinema com controle visual de assentos
- Controle de acesso concorrente com semáforos
- Demonstração visual de processos e threads
- Estatísticas de desempenho
- Contador de tempo regressivo para sessões e fãs

## Funcionalidades Especiais
- Fã **Vojvoda**: som e imagem especial
- Fã **Kanal**: som e imagem especial
- Fã **Fortaleza/Laion**: som e imagem especial

## Considerações Finais
Este projeto demonstra conceitos de sincronização, controle de recursos compartilhados, prevenção de deadlocks e interface gráfica com JavaFX.

Desenvolvido para a disciplina de Sistemas Operacionais - IFCE 2025.1

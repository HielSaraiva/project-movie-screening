# Guia Rápido de Execução

Para rodar este projeto, siga os passos abaixo **dentro desta pasta (dist)**:

1. **Execute o arquivo .bat:**
   - Basta dar dois cliques no arquivo `iniciar-cinema.bat` para configurar o JavaFX e iniciar o sistema automaticamente.

2. **(Alternativa) Execute manualmente pelo PowerShell:**
   - Configure a variável `PATH_TO_FX` apontando para a pasta `lib` do JavaFX SDK que está aqui dentro.
     - Exemplo no PowerShell:
       ```powershell
       $env:PATH_TO_FX = ".\javafx-sdk-21.0.2\lib"
       ```
   - (Opcional) Se quiser usar o JDK incluso, aponte o PATH para a pasta `bin` do JDK.
   - Rode o comando:
     ```powershell
     java --module-path "$env:PATH_TO_FX" --add-modules javafx.controls,javafx.fxml,javafx.media -jar project-movie-screening-1.0.jar
     ```

> **Importante:**
> - Sempre execute o comando acima **dentro da pasta dist**.
> - Não tente rodar o .jar clicando duas vezes, pois o JavaFX precisa ser informado manualmente.
> - O arquivo .bat já faz toda a configuração automaticamente para você.

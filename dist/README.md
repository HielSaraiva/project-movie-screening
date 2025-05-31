# Guia Rápido de Execução

Para rodar este projeto corretamente, siga os passos abaixo **dentro desta pasta (dist)**:

1. **Configure o Java (JDK):**
   - Certifique-se de que o JDK incluso está configurado no PATH do sistema ou use o seu próprio JDK 21+.
   - Se quiser usar o JDK incluso, adicione o caminho da pasta `bin` do JDK ao PATH. Exemplo no PowerShell:
     ```powershell
     $env:PATH = "$env:PATH;$(Resolve-Path .\jdk-21.0.2\bin)"
     ```

2. **Configure o JavaFX SDK:**
   - Defina a variável de ambiente `PATH_TO_FX` apontando para a pasta `lib` do JavaFX SDK que está aqui dentro.
   - Exemplo no PowerShell:
     ```powershell
     $env:PATH_TO_FX = ".\javafx-sdk-21.0.2\lib"
     ```

3. **Execute o arquivo .bat:**
   - Agora basta dar dois cliques no arquivo `iniciar-cinema.bat` para configurar o JavaFX e iniciar o sistema automaticamente.

> **Importante:**
> - Sempre execute o .bat **dentro da pasta dist**.
> - Não tente rodar o .jar clicando duas vezes, pois o JavaFX precisa ser informado manualmente.
> - O arquivo .bat já faz toda a configuração automaticamente para você, desde que o Java e o JavaFX estejam corretamente configurados.

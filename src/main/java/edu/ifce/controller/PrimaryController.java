package edu.ifce.controller;

import java.util.ArrayList;
import java.util.List;

import edu.ifce.thread.CinemaThread;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class PrimaryController {
    // --- FXML Components ---
    @FXML
    private TextField capacidadeField;
    @FXML
    private TextField tempoExibicaoField;
    @FXML
    private Button iniciarDemoBtn;
    @FXML
    private TextField fanIdField;
    @FXML
    private TextField fanTempoLancheField;
    @FXML
    private Button adicionarFanBtn;
    @FXML
    private TextArea logArea;
    @FXML
    private Label statusDemonstradorLabel;
    @FXML
    private GridPane fansStatusGrid;
    @FXML
    private Button toggleScrollBtn;
    @FXML
    private Label toggleScrollIcon;

    // --- Control Variables ---
    private boolean demonstradorIniciado = false;
    private final List<CinemaThread.Fan> fans = new ArrayList<>();    private CinemaThread.Demonstrador demonstrador;
    private boolean autoScroll = true;
    private int tempoExibicaoFilme = 0; // tempo de exibição do filme
    // Controle de animação de processamento por fã
    private final java.util.Map<String, Timeline> fanProcessingBlinkers = new java.util.HashMap<>();
    private final java.util.Map<String, Label> fanLabels = new java.util.HashMap<>();
    private final java.util.Map<String, ImageView> fanImageViews = new java.util.HashMap<>();
    private Timeline demonstradorBlinker;    // Controle do contador de tempo regressivo
    private Timeline contadorRegressivo;
    private double tempoRestanteSegundos = 0.0;
    // Controle dos contadores de tempo regressivo dos fãs
    private final java.util.Map<String, Timeline> fanContadores = new java.util.HashMap<>();
    private final java.util.Map<String, Double> fanTemposRestantes = new java.util.HashMap<>();

    // --- Handlers ---
    @FXML
    private void iniciarDemonstrador() {
        try {
            int n = Integer.parseInt(capacidadeField.getText());
            int te = Integer.parseInt(tempoExibicaoField.getText());
            tempoExibicaoFilme = te; // Salva o tempo de exibição
            CinemaThread.configurar(n, te);
            demonstrador = new CinemaThread.Demonstrador();
            demonstrador.setOnStatusChange(this::atualizarStatusDemonstrador);
            demonstrador.setLogger(this::log);
            demonstrador.start();
            demonstradorIniciado = true;
            adicionarFanBtn.setDisable(false);
            log("Demonstrador iniciado com capacidade " + n + " e tempo de exibição " + te + "s.");
            iniciarDemoBtn.setDisable(true);
            atualizarStatusDemonstrador(demonstrador.getStatus());
        } catch (Exception e) {
            log("Erro ao iniciar demonstrador: " + e.getMessage());
        }
    }

    @FXML
    private void adicionarFan() {
        if (!demonstradorIniciado)
            return;
        try {
            String id = fanIdField.getText();
            // Verifica se já existe um fã com esse ID
            boolean idExistente = fans.stream().anyMatch(f -> f.getFanId().equals(id));
            if (idExistente) {
                log("Erro: já existe um fã com o ID '" + id + "'. Escolha outro ID.");
                return;
            }
            int tl = Integer.parseInt(fanTempoLancheField.getText());
            CinemaThread.Fan fan = new CinemaThread.Fan(id, tl);
            fans.add(fan);
            fan.setOnStatusChange(() -> atualizarStatusFans(fans));
            fan.setLogger(this::log);
            fan.start();
            log("Fã " + id + " criado com tempo de lanche " + tl + "s.");
            atualizarStatusFans(fans);
        } catch (Exception e) {
            log("Erro ao criar fã: " + e.getMessage());
        }
    }

    @FXML
    private void toggleScrollMode() {
        autoScroll = !autoScroll;
        if (autoScroll) {
            toggleScrollIcon.setText("⤓"); // ícone para auto-scroll ativado
            toggleScrollBtn.setTooltip(new javafx.scene.control.Tooltip("Rolagem automática ativada"));
        } else {
            toggleScrollIcon.setText("⤓✕"); // ícone alternativo para auto-scroll desativado
            toggleScrollBtn.setTooltip(new javafx.scene.control.Tooltip("Rolagem automática desativada"));        }
    }

    // --- Atualização de Status ---
    
    private void iniciarContadorRegressivo() {
        // Para o contador anterior se existir
        if (contadorRegressivo != null) {
            contadorRegressivo.stop();
        }
        
        // Inicializa o tempo restante
        tempoRestanteSegundos = tempoExibicaoFilme;
        
        // Cria o contador que atualiza a cada 100ms (0.1 segundos)
        contadorRegressivo = new Timeline(
            new KeyFrame(javafx.util.Duration.millis(100), e -> {
                tempoRestanteSegundos -= 0.1;
                if (tempoRestanteSegundos <= 0) {
                    tempoRestanteSegundos = 0;
                    contadorRegressivo.stop();
                }
                atualizarDisplayContador();
            })
        );
        contadorRegressivo.setCycleCount(Timeline.INDEFINITE);
        contadorRegressivo.play();
    }
      private void pararContadorRegressivo() {
        if (contadorRegressivo != null) {
            contadorRegressivo.stop();
            contadorRegressivo = null;
        }
    }
    
    private void iniciarContadorFan(String fanId, int tempoLanche) {
        // Para o contador anterior se existir
        Timeline contadorAnterior = fanContadores.get(fanId);
        if (contadorAnterior != null) {
            contadorAnterior.stop();
        }
        
        // Inicializa o tempo restante
        double tempoRestante = tempoLanche;
        fanTemposRestantes.put(fanId, tempoRestante);
        
        // Cria o contador que atualiza a cada 100ms (0.1 segundos)
        Timeline contador = new Timeline(
            new KeyFrame(javafx.util.Duration.millis(100), e -> {
                double tempoAtual = fanTemposRestantes.get(fanId) - 0.1;
                if (tempoAtual <= 0) {
                    tempoAtual = 0;
                    Timeline cont = fanContadores.get(fanId);
                    if (cont != null) {
                        cont.stop();
                    }
                }
                fanTemposRestantes.put(fanId, tempoAtual);
                atualizarDisplayContadorFan(fanId);
            })
        );
        contador.setCycleCount(Timeline.INDEFINITE);
        contador.play();
        fanContadores.put(fanId, contador);
    }
    
    private void pararContadorFan(String fanId) {
        Timeline contador = fanContadores.get(fanId);
        if (contador != null) {
            contador.stop();
            fanContadores.remove(fanId);
        }
        fanTemposRestantes.remove(fanId);
    }
    
    private void atualizarDisplayContadorFan(String fanId) {
        Platform.runLater(() -> {
            Label label = fanLabels.get(fanId);
            if (label != null) {
                Double tempoRestante = fanTemposRestantes.get(fanId);
                if (tempoRestante != null) {
                    String tempoFormatado = String.format("%.1f", tempoRestante);
                    // Busca o fã para obter informações completas
                    CinemaThread.Fan fan = fans.stream()
                        .filter(f -> f.getFanId().equals(fanId))
                        .findFirst()
                        .orElse(null);
                    if (fan != null) {
                        label.setText(fan.getFanId() + " | tl: " + fan.getTl() + " | " + 
                                    fan.getStatus() + " - Tempo restante: " + tempoFormatado + "s");
                    }
                }
            }
        });
    }
      private void atualizarDisplayContador() {
        Platform.runLater(() -> {
            String tempoFormatado = String.format("%.1f", tempoRestanteSegundos);
            statusDemonstradorLabel.setText("Status do Demonstrador: EXIBINDO_FILME - Tempo restante: " + tempoFormatado + "s");
            // Mantém o estilo visual durante a exibição
            if (!statusDemonstradorLabel.getStyle().contains("-fx-background-color")) {
                statusDemonstradorLabel.setStyle(
                    "-fx-background-color: #4caf50; -fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;"
                );
            }
        });
    }
    
    private void atualizarStatusDemonstrador(CinemaThread.Demonstrador.Status status) {
        Platform.runLater(() -> {
            statusDemonstradorLabel.setText("Status do Demonstrador: " + status);
            if (demonstradorBlinker != null) {
                demonstradorBlinker.stop();                demonstradorBlinker = null;
                statusDemonstradorLabel.setStyle("");
            }
            switch (status) {
                case EXIBINDO_FILME:
                    // Inicia o contador regressivo
                    iniciarContadorRegressivo();
                    
                    statusDemonstradorLabel.setStyle(
                            "-fx-background-color: #4caf50; -fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");
                    // Efeito de piscar em verde durante o tempo de exibição
                    int ciclos = 10;
                    double duracao = tempoExibicaoFilme / (double) ciclos;
                    String corOn = "#00e676"; // verde
                    String corOff = "#4caf50"; // cor de fundo normal
                    demonstradorBlinker = new Timeline();
                    for (int i = 0; i < ciclos * 2; i++) {
                        final boolean isOn = i % 2 == 0;
                        demonstradorBlinker.getKeyFrames().add(
                            new KeyFrame(
                                javafx.util.Duration.seconds(i * duracao / 2),
                                e -> statusDemonstradorLabel.setStyle(
                                    "-fx-background-color: " + (isOn ? corOn : corOff) + "; -fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;"
                            )
                            )
                        );
                    }
                    demonstradorBlinker.getKeyFrames().add(
                        new KeyFrame(
                            javafx.util.Duration.seconds(duracao * ciclos),
                            e -> statusDemonstradorLabel.setStyle(
                                "-fx-background-color: #4caf50; -fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;"
                            )
                        )
                    );
                    demonstradorBlinker.setCycleCount(1);
                    demonstradorBlinker.play();
                    break;
                case AGUARDANDO_LOTACAO:
                    // Para o contador regressivo
                    pararContadorRegressivo();
                    
                    statusDemonstradorLabel.setText("Status do Demonstrador: " + status);
                    statusDemonstradorLabel.setStyle(
                            "-fx-background-color: #ff9800; -fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");
                    break;
            }
        });
    }

    private void atualizarStatusFans(List<CinemaThread.Fan> fans) {
        Platform.runLater(() -> {
            fansStatusGrid.getChildren()
                    .removeIf(node -> GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) > 0);
            fanLabels.clear();
            fanImageViews.clear();
            List<CinemaThread.Fan> naFila = new ArrayList<>();
            List<CinemaThread.Fan> aguardando = new ArrayList<>();
            List<CinemaThread.Fan> assistindo = new ArrayList<>();
            List<CinemaThread.Fan> lanchando = new ArrayList<>();
            for (CinemaThread.Fan fan : fans) {
                switch (fan.getStatus()) {
                    case NA_FILA:
                        naFila.add(fan);
                        break;
                    case AGUARDANDO_INICIO:
                        aguardando.add(fan);
                        break;
                    case ASSISTINDO:
                        assistindo.add(fan);
                        break;
                    case LANCHANDO:
                        lanchando.add(fan);
                        break;
                }
            }
            List<List<CinemaThread.Fan>> colunas = List.of(naFila, aguardando, assistindo, lanchando);
            int maxRows = colunas.stream().mapToInt(List::size).max().orElse(0);
            
            // Carrega as imagens (apenas uma vez)
            Image fanFilaImage = new Image(getClass().getResourceAsStream("/imgs/fila.png"));
            Image aguardandoImage = new Image(getClass().getResourceAsStream("/imgs/cinema1.png"));
            Image assistindoImage = new Image(getClass().getResourceAsStream("/imgs/cinema2.png"));
            Image comendoImage = new Image(getClass().getResourceAsStream("/imgs/comendo2.png"));
            Image comendoBaseImage = new Image(getClass().getResourceAsStream("/imgs/comendo1.png"));
            
            for (int row = 0; row < maxRows; row++) {
                for (int col = 0; col < 4; col++) {
                    if (colunas.get(col).size() > row) {
                        CinemaThread.Fan fan = colunas.get(col).get(row);
                          // Cria o label com o texto do fã
                        Label label;
                        if (fan.getStatus() == CinemaThread.Fan.Status.LANCHANDO) {
                            // Para fãs lanchando, inicia o contador se ainda não existir
                            if (!fanContadores.containsKey(fan.getFanId())) {
                                iniciarContadorFan(fan.getFanId(), fan.getTl());
                            }
                            // Exibe com contador regressivo
                            Double tempoRestante = fanTemposRestantes.get(fan.getFanId());
                            if (tempoRestante != null) {
                                String tempoFormatado = String.format("%.1f", tempoRestante);
                                label = new Label(fan.getFanId() + " | tl: " + fan.getTl() + " | " + 
                                                fan.getStatus() + " - Tempo restante: " + tempoFormatado + "s");
                            } else {
                                label = new Label(fan.getFanId() + " | tl: " + fan.getTl() + " | " + fan.getStatus());
                            }
                        } else {
                            // Para outros status, para o contador se existir
                            pararContadorFan(fan.getFanId());
                            label = new Label(fan.getFanId() + " | tl: " + fan.getTl() + " | " + fan.getStatus());
                        }
                        
                        // Todos os fãs terão uma imagem ao lado do texto
                        HBox fanBox = new HBox(5); // 5px de espaço entre os elementos
                        fanBox.setMaxWidth(Double.MAX_VALUE); // Permite que o HBox ocupe toda a largura disponível
                        
                        // Adiciona o texto
                        fanBox.getChildren().add(label);
                        
                        // Configura o alinhamento do HBox de acordo com a coluna
                        switch (col) {
                            case 0: // NA_FILA - alinhamento à esquerda
                                fanBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                                break;
                            case 1: // AGUARDANDO_INICIO - alinhamento ao centro
                            case 2: // ASSISTINDO - alinhamento ao centro
                                fanBox.setAlignment(javafx.geometry.Pos.CENTER);
                                break;
                            default: // LANCHANDO (col = 3) - alinhamento à direita
                                fanBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
                                break;
                        }
                        
                        // Cria e configura a imagem dependendo do status
                        ImageView imageView;
                        switch (col) {
                            case 0: // NA_FILA
                                imageView = new ImageView(fanFilaImage);
                                break;
                            case 1: // AGUARDANDO_INICIO
                                imageView = new ImageView(aguardandoImage);
                                break;
                            case 2: // ASSISTINDO
                                imageView = new ImageView(assistindoImage);
                                // Guarda a referência da ImageView para poder mudar a imagem durante o blink
                                fanImageViews.put(fan.getFanId(), imageView);
                                break;
                            default: // LANCHANDO (col = 3)
                                imageView = new ImageView(comendoImage);
                                // Guarda a referência da ImageView para poder mudar a imagem durante o blink
                                fanImageViews.put(fan.getFanId(), imageView);
                                break;
                        }
                        
                        imageView.setFitHeight(48);
                        imageView.setFitWidth(48);
                        imageView.setPreserveRatio(true);
                        
                        // Adiciona a imagem depois do texto
                        fanBox.getChildren().add(imageView);
                        
                        // Adiciona o HBox ao GridPane
                        fansStatusGrid.add(fanBox, col, row + 1);
                        
                        fanLabels.put(fan.getFanId(), label);
                    }
                }
            }
            
            // Efeito de processamento: piscar 10 vezes em verde (ASSISTINDO) ou laranja (LANCHANDO)
            for (CinemaThread.Fan fan : fans) {
                Label label = fanLabels.get(fan.getFanId());
                Timeline blinker = fanProcessingBlinkers.get(fan.getFanId());
                boolean processando = fan.getStatus() == CinemaThread.Fan.Status.ASSISTINDO
                        || fan.getStatus() == CinemaThread.Fan.Status.LANCHANDO;
                if (processando && label != null) {
                    // Se já existe um blinker, pare e remova
                    if (blinker != null) {
                        blinker.stop();
                        fanProcessingBlinkers.remove(fan.getFanId());
                    }
                    int ciclos = 10;
                    double duracao;
                    String corOn, corOff;
                    if (fan.getStatus() == CinemaThread.Fan.Status.ASSISTINDO) {
                        duracao = tempoExibicaoFilme / (double) ciclos;
                        corOn = "#00e676"; // verde
                        corOff = "#222";
                    } else {
                        duracao = fan.getTl() / (double) ciclos;
                        corOn = "#ff9800"; // laranja
                        corOff = "#222";
                    }
                    Timeline novoBlinker = new Timeline();
                    for (int i = 0; i < ciclos * 2; i++) {
                        final boolean on = i % 2 == 0;
                        novoBlinker.getKeyFrames().add(
                                new KeyFrame(javafx.util.Duration.seconds(i * duracao / 2),
                                        e -> {
                                            // Altera cor do texto
                                            label.setStyle("-fx-text-fill: " + (on ? corOn : corOff)
                                                    + "; -fx-font-weight: bold;");
                                            
                                            // Se estiver assistindo, alterna a imagem
                                            if (fan.getStatus() == CinemaThread.Fan.Status.ASSISTINDO) {
                                                ImageView imageView = fanImageViews.get(fan.getFanId());
                                                if (imageView != null) {
                                                    if (on) {
                                                        // Texto verde = cinema2.png (assistindo)
                                                        imageView.setImage(assistindoImage);
                                                    } else {
                                                        // Texto preto = cinema1.png (aguardando)
                                                        imageView.setImage(aguardandoImage);
                                                    }
                                                }
                                            }
                                            // Se estiver lanchando, alterna a imagem
                                            else if (fan.getStatus() == CinemaThread.Fan.Status.LANCHANDO) {
                                                ImageView imageView = fanImageViews.get(fan.getFanId());
                                                if (imageView != null) {
                                                    if (on) {
                                                        // Texto laranja = comendo2.png
                                                        imageView.setImage(comendoImage);
                                                    } else {
                                                        // Texto preto = comendo1.png
                                                        imageView.setImage(comendoBaseImage);
                                                    }
                                                }
                                            }
                                        }));
                        novoBlinker.getKeyFrames().add(
                                new KeyFrame(javafx.util.Duration.seconds(i * duracao / 2),
                                        e -> {
                                            // Altera cor do texto
                                            label.setStyle("-fx-text-fill: " + (on ? corOn : corOff)
                                                    + "; -fx-font-weight: bold;");
                                            
                                            // Se estiver assistindo, alterna a imagem
                                            if (fan.getStatus() == CinemaThread.Fan.Status.ASSISTINDO) {
                                                ImageView imageView = fanImageViews.get(fan.getFanId());
                                                if (imageView != null) {
                                                    if (on) {
                                                        // Texto verde = cinema2.png (assistindo)
                                                        imageView.setImage(assistindoImage);
                                                    } else {
                                                        // Texto preto = cinema1.png (aguardando)
                                                        imageView.setImage(aguardandoImage);
                                                    }
                                                }
                                            }
                                        }));
                    }
                    // Ao terminar, volta ao normal
                    novoBlinker.getKeyFrames().add(
                            new KeyFrame(javafx.util.Duration.seconds(duracao * ciclos),
                                    e -> {
                                        label.setStyle("");
                                        // Restaura a imagem padrão conforme o status
                                        ImageView imageView = fanImageViews.get(fan.getFanId());
                                        if (imageView != null) {
                                            if (fan.getStatus() == CinemaThread.Fan.Status.ASSISTINDO) {
                                                imageView.setImage(assistindoImage);
                                            } else if (fan.getStatus() == CinemaThread.Fan.Status.LANCHANDO) {
                                                imageView.setImage(comendoImage);
                                            }
                                        }
                                    }));
                    novoBlinker.setCycleCount(1);
                    novoBlinker.play();
                    fanProcessingBlinkers.put(fan.getFanId(), novoBlinker);
                } else {
                    // Não está processando: remove efeito
                    if (blinker != null) {
                        blinker.stop();
                        fanProcessingBlinkers.remove(fan.getFanId());
                    }
                    if (label != null)
                        label.setStyle("");
                }
            }
        });
    }

    // --- Logger ---
    private void log(String msg) {
        Platform.runLater(() -> {
            int caretPos = logArea.getCaretPosition();
            logArea.appendText(msg + "\n");
            if (autoScroll) {
                logArea.positionCaret(logArea.getText().length());
            } else {
                logArea.positionCaret(caretPos);
            }
        });
    }
}
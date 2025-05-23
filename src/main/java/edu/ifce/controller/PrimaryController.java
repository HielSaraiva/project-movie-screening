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
import javafx.scene.layout.GridPane;

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
    private final List<CinemaThread.Fan> fans = new ArrayList<>();
    private CinemaThread.Demonstrador demonstrador;
    private boolean autoScroll = true;
    private int tempoExibicaoFilme = 0; // tempo de exibição do filme
    // Controle de animação de processamento por fã
    private final java.util.Map<String, Timeline> fanProcessingBlinkers = new java.util.HashMap<>();
    private final java.util.Map<String, Label> fanLabels = new java.util.HashMap<>();
    private Timeline demonstradorBlinker;

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
            toggleScrollBtn.setTooltip(new javafx.scene.control.Tooltip("Rolagem automática desativada"));
        }
    }

    // --- Atualização de Status ---
    private void atualizarStatusDemonstrador(CinemaThread.Demonstrador.Status status) {
        Platform.runLater(() -> {
            statusDemonstradorLabel.setText("Status do Demonstrador: " + status);
            if (demonstradorBlinker != null) {
                demonstradorBlinker.stop();
                demonstradorBlinker = null;
                statusDemonstradorLabel.setStyle("");
            }
            switch (status) {
                case EXIBINDO_FILME:
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
            for (int row = 0; row < maxRows; row++) {
                for (int col = 0; col < 4; col++) {
                    if (colunas.get(col).size() > row) {
                        CinemaThread.Fan fan = colunas.get(col).get(row);
                        Label label = new Label(fan.getFanId() + " | tl: " + fan.getTl() + " | " + fan.getStatus());
                        fansStatusGrid.add(label, col, row + 1);
                        fanLabels.put(fan.getFanId(), label);
                    }
                }
            }
            // Efeito de processamento: piscar 10 vezes em verde (ASSISTINDO) ou laranja
            // (LANCHANDO)
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
                        boolean on = i % 2 == 0;
                        novoBlinker.getKeyFrames().add(
                                new KeyFrame(javafx.util.Duration.seconds(i * duracao / 2),
                                        e -> label.setStyle("-fx-text-fill: " + (on ? corOn : corOff)
                                                + "; -fx-font-weight: bold;")));
                    }
                    // Ao terminar, volta ao normal
                    novoBlinker.getKeyFrames().add(
                            new KeyFrame(javafx.util.Duration.seconds(duracao * ciclos),
                                    e -> label.setStyle("")));
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
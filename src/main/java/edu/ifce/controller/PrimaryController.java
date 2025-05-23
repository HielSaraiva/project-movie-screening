package edu.ifce.controller;

import java.util.ArrayList;
import java.util.List;

import edu.ifce.thread.CinemaThread;
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

    // --- Handlers ---
    @FXML
    private void iniciarDemonstrador() {
        try {
            int n = Integer.parseInt(capacidadeField.getText());
            int te = Integer.parseInt(tempoExibicaoField.getText());
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
            switch (status) {
                case EXIBINDO_FILME:
                    statusDemonstradorLabel.setStyle(
                            "-fx-background-color: #4caf50; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
                    break;
                case AGUARDANDO_LOTACAO:
                    statusDemonstradorLabel.setStyle(
                            "-fx-background-color: #ff9800; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
                    break;
            }
        });
    }

    private void atualizarStatusFans(List<CinemaThread.Fan> fans) {
        Platform.runLater(() -> {
            fansStatusGrid.getChildren()
                    .removeIf(node -> GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) > 0);
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
                    }
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
package edu.ifce.controller;

import edu.ifce.thread.CinemaThread;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class PrimaryController {

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

    private boolean demonstradorIniciado = false;

    private boolean autoScroll = true;
    private double lastScrollPosition = 0;

    @FXML
    private void iniciarDemonstrador() {
        try {
            int n = Integer.parseInt(capacidadeField.getText());
            int te = Integer.parseInt(tempoExibicaoField.getText());
            CinemaThread.configurar(n, te);
            CinemaThread.Demonstrador demonstrador = new CinemaThread.Demonstrador();
            demonstrador.setLogger(this::log);
            demonstrador.start();
            demonstradorIniciado = true;
            adicionarFanBtn.setDisable(false);
            log("Demonstrador iniciado com capacidade " + n + " e tempo de exibição " + te + "s.");
            iniciarDemoBtn.setDisable(true);
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
            fan.setLogger(this::log);
            fan.start();
            log("Fã " + id + " criado com tempo de lanche " + tl + "s.");
        } catch (Exception e) {
            log("Erro ao criar fã: " + e.getMessage());
        }
    }

    private void log(String msg) {
        Platform.runLater(() -> logArea.appendText(msg + "\n"));
    }
}
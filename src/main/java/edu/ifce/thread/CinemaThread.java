package edu.ifce.thread;

import java.util.concurrent.Semaphore;
import java.util.function.Consumer;

import edu.ifce.function.SleepUtil;
import javafx.application.Platform;

public class CinemaThread {
    // --- Variáveis de configuração ---
    public static int N; // capacidade do auditório
    public static int te; // tempo de exibicao

    // --- Semáforos ---
    public static Semaphore entrada; // controla entrada no auditório
    public static Semaphore podeExibir; // sinaliza para o demonstrador
    public static Semaphore podeAssistir; // libera fãs para assistir
    public static Semaphore filmeIniciado; // garante ordem dos prints
    public static Semaphore mutex;

    // --- Estado global ---
    public static int presentes = 0; // fãs presentes no auditório

    // --- Métodos utilitários ---
    public static void configurar(int n, int te) {
        CinemaThread.N = n;
        CinemaThread.te = te;
        entrada = new Semaphore(N);
        podeExibir = new Semaphore(0);
        podeAssistir = new Semaphore(0);
        filmeIniciado = new Semaphore(0);
        mutex = new Semaphore(1);
        presentes = 0;
    }

    // --- Classe Fan ---
    public static class Fan extends Thread {
        private String id;
        private int tl; // tempo de lanche em segundos
        private Consumer<String> logger = System.out::println; // padrão: console
        private Runnable onStatusChange;
        private Status status;

        public enum Status {
            NA_FILA,
            AGUARDANDO_INICIO,
            ASSISTINDO,
            LANCHANDO
        }

        public Fan(String id, int tl) {
            this.id = id;
            this.tl = tl;
            setDaemon(true);
        }

        // --- Getters ---
        public String getFanId() {
            return id;
        }

        public int getTl() {
            return tl;
        }

        public Status getStatus() {
            return status;
        }

        // --- Setters ---
        public void setLogger(java.util.function.Consumer<String> logger) {
            this.logger = logger;
        }

        public void setOnStatusChange(Runnable callback) {
            this.onStatusChange = callback;
        }

        // --- Atualização de status ---
        private void updateStatus(Status newStatus) {
            this.status = newStatus;
            if (onStatusChange != null) {
                Platform.runLater(onStatusChange);
            }
        }

        @Override
        public void run() {
            try {
                while (true) {
                    logger.accept("[" + id + "] Status: NA_FILA - Esperando para entrar no auditorio.");
                    updateStatus(Status.NA_FILA);
                    entrada.acquire();

                    mutex.acquire();
                    try {
                        presentes++;
                        logger.accept("[" + id + "] Status: AGUARDANDO_INICIO - Entrou no auditorio. Presentes: "
                                + presentes);
                        updateStatus(Status.AGUARDANDO_INICIO);
                        if (presentes == N) {
                            podeExibir.release();
                            logger.accept(">>> Auditorio lotado! Iniciando sessao...");
                            podeAssistir.release(N);
                        }
                    } finally {
                        mutex.release();
                    }

                    podeAssistir.acquire();
                    filmeIniciado.acquire(); // espera o demonstrador sinalizar início do filme
                    updateStatus(Status.ASSISTINDO);
                    logger.accept("[" + id + "] Status: ASSISTINDO - Comecou a assistir ao filme");
                    SleepUtil.busySleep(te * 1000);

                    mutex.acquire();
                    try {
                        presentes--;
                        logger.accept("[" + id + "] Saiu do auditorio para lanchar. Presentes restantes: " + presentes);
                        if (presentes == 0) {
                            logger.accept(
                                    ">>> Auditorio esvaziou! Liberando para proxima sessao.\n---------------------------");
                            logger.accept("[DEMONSTRADOR] Status: AGUARDANDO_LOTACAO - Esperando auditorio lotar...");
                            entrada.release(N);
                        }
                    } finally {
                        mutex.release();
                    }

                    logger.accept("[" + id + "] Status: LANCHANDO - Foi lanchar.");
                    updateStatus(Status.LANCHANDO);
                    SleepUtil.busySleep(tl * 1000);
                    logger.accept("[" + id + "] Terminou de lanchar e voltou para a fila.");
                    updateStatus(Status.NA_FILA);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // --- Classe Demonstrador ---
    public static class Demonstrador extends Thread {
        private Consumer<Status> onStatusChange;
        private Consumer<String> logger = System.out::println; // padrão: console
        private Status status;

        public enum Status {
            EXIBINDO_FILME,
            AGUARDANDO_LOTACAO
        }

        public Demonstrador() {
            setDaemon(true);
        }

        // --- Getters/Setters ---
        public Status getStatus() {
            return status;
        }

        public void setOnStatusChange(Consumer<Status> callback) {
            this.onStatusChange = callback;
        }

        public void setLogger(java.util.function.Consumer<String> logger) {
            this.logger = logger;
        }

        // --- Atualização de status ---
        private void updateStatus(Status newStatus) {
            this.status = newStatus;
            if (onStatusChange != null) {
                Platform.runLater(() -> onStatusChange.accept(newStatus));
            }
        }

        @Override
        public void run() {
            try {
                while (true) {
                    updateStatus(Status.AGUARDANDO_LOTACAO);
                    podeExibir.acquire();
                    logger.accept("[DEMONSTRADOR] Status: EXIBINDO_FILME - Exibindo filme!");
                    updateStatus(Status.EXIBINDO_FILME);
                    filmeIniciado.release(N); // libera para todos os fãs
                    SleepUtil.busySleep(te * 1000);
                    logger.accept("[DEMONSTRADOR] Filme acabou! Liberando fas para lanchar.");
                    updateStatus(Status.AGUARDANDO_LOTACAO);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
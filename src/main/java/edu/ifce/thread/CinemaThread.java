package edu.ifce.thread;

import java.util.concurrent.Semaphore;

import edu.ifce.function.SleepUtil;

public class CinemaThread {

    public static int N; // capacidade do auditório
    public static int te; // tempo de exibicao

    public static Semaphore entrada; // controla entrada no auditório
    public static Semaphore podeExibir; // sinaliza para o demonstrador
    public static Semaphore podeAssistir; // libera fãs para assistir
    public static Semaphore filmeIniciado; // garante ordem dos prints
    public static Semaphore mutex;

    public static int presentes = 0; // fãs presentes no auditório

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

    public static class Fan extends Thread {

        String id;
        int tl; // tempo de lanche em segundos

        enum Status {
            NA_FILA,
            AGUARDANDO_INICIO,
            ASSISTINDO,
            LANCHANDO
        }

        Status status;

        private java.util.function.Consumer<String> logger = System.out::println; // padrão: console

        public void setLogger(java.util.function.Consumer<String> logger) {
            this.logger = logger;
        }

        public Fan(String id, int tl) {
            this.id = id;
            this.tl = tl;
            setDaemon(true);
        }

        public void run() {
            try {
                while (true) {
                    logger.accept("[" + id + "] Status: NA_FILA - Esperando para entrar no auditorio.");
                    status = Status.NA_FILA;
                    entrada.acquire();

                    mutex.acquire();
                    try {
                        presentes++;
                        logger.accept("[" + id + "] Status: AGUARDANDO_INICIO - Entrou no auditorio. Presentes: "
                                + presentes);
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
                    status = Status.ASSISTINDO;
                    logger.accept("[" + id + "] Status: ASSISTINDO - Comecou a assistir ao filme");
                    SleepUtil.busySleep(te * 1000);

                    mutex.acquire();
                    try {
                        presentes--;
                        logger.accept(
                                "[" + id + "] Saiu do auditorio para lanchar. Presentes restantes: " + presentes);
                        if (presentes == 0) {
                            logger.accept(
                                    ">>> Auditorio esvaziou! Liberando para proxima sessao.\n---------------------------");
                            logger.accept(
                                    "[DEMONSTRADOR] Status: AGUARDANDO_LOTACAO - Esperando auditorio lotar...");
                            entrada.release(N);
                        }
                    } finally {
                        mutex.release();
                    }

                    logger.accept("[" + id + "] Status: LANCHANDO - Foi lanchar.");
                    status = Status.LANCHANDO;
                    SleepUtil.busySleep(tl * 1000);
                    logger.accept("[" + id + "] Terminou de lanchar e voltou para a fila.");
                    status = Status.NA_FILA;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class Demonstrador extends Thread {

        // int capacidade; // N: capacidade do auditório
        // int te; // Te: tempo de exibição do filme em segundos
        enum Status {
            EXIBINDO_FILME,
            AGUARDANDO_LOTACAO
        }

        Status status;

        public Demonstrador() {
            // this.capacidade = capacidade;
            // this.te = te;
            setDaemon(true);
        }

        private java.util.function.Consumer<String> logger = System.out::println; // padrão: console

        public void setLogger(java.util.function.Consumer<String> logger) {
            this.logger = logger;
        }

        public void run() {
            try {
                while (true) {
                    status = Status.AGUARDANDO_LOTACAO;
                    podeExibir.acquire();
                    logger.accept("[DEMONSTRADOR] Status: EXIBINDO_FILME - Exibindo filme!");
                    status = Status.EXIBINDO_FILME;
                    filmeIniciado.release(N); // libera para todos os fãs
                    SleepUtil.busySleep(te * 1000);
                    logger.accept("[DEMONSTRADOR] Filme acabou! Liberando fas para lanchar.");
                    status = Status.AGUARDANDO_LOTACAO;

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // public static void main(String[] args) {
    // Demonstrador demonstrador = new Demonstrador();
    // demonstrador.start();

    // new Fan("Fan-" + 1, 1).start();
    // new Fan("Fan-" + 2, 2).start();
    // // new Fan("Fan-" + 3, 3).start();
    // // new Fan("Fan-" + 4, 4).start();
    // // new Fan("Fan-" + 5, 5).start();
    // // new Fan("Fan-" + 6, 6).start();
    // // new Fan("Fan-" + 7, 7).start();
    // // new Fan("Fan-" + 8, 8).start();
    // // new Fan("Fan-" + 9, 9).start();
    // // new Fan("Fan-" + 10, 10).start();
    // // new Fan("Fan-" + 11, 11).start();
    // // new Fan("Fan-" + 12, 12).start();
    // // new Fan("Fan-" + 13, 13).start();
    // // new Fan("Fan-" + 14, 14).start();
    // // new Fan("Fan-" + 15, 15).start();
    // // new Fan("Fan-" + 16, 16).start();
    // // new Fan("Fan-" + 17, 17).start();
    // // new Fan("Fan-" + 18, 18).start();
    // // new Fan("Fan-" + 19, 19).start();
    // }
    // }
}
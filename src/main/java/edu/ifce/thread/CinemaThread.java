package edu.ifce.thread;

import java.util.concurrent.Semaphore;

public class CinemaThread {

    static final int N = 3; // capacidade do auditório

    static Semaphore entrada = new Semaphore(N); // controla entrada no auditório
    static Semaphore podeExibir = new Semaphore(0);    // sinaliza para o demonstrador
    static Semaphore podeLanchar = new Semaphore(0);   // libera fãs para lanchar

    static int presentes = 0; // fãs presentes no auditório

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

        public Fan(String id, int tl) {
            this.id = id;
            this.tl = tl;
        }

        public void run() {
            try {
                while (true) {
                    System.out.println("[" + id + "] Status: NA_FILA - Esperando para entrar no auditorio.");
                    status = Status.NA_FILA;
                    entrada.acquire();

                    synchronized (CinemaThread.class) {
                        presentes++;
                        System.out.println("[" + id + "] Status: AGUARDANDO_INICIO - Entrou no auditorio. Presentes: " + presentes);
                        if (presentes == N) {
                            System.out.println(">>> Auditorio lotado! Iniciando sessao...");
                            status = Status.ASSISTINDO;
                            podeExibir.release();
                        }
                    }

                    podeLanchar.acquire();

                    synchronized (CinemaThread.class) {
                        presentes--;
                        System.out.println("[" + id + "] Saiu do auditorio para lanchar. Presentes restantes: " + presentes);
                        if (presentes == 0) {
                            System.out.println(">>> Auditorio esvaziou! Liberando para proxima sessao.\n---------------------------");
                            System.out.println("[DEMONSTRADOR] Status: AGUARDANDO_LOTACAO - Esperando auditorio lotar...");
                            entrada.release(N);
                        }
                    }

                    System.out.println("[" + id + "] Status: LANCHANDO - Foi lanchar.");
                    status = Status.LANCHANDO;
                    Thread.sleep(tl * 1000);
                    System.out.println("[" + id + "] Terminou de lanchar e voltou para a fila.");
                    status = Status.NA_FILA;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class Demonstrador extends Thread {

        // int capacidade; // N: capacidade do auditório
        int te; // Te: tempo de exibição do filme em segundos

        enum Status {
            EXIBINDO_FILME,
            AGUARDANDO_LOTACAO
        }

        Status status;

        public Demonstrador(int te) {
            // this.capacidade = capacidade;
            this.te = te;
        }

        public void run() {
            try {
                while (true) {
                    status = Status.AGUARDANDO_LOTACAO;
                    podeExibir.acquire();
                    status = Status.EXIBINDO_FILME;
                    System.out.println("[DEMONSTRADOR] Status: EXIBINDO_FILME - Exibindo filme!");
                    Thread.sleep(te * 1000);
                    System.out.println("[DEMONSTRADOR] Filme acabou! Liberando fas para lanchar.");
                    status = Status.AGUARDANDO_LOTACAO;
                    podeLanchar.release(N);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Demonstrador demonstrador = new Demonstrador(3); // tempo de exibição = 3s
        demonstrador.start();

        new Fan("Fan-" + 1, 1).start();
        new Fan("Fan-" + 2, 2).start();
        new Fan("Fan-" + 3, 3).start();
        new Fan("Fan-" + 4, 4).start();
        new Fan("Fan-" + 5, 5).start();
        new Fan("Fan-" + 6, 6).start();
        new Fan("Fan-" + 7, 7).start();
        new Fan("Fan-" + 8, 8).start();
        // new Fan("Fan-" + 9, 9).start();
        // new Fan("Fan-" + 10, 10).start();
        // new Fan("Fan-" + 11, 11).start();
        // new Fan("Fan-" + 12, 12).start();
        // new Fan("Fan-" + 13, 13).start();
        // new Fan("Fan-" + 14, 14).start();
        // new Fan("Fan-" + 15, 15).start();
        // new Fan("Fan-" + 16, 16).start();
        // new Fan("Fan-" + 17, 17).start();
        // new Fan("Fan-" + 18, 18).start();
        // new Fan("Fan-" + 19, 19).start();
    }
}

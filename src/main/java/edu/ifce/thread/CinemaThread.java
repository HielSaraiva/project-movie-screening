package edu.ifce.thread;

import java.util.concurrent.Semaphore;

public class CinemaThread {

    static final int N = 3; // capacidade do auditório
    static final int NUM_FANS = 6; // total de fãs para simulação

    static Semaphore entrada = new Semaphore(N, true); // controla entrada no auditório
    static Semaphore podeExibir = new Semaphore(0);    // sinaliza para o demonstrador
    static Semaphore podeLanchar = new Semaphore(0);   // libera fãs para lanchar

    static int presentes = 0; // fãs presentes no auditório

    public static class Fan extends Thread {

        String id;
        int tl; // tempo de lanche em segundos

        enum Status {
            NA_FILA,
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
                    entrada.acquire(); // tenta entrar no auditório

                    synchronized (CinemaThread.class) {
                        presentes++;
                        System.out.println(id + " entrou no auditorio. Presentes: " + presentes);
                        if (presentes == N) {
                            podeExibir.release(); // avisa o demonstrador que pode exibir
                        }
                    }

                    podeLanchar.acquire(); // espera o filme acabar para lanchar

                    System.out.println(id + " foi lanchar.");
                    Thread.sleep(tl * 1000); // simula tempo de lanche
                    System.out.println(id + " terminou de lanchar e voltou pra fila do cinema.");

                    synchronized (CinemaThread.class) {
                        presentes--;
                        if (presentes == 0) {
                            // libera o auditório para próxima sessão
                            for (int i = 0; i < N; i++) {
                                entrada.release();
                            }
                        }
                    }
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
                    podeExibir.acquire(); // espera o auditório encher
                    System.out.println("Demonstrador: Exibindo filme...");
                    Thread.sleep(te * 1000); // simula exibição do filme
                    System.out.println("Demonstrador: Filme acabou!");

                    // libera todos os fãs para lanchar
                    for (int i = 0; i < N; i++) {
                        podeLanchar.release();
                    }
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
    }
}

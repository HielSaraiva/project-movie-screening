package edu.ifce.function;

public class SleepUtil {
    public static void busySleep(long millis) {
        long end = System.currentTimeMillis() + millis;
        while (System.currentTimeMillis() < end) {
            // Ocupa a CPU ativamente
        }
    }
}

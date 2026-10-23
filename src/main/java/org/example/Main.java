package org.example;

import org.example.facade.KafkaAnalysisFacade;

// Точка входа в программу.
public class Main {
    public static void main(String[] args) {
        KafkaAnalysisFacade facade = new KafkaAnalysisFacade();
        int exitCode = facade.run(args);

        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }
}

package com.company.Template;

public class Main {
    public static void main(String[] args) {
        // Пример: ρ = 0.5, т.е. система стабильна
        double meanTau = 2.0;       // Среднее межприходное время
        double meanSigma = 1.0;     // Среднее время обслуживания
        int bufferSize = 5;
        int K = 1000;

        Simulation simulation = new Simulation(bufferSize, K, meanTau, meanSigma);
        simulation.run();
    }
}



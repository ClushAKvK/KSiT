package com.company.DIPrincess;

public class Main {
    public static void main(String[] args) {
        // Пример: ρ = 0.5, т.е. система стабильна
        double meanTau = 1.5;       // Среднее межприходное время
        double meanSigma = 1.2;     // Среднее время обслуживания
        int bufferSize = 15;
        double T = 100.0;
        final double R_a = 0.1;
        final double R_b = 5.2;
        int Ln = 3;
        double Aout = 0.3;


        Simulation simulation = new Simulation(bufferSize, T, meanTau, meanSigma, R_a, R_b, Ln, Aout);
       // simulation.run();
    }
}



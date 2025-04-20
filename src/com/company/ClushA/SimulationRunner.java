package com.company.ClushA;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;

public class SimulationRunner {
    public static void main(String[] args) {
        final int experiments = 1000;
        final int bufferSize = 8;
        final int tasksPerExperiment = 100;

        final double meanTau = 2.0;
        final double meanSigma = 1.5;

        double totalBusyTime = 0.0;
        double totalTime = 0.0;
        double totalDelay = 0.0;
        int totalTasks = 0;

        int totalArrivals = 0;
        int totalDenails = 0;

        // Подготовка данных для графиков
        XYSeries wSeries = new XYSeries("W(x) - Среднее время пребывания");
        XYSeries pBusySeries = new XYSeries("P_busy(x) - Загрузка");
        XYSeries aSeries = new XYSeries("A(x) - Интенсивность поступления");
        XYSeries cdfSeries = new XYSeries("CDF W(x) - Вероятность ожидания ≤ x");

        for (int i = 0; i < experiments; i++) {
            Simulation sim = new Simulation(bufferSize, tasksPerExperiment, meanTau, meanSigma);
            SimulationResult result = sim.runAndCollect();

            totalBusyTime += result.busyTime;
            totalTime += result.totalTime;
            totalDelay += result.totalDelay;
            totalTasks += result.tasksProcessed;
            totalDenails += result.denials;
            totalArrivals += result.totalArrivals;

            // График W(x) — среднее время пребывания в системе
            double w = result.totalDelay / result.tasksProcessed;
            wSeries.add(i, w);

            // График P_busy(x) — загрузка сервер
            // double pBusy = result.busyTime / result.totalTime;
            // pBusySeries.add(i, pBusy);

            pBusySeries.add(i, result.rejectionProbability);

            // График A(x) — интенсивность поступления
            double a = result.tasksProcessed / result.totalTime;
            aSeries.add(i, a);

            // CDF по времени ожидания
            double[] cdf = result.waitTimeCDF;
            for (int j = 0; j < cdf.length; j++) {
                double x = (j + 1) * (result.totalDelay / result.tasksProcessed);
                cdfSeries.add(x, cdf[j]);
            }
        }

        double avgW = totalDelay / totalTasks;
        //double avgPBusy = totalBusyTime / totalTime;
        double avgPdeni = (double) totalDenails / totalArrivals;

        System.out.printf("После %d экспериментов:\n", experiments);
        System.out.printf("W(x) — Среднее время пребывания: %.4f\n", avgW);
        //System.out.printf("P_busy(x) — Доля занятости сервера: %.4f\n", avgPBusy);
        System.out.printf("P_deni - вероятность отказа заданию в обработке: %.4f\n", avgPdeni);

        // Создание и отображение каждого графика в отдельном окне
        createAndShowChartWindow(wSeries, "W(x) - Среднее время в системе", "Эксперимент", "W");
        //createAndShowChartWindow(pBusySeries, "P_busy(x) - Загрузка", "Эксперимент", "P_busy");
        createAndShowChartWindow(pBusySeries, "P_deni - Загрузка", "Эксперимент", "P_deni");
        createAndShowChartWindow(aSeries, "A(x) - Интенсивность поступления", "Эксперимент", "A");
        createAndShowChartWindow(cdfSeries, "W(x) - CDF ожидания", "Время", "P(w ≤ x)");
    }

    private static void createAndShowChartWindow(XYSeries series, String title, String xLabel, String yLabel) {
        JFreeChart chart = createChart(series, title, xLabel, yLabel);
        ChartPanel chartPanel = new ChartPanel(chart);

        // Создаем отдельное окно для каждого графика
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(chartPanel);
        frame.pack();
        frame.setVisible(true);
    }

    private static JFreeChart createChart(XYSeries series, String title, String xLabel, String yLabel) {
        XYSeriesCollection dataset = new XYSeriesCollection(series);
        return ChartFactory.createXYLineChart(
                title,
                xLabel,
                yLabel,
                dataset,
                PlotOrientation.VERTICAL,
                true, // показывать легенду
                true, // показывать информацию о графике
                false // не показывать панель инструментов
        );
    }
}


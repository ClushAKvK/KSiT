package com.company.DIPrincess;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class GraphBuilder {
    public static void plotL_n(double[] P_Ln) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (int n = 0; n < P_Ln.length; n++) {
            dataset.addValue(P_Ln[n], "P(Ln)", String.valueOf(n + 1));
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "L(n) — Распределение вероятностей длины очереди",
                "n", "P(Ln)", dataset, PlotOrientation.VERTICAL, true, false, false);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));
        javax.swing.JFrame frame = new javax.swing.JFrame();
        frame.setContentPane(chartPanel);
        frame.pack();
        frame.setVisible(true);
    }

    public static void plotAout(double[] P_Aout) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (int i = 0; i < P_Aout.length; i++) {
            dataset.addValue(P_Aout[i], "P(Aout)", String.valueOf(i));
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "Aout(x) — Функция распределения вероятностей длительностей интервалов",
                "x", "P(Aout)", dataset, PlotOrientation.VERTICAL, true, false, false);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));
        javax.swing.JFrame frame = new javax.swing.JFrame();
        frame.setContentPane(chartPanel);
        frame.pack();
        frame.setVisible(true);
    }

    // Метод для построения графика для A(x)
    public static void plotA_x(double[] A_x) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (int i = 0; i < A_x.length; i++) {

            dataset.addValue(A_x[i] + 0.1*i, "Вероятность", "A(x) = " + i);
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "A(x) - Функция распределения интервалов",
                "Интервал",
                "Вероятность",
                dataset, PlotOrientation.VERTICAL, true, false, false
        );

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));
        javax.swing.JFrame frame = new javax.swing.JFrame();
        frame.setContentPane(chartPanel);
        frame.pack();
        frame.setVisible(true);
    }

    // Метод для построения графика для W(x)
    public static void plotW_x(double[] W_x) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (int i = 0; i < W_x.length; i++) {
            dataset.addValue(W_x[i] + 0.2*i, "Вероятность", "W(x) = " + i);
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "W(x) - Функция распределения времени пребывания в системе",
                "Время задержки",
                "Вероятность",
                dataset, PlotOrientation.VERTICAL, true, false, false
        );

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));
        javax.swing.JFrame frame = new javax.swing.JFrame();
        frame.setContentPane(chartPanel);
        frame.pack();
        frame.setVisible(true);
    }
}

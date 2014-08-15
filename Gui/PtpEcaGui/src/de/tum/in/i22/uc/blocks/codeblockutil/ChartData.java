package de.tum.in.i22.uc.blocks.codeblockutil;

import org.jfree.chart.JFreeChart;

public abstract class ChartData {

    public final String title;

    public abstract JFreeChart makeChart();

    public ChartData(String title) {
        this.title = title;
    }
}

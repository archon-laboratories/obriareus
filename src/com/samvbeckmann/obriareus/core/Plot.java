package com.samvbeckmann.obriareus.core;

import com.panayotis.gnuplot.JavaPlot;
import com.panayotis.gnuplot.plot.AbstractPlot;
import com.panayotis.gnuplot.plot.DataSetPlot;
import com.panayotis.gnuplot.style.PlotStyle;
import com.panayotis.gnuplot.style.Style;
import com.panayotis.gnuplot.terminal.PostscriptTerminal;

import java.util.ArrayList;
import java.util.List;

/**
 * Mucking about with JavaPlot in attempting to get a reasonable plot
 */
public class Plot
{
    private boolean normalized;
    private Dataset dataset;
    private IDistribution distribution;
    private List<double[][]> data;
    private int counter;

    public Plot(Dataset dataset, boolean normalized, IDistribution distribution)
    {
        this.dataset = dataset;
        this.normalized = normalized;
        this.distribution = distribution;
        counter = 0;
        data = new ArrayList<double[][]>();
        for (int i = 0; i < dataset.getAlgorithms().size(); i++)
        {
            data.add(new double[dataset.getBudgets().size()][2]);
        }
    }

    /**
     * Adds algorithm performance for a certain budget to the data to be used in this plot.
     *
     * @param means  Array of means of returns for this algorithm at the given budget
     * @param budget Budget that these means are for.
     */
    public void addToDataset(double[] means, int budget)
    {
        try
        {
            for (int i = 0; i < means.length; i++)
            {
                data.get(i)[counter][0] = budget;
                data.get(i)[counter][1] = means[i];
            }
            counter++;
        } catch (ArrayIndexOutOfBoundsException e)
        {
            System.err.print("Encountered a problem trying to add plotting data: " + e + "\n");
        }
    }

    public JavaPlot generatePlot()
    {
        /* Initial Arguments */
        JavaPlot jPlot = new JavaPlot();
        String norm = normalized ? "Normalized" : "Absolute";
        PostscriptTerminal epsf = new PostscriptTerminal(String.format("output/graphs/%s_%s_%s.eps", dataset.toString(),
                distribution.getName(), norm));
        jPlot.setTerminal(epsf);
        jPlot.setTitle(String.format("%s %s %s", dataset.toString(), distribution.getName(), norm));
        jPlot.getAxis("x").setLabel("Budget");
        jPlot.getAxis("y").setLabel((norm) + " Performance");
        jPlot.setKey(JavaPlot.Key.OUTSIDE);

        /* Add the data as plots */
        for (int i = 0; i < data.size(); i++)
        {
            double[][] set = data.get(i);
            DataSetPlot s = new DataSetPlot(set);
            s.setTitle(dataset.getAlgorithms().get(i).getAlgorithm());
            jPlot.addPlot(s);
        }

        /* Set plot styles */
        for (int i = 0; i < data.size(); i++)
        {
            PlotStyle stl = ((AbstractPlot) jPlot.getPlots().get(i)).getPlotStyle();
            stl.setStyle(Style.LINESPOINTS);
        }

        /* Plot */
        jPlot.plot();
        return jPlot;
    }
}

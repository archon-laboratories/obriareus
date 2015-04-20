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
        PostscriptTerminal epsf = new PostscriptTerminal(String.format("output/graphs/%s_%s_%s.pdf", dataset.toString(),
                distribution.getName(), norm));
//        epsf.set("size", "8in, 4in");
        jPlot.setTerminal(epsf);
//        jPlot.set("term", "eps size 1000, 400");
        jPlot.setTitle(String.format("%s %s %s", dataset.toString(), distribution.getName(), norm));
        jPlot.getAxis("x").setLabel("Budget");
        jPlot.getAxis("y").setLabel((norm) + " Performance");
        jPlot.setKey(JavaPlot.Key.TOP_LEFT);
//        jPlot.set("view","equals xyz");
//        jPlot.set("view", ",2,");
//        jPlot.set("size", "square");
//        jPlot.set("ratio", "1");

        /* Test Code, to remove */
//        double[][] set = data.get(0);
//        DataSetPlot s = new DataSetPlot(set);
//        jPlot.addPlot(s);
//
//        PlotStyle stl = ((AbstractPlot) jPlot.getPlots().get(0)).getPlotStyle();
//        stl.setStyle(Style.LINESPOINTS);
        /* end test code */

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

        jPlot.newGraph();

//        stl.setLineType(NamedPlotColor.GOLDENROD);
//        stl.setPointType(5);
//        stl.setPointSize(8);


//        p.addPlot("sin(x)");

//        p.newGraph3D();
//        double[][] plot3d = {{1, 1.1, 3}, {2, 2.2, 3}, {3, 3.3, 3.4}, {4, 4.3, 5}};
//        p.addPlot(plot3d);

//        p.newGraph3D();
//        p.addPlot("sin(x)*sin(y)");

//        p.setMultiTitle("Global test title");
//        StripeLayout lo = new StripeLayout();
//        lo.setColumns(9999);
//        p.getPage().setLayout(lo);

        jPlot.plot();

        return jPlot;
    }
}

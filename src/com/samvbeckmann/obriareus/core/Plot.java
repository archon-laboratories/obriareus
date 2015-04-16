package com.samvbeckmann.obriareus.core;

import com.panayotis.gnuplot.JavaPlot;
import com.panayotis.gnuplot.plot.AbstractPlot;
import com.panayotis.gnuplot.plot.DataSetPlot;
import com.panayotis.gnuplot.style.PlotStyle;
import com.panayotis.gnuplot.style.Style;

/**
 * Mucking about with JavaPlot in attempting to get a reasonable plot
 */
public class Plot
{
    private JavaPlot jPlot;
    private boolean normalized;
    private double[][] data;
    private int counter;

    public Plot(int numBudgets, boolean normalized)
    {
        this.normalized = normalized;
        counter = 0;
        data = new double[numBudgets][];
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
            data[counter][0] = budget;
            for (int i = 0; 1 < means.length; i++)
            {
                data[counter][i + 1] = means[i];
            }
            counter++;
        } catch (ArrayIndexOutOfBoundsException e)
        {
            System.err.print("Encounter a problem trying to add plotting data: " + e);
        }
    }

    public JavaPlot generatePlot()
    {
        jPlot = new JavaPlot();
        jPlot.setTitle("Testing Output");
        jPlot.getAxis("x").setLabel("Budget", "Arial", 20);
        jPlot.getAxis("y").setLabel(normalized ? "Normalized" : "Absolute" + " Performance");

//        jPlot.getAxis("x").setBoundaries(-30, 20);
        jPlot.setKey(JavaPlot.Key.OUTSIDE);


//        double[][] plot = {{1, 1.1}, {2, 2.2}, {3, 3.3}, {4, 4.3}}; // whats this do
        DataSetPlot s = new DataSetPlot(data);
        jPlot.addPlot(s);
//        jPlot.addPlot("besj0(x)*0.12e1");
        PlotStyle stl = ((AbstractPlot) jPlot.getPlots().get(1)).getPlotStyle();
        stl.setStyle(Style.LINESPOINTS);
//        stl.setLineType(NamedPlotColor.GOLDENROD);
//        stl.setPointType(5);
//        stl.setPointSize(8);
//        jPlot.addPlot("sin(x)");

        jPlot.newGraph();
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

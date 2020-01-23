package org.xper.sach.testing;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.StatisticalBarRenderer;
import org.jfree.data.statistics.DefaultStatisticalCategoryDataset;
import org.jfree.data.statistics.StatisticalCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;


public class BarErrorTest extends ApplicationFrame {

    /**
     * Creates a new demo.
     *
     * @param title  the frame title.
     */
    public BarErrorTest(final String title) {

        super(title);
        Font axislabelfont = new Font("Helvetica", Font.PLAIN, 11);
        Font ticklabelfont = new Font("Helvetica", Font.PLAIN, 10);
        final StatisticalCategoryDataset dataset = createDataset();

        final CategoryAxis xAxis = new CategoryAxis("category");
        xAxis.setLowerMargin(0.01d); // percentage of space before first bar
        xAxis.setUpperMargin(0.01d); // percentage of space after last bar
        xAxis.setCategoryMargin(0.0d); // percentage of space between categories
        final ValueAxis yAxis = new NumberAxis("response");
        
        xAxis.setLabelFont(axislabelfont);
        xAxis.setTickLabelPaint(new Color(0,0,0,255/2));
        xAxis.setTickLabelFont(ticklabelfont);
        
        yAxis.setLabelFont(axislabelfont);
        yAxis.setTickLabelPaint(new Color(0,0,0,255/2));
        yAxis.setTickLabelFont(ticklabelfont);
        
        // define the plot
        final StatisticalBarRenderer renderer = new StatisticalBarRenderer();
        renderer.setSeriesVisibleInLegend(0,false);
        renderer.setSeriesPaint(0, new Color(0,0,255,255/3));
        renderer.setSeriesOutlinePaint(0, new Color(0,0,255,255));
        renderer.setSeriesOutlineStroke(0,new BasicStroke(0.5f));
        renderer.setDrawBarOutline(true);
        
        final CategoryPlot plot = new CategoryPlot(dataset, xAxis, yAxis, renderer);

        plot.setOutlineVisible(false);
        plot.setRangeGridlinesVisible(true);
        plot.setDomainGridlinesVisible(true);
        
        final JFreeChart chart = new JFreeChart("",
                                          new Font("Helvetica", Font.BOLD, 14),
                                          plot,
                                          true);
        
        chart.setBackgroundPaint(Color.white);
        chart.setPadding(new RectangleInsets(25d,0d,0d,25d));
        
        // add the chart to a panel...
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(400,300));
        setContentPane(chartPanel);

    }
    
    /**
     * Creates a sample dataset.
     *
     * @return The dataset.
     */
    private StatisticalCategoryDataset createDataset() {

        final DefaultStatisticalCategoryDataset result = new DefaultStatisticalCategoryDataset();

        // {10, 20, 5, 0, 15, 30, 10, 25}
        // {4,6,3,3,2,4,1,4}
        String s1 = "";
        
        result.add(10, 4, s1, "0");
        result.add(20, 6, s1, "1");
        result.add(5,  3, s1, "2");
        result.add(0,  3, s1, "3");
        result.add(15, 2, s1, "4");
        result.add(30, 4, s1, "5");
        result.add(10, 1, s1, "6");
        result.add(25, 4, s1, "7");

//        result.add(22.9,  7.9, "Series 2", "Type 1");
//        result.add(21.8, 18.4, "Series 2", "Type 2");
//        result.add(19.3, 12.4, "Series 2", "Type 3");
//        result.add(30.3, 20.7, "Series 2", "Type 4");
//
//        result.add(12.5, 10.9, "Series 3", "Type 1");
//        result.add(24.8,  7.4, "Series 3", "Type 2");
//        result.add(19.3, 13.4, "Series 3", "Type 3");
//        result.add(17.1, 10.6, "Series 3", "Type 4");

        return result;

    }

    /**
     * For testing from the command line.
     *
     * @param args  ignored.
     */
    public static void main(final String[] args) {

        final BarErrorTest demo = new BarErrorTest(
            "Tuning for categorical stimuli"
        );
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}
package org.xper.sach.analysis;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.util.Arrays;
import java.util.List;

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
import org.xper.sach.util.CreateDbDataSource;
import org.xper.sach.util.SachDbUtil;
import org.xper.sach.util.SachMathUtil;

/**
 * This plots firing rates for the standard behavioral categories (as mean, stdev, category) 
 * in a bar plot with error bars.
 * 
 * @author sach2
 *
 */

public class BehCatPrefPlot extends ApplicationFrame {

	private static final long serialVersionUID = 1L;

	StatisticalCategoryDataset dataset;
	static SachDbUtil dbUtil;
	
	/**
     * Creates a new plot from the dataset.
     *
     * @param title  the frame title.
     */
    public BehCatPrefPlot(final String title, final StatisticalCategoryDataset dataset) {
        super(title);
        this.setDataset(dataset);
        createBehCatPrefPlot();
    }
    
    public BehCatPrefPlot(final String title, final double[][] data) {
        super(title);
        setDatasetFromData(data);        
        createBehCatPrefPlot();
    }
    
    private void createBehCatPrefPlot() {

        Font axislabelfont = new Font("Helvetica", Font.PLAIN, 11);
        Font ticklabelfont = new Font("Helvetica", Font.PLAIN, 10);
//        final StatisticalCategoryDataset dataset = createDataset();

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
    private static StatisticalCategoryDataset createDataset() {

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
    
    private void setDatasetFromData(double[][] data) {
    	// expect data as double[][] {{mean0,stdev0},{mean1,stdev1},...}
    	// should be all 8 categories!
    	
    	final DefaultStatisticalCategoryDataset result = new DefaultStatisticalCategoryDataset();
    	    	
    	for (int n=0;n<data.length;n++) {
    		double[] d = data[n];
    		
    		result.add(d[0],d[1],"",String.valueOf(n));
    	}
    	
    	this.setDataset(result);
    }
    
    private static double[][] getDataFromGenId(long genId) {
    	
    	List<Long> stimObjIds = dbUtil.readStimObjIdsFromGenId(genId);
//    	List<SachStimDataEntry> stimData = dbUtil.readStimObjData(stimObjIds);
    	List<SachStimDataEntry> stimData = dbUtil.readListStimData(stimObjIds);
    	
		int len = stimData.size();
		double[][] spikeData = new double[len][2];
		for (int n=0;n<len;n++) {
			SachStimDataEntry d = stimData.get(n);
			spikeData[n][0] = d.getAvgFR();
			spikeData[n][1] = d.getStdFR();
		}
		
    	return spikeData;
    }

    /**
     * For testing from the command line.
     *
     * @param args  ignored.
     */
    public static void main(final String[] args) {

//    	String title = "Tuning for categorical stimuli";
//    	
//    	double[][] data = {{40,10},{80,7},{20,5},{55,8},{90,7}};
//    	
////        final BehCatPrefPlot demo = new BehCatPrefPlot(title,createDataset());
//        final BehCatPrefPlot demo = new BehCatPrefPlot(title,data);
//        demo.pack();
//        RefineryUtilities.centerFrameOnScreen(demo);
//        demo.setVisible(true);
        
        
//        int len = 8;
//		double[][] spikeData = new double[len][2];
//		for (int n=0;n<len;n++) {
//			//SachStimDataEntry d = stimData.get(n);
//			spikeData[n][0] = SachMathUtil.randRange(100, 0);
//			spikeData[n][1] = SachMathUtil.randRange(30, 0);
//		}
		
    	setDbUtil();
		double[][] spikeData = getDataFromGenId(479);
    	
			// plot
		String title = "Tuning for categorical stimuli";
//    	double[][] data = {{40,10},{80,7},{20,5},{55,8},{90,7}};
//    	final BehCatPrefPlot demo = new BehCatPrefPlot(title,data);
    	final BehCatPrefPlot demo = new BehCatPrefPlot(title,spikeData);
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
        
        System.out.println(Arrays.deepToString(spikeData));

    }

	public StatisticalCategoryDataset getDataset() {
		return dataset;
	}

	public void setDataset(StatisticalCategoryDataset dataset) {
		this.dataset = dataset;
	}
	
	public SachDbUtil getDbUtil() {
		return dbUtil;
	}

	public void setDbUtil(SachDbUtil dbUtil) {
		this.dbUtil = dbUtil;
	}
	
	public static SachDbUtil setDbUtil() {
    	CreateDbDataSource dataSourceMaker = new CreateDbDataSource();
    	return new SachDbUtil(dataSourceMaker.getDataSource());
	}

}
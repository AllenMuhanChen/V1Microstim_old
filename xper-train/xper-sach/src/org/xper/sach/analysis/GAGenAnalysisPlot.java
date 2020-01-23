package org.xper.sach.analysis;


import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.renderer.category.StatisticalBarRenderer;
import org.jfree.data.statistics.DefaultStatisticalCategoryDataset;
import org.jfree.data.statistics.StatisticalCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;
import org.xper.sach.drawing.stimuli.BsplineObjectSpec;
import org.xper.sach.util.CreateDbDataSource;
import org.xper.sach.util.SachDbUtil;
import org.xper.sach.util.SachMathUtil;

/**
 * Plots firing rates and error (stdev) for the stimuli in a GA generation in a bar plot.
 * 
 * @author sach2
 *
 */

public class GAGenAnalysisPlot extends ApplicationFrame {

	private static final long serialVersionUID = 1L;

	SachDbUtil dbUtil;
	StatisticalCategoryDataset dataset;
	List<byte[]> imageset;
	double[][] spikeData = null;
	double[] bkgdData = new double[2];
	
	CategoryPlot plot;
	JList list;
	
	int width = 836;
	
	/**
     * Creates a new plot from the dataset.
     *
     * @param title  the frame title.
     */
    public GAGenAnalysisPlot(final String title, final StatisticalCategoryDataset dataset) {
        super(title);
        this.setDataset(dataset);
        createBehCatPrefPlot();
    }
    
    public GAGenAnalysisPlot(final String title, final double[][] data) {
        super(title);
        setDatasetFromData(data);        
        createBehCatPrefPlot();
    }
    
    public GAGenAnalysisPlot(final long genId,SachDbUtil dbUtil) {
    	super("GA generation " + genId + " responses");
    	this.dbUtil = dbUtil;
    	
    	setDataFromGenId(genId);
        createBehCatPrefPlot();   	
    	
    }
    
    public GAGenAnalysisPlot(SachDbUtil dbUtil) {
    	super("GA responses");
    	this.dbUtil = dbUtil;
    }
    
    public void run(long genId) {
    	System.out.println("Plotting data from gen: " + genId);
    	if (spikeData==null) {
    		setDataFromGenId(genId);
    		createBehCatPrefPlot();
    	} else {
    		setDataFromGenId(genId);
    		updatePlot();
    	}
    }
    
    private void setDataFromGenId(final long genId) {
        // want to plot [stimObjId, avgFR, stdev, image] for each stim
        // 1. pass genId, grabbing stimObjIds, then avgFR, stdev, and image from there
    	dataset = null;
    	imageset = null;
    	    	
    	List<Long> stimObjIds = dbUtil.readStimObjIdsFromGenId(genId);

//    	List<SachStimDataEntry> stimData = dbUtil.readStimObjData(stimObjIds);
    	List<SachStimDataEntry> stimData = dbUtil.readListStimData(stimObjIds);
    	
//    	List<BsplineObjectSpec> stimSpecs = dbUtil.readStimObjSpecs(stimObjIds);
    	List<BsplineObjectSpec> stimSpecs = dbUtil.readListStimSpecs(stimObjIds);
    	
    	List<byte[]> imageData = new ArrayList<byte[]>();
    	    	
    	// put spike data into double[][] and pull images
		int len = stimData.size();
		double[][] thisSpikeData = new double[len][5];	// columns are [avgFR, stdev, stimObjId, birthGen,lineage]
		
		for (int n=0;n<len;n++) {
			SachStimDataEntry d = stimData.get(n);
			BsplineObjectSpec s = stimSpecs.get(n);
			// don't use blanks here
			if (!s.isBlankStim()) {
				thisSpikeData[n][0] = d.getAvgFR();
				thisSpikeData[n][1] = d.getStdFR();
				thisSpikeData[n][2] = d.getStimObjId();
				thisSpikeData[n][3] = d.getBirthGen();
				thisSpikeData[n][4] = d.getLineage();
			}
		}
		
		// add this spike data to existing spike data:
		if (spikeData == null) {
			spikeData = thisSpikeData;
		} else {
			spikeData = SachMathUtil.mergeArrays(spikeData,thisSpikeData);
		}
		
    	// sort spike data
    	Arrays.sort(spikeData,new Comparator<double[]>() {
    		@Override
    		public int compare(final double[] e1,final double[] e2) {
    			final double d1 = e1[0];
    			final double d2 = e2[0];
    			return (int)Math.signum(d2-d1);
    		}
    	});
    	
    	
    	// add spike data for plotting and pull images
    	final DefaultStatisticalCategoryDataset result = new DefaultStatisticalCategoryDataset();
    	for (int n=0;n<spikeData.length;n++) {
    		double[] d = spikeData[n];
    		result.add(d[0],d[1],"",String.valueOf(n));
    		long id = (long)d[2];
    		//System.out.println(id);
    		byte[] im;
    		try {
        		im = dbUtil.readThumbnail(id);
    		} catch (Exception e) {
    			//System.out.println("no thumbnail found for " + id);
    			im = null;
    		}
    		imageData.add(im);
    	}
    	
    	// add background FR and StDev:
		result.add(stimData.get(0).getBkgdAvgFR(), stimData.get(0).getBkgdStdFR(),"","bkgd");
		bkgdData[0] = stimData.get(0).getBkgdAvgFR();
		bkgdData[1] = stimData.get(0).getBkgdStdFR();
    	
    	this.setDataset(result);
    	this.setImageset(imageData);
    }
    
    
    private void createBehCatPrefPlot() {
    	
        Font axislabelfont = new Font("Helvetica", Font.PLAIN, 11);
        Font ticklabelfont = new Font("Helvetica", Font.PLAIN, 10);
//        final StatisticalCategoryDataset dataset = createDataset();

        // setup axes
        final CategoryAxis xAxis = new CategoryAxis("stimulus");
        final ValueAxis yAxis = new NumberAxis("response");

        xAxis.setLowerMargin(0.01d); // percentage of space before first bar
        xAxis.setUpperMargin(0.01d); // percentage of space after last bar
        xAxis.setCategoryMargin(0.0d); // percentage of space between categories
        xAxis.setLabelFont(axislabelfont);
//        xAxis.setTickLabelPaint(new Color(0,0,0,255/2));
//        xAxis.setTickLabelFont(ticklabelfont);
        xAxis.setTickLabelsVisible(false);
        xAxis.setTickMarksVisible(false);
        
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
        
//        final CategoryPlot plot = new CategoryPlot(dataset, xAxis, yAxis, renderer);
        plot = new CategoryPlot(dataset, xAxis, yAxis, renderer);
        plot.setOutlineVisible(false);
        plot.setRangeGridlinesVisible(true);
        plot.setDomainGridlinesVisible(true);
        
        // add background level
        ValueMarker bkgdMarker = new ValueMarker(bkgdData[0], Color.RED, new BasicStroke(1.5f,BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER,10.0f,new float[]{4.0f}, 0.0f));
        plot.addRangeMarker(bkgdMarker);
        
        final JFreeChart chart = new JFreeChart("",new Font("Helvetica", Font.BOLD, 14),plot,true);
        chart.setBackgroundPaint(Color.white);
        chart.setPadding(new RectangleInsets(25d,0d,0d,25d));
        
        // add the chart to a panel...
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(width,200));
//        setContentPane(chartPanel);

        
        add(chartPanel,BorderLayout.NORTH,0);
        
        // create stim image panel
        add(createStimImagePanel(),BorderLayout.CENTER,1);
       
    }
        
    private JScrollPane createStimImagePanel() {
    	list = new JList(); //data has type Object[]
    	list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    	list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
    	list.setVisibleRowCount(-1);
    	
    	Vector<ImageIcon> images = generateImagesFromImageset();
    	list.setListData(images);
    	
    	// put into scroll panel
    	JScrollPane listScroller = new JScrollPane(list);
    	listScroller.setPreferredSize(new Dimension(width,500));
    	
    	return listScroller;
    }
    
    private Vector<ImageIcon> generateImagesFromImageset() {

    	// add images to list:
		double maxFR = spikeData[0][0];
		double bkgdFR = spikeData[spikeData.length-1][0];
    	
    	Vector<ImageIcon> images = new Vector<ImageIcon>();
    	//int maxNumImagesToShow = 80;
    	for (int n=0;n<imageset.size();n++) {	//(byte[] b : imageset) {
    		byte[] b = imageset.get(n);
    		if (b != null) {
    			BufferedImage bimg = null;
    			//System.out.print(" " + n);
    			try {
					bimg = ImageIO.read(new ByteArrayInputStream(b));
				} catch (IOException e) {
					e.printStackTrace();
				}

    			// change color
    			double thisFR = spikeData[n][0];
    			double normFR = (thisFR-bkgdFR)/(maxFR-bkgdFR);
    			normFR = bkgdFR > thisFR ? 0 : normFR;
    			normFR = normFR < 0 ? 0 : normFR;
    			normFR = normFR > 1 ? 1 : normFR;
    	        Color oldColor = new Color(bimg.getRGB(0, 0));
//    	        int redness = (int) (255*normFR);
    	        int redness = (int) ((255-oldColor.getRed())*normFR+oldColor.getRed());
    	        int greenness = (int) (oldColor.getGreen()-normFR*oldColor.getGreen());
    	        int blueness = (int) (oldColor.getBlue()-normFR*oldColor.getBlue());
    			Color newColor = new Color(redness,greenness,blueness);
//    			Color newColor = new Color(redness,0,0);
    	        changeColor(bimg,oldColor,newColor);
    	        
    			// add text
    			Graphics graphics = bimg.getGraphics();
    			Font f = new Font(Font.SANS_SERIF,Font.PLAIN,28);
    			String birthGen = Integer.toString((int)spikeData[n][3]);
    			String lineage = Integer.toString((int)spikeData[n][4]);
//    	        graphics.setColor(Color.CYAN);
//    	        graphics.fillRect(0, 0, 50, 50);
    	        graphics.setFont(f);
    	        graphics.setColor(Color.BLACK);		
    			graphics.drawString(birthGen,8,290);
    	        graphics.setColor(Color.WHITE);		
    			graphics.drawString(lineage,277,290);
    			graphics.dispose();
    			
    			// rescale image
    			Image img = new ImageIcon(bimg).getImage();
    			img = img.getScaledInstance(100, 100,  java.awt.Image.SCALE_SMOOTH);  
    			
    			ImageIcon icon = new ImageIcon(img); 
    			images.add(icon);    			

    		}
    	}
		return images;
    }
    
    public void updatePlot() {
    	plot.setDataset(dataset);
    	list.setListData(generateImagesFromImageset());
    }

    
    
    /**
     * Creates a sample dataset.
     *
     * @return The dataset.
     */
    private static StatisticalCategoryDataset createDataset() {

        final DefaultStatisticalCategoryDataset result = new DefaultStatisticalCategoryDataset();

        int numStims = 20;
        double[] FRs = SachMathUtil.randRange(100d, 0d, numStims);
        double[] stdevs = SachMathUtil.randRange(30d, 0d, numStims);
        
        for (int n=0;n<numStims;n++) {
        	result.add(FRs[n],stdevs[n], "",Integer.toString(n));
        }
        
        return result;
    }
    
    private void setDatasetFromData(double[][] data) {
    	// expect data as double[][] {{mean0,stdev0},{mean1,stdev1},...}
    	
    	// sort data
    	Arrays.sort(data,new Comparator<double[]>() {
    		@Override
    		public int compare(final double[] e1,final double[] e2) {
    			final double d1 = e1[0];
    			final double d2 = e2[0];
    			return (int)Math.signum(d2-d1);
    		}
    	});
    	
    	final DefaultStatisticalCategoryDataset result = new DefaultStatisticalCategoryDataset();
    	    	
    	for (int n=0;n<data.length;n++) {
    		double[] d = data[n];
    		
    		result.add(d[0],d[1],"",String.valueOf(n));
    	}
    	
    	this.setDataset(result);
    }

    public void changeColor(BufferedImage img, Color oldColor, Color newColor) {
        final int oldRGB = oldColor.getRGB();
        final int newRGB = newColor.getRGB();
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                if (img.getRGB(x, y) == oldRGB)
                    img.setRGB(x, y, newRGB);
            }
        }
    }

    /**
     * For testing from the command line.
     *
     * @param args  ignored.
     */
    public static void main(final String[] args) {
        
//        int len = 20;
//		double[][] spikeData = new double[len][2];
//		for (int n=0;n<len;n++) {
//			spikeData[n][0] = SachMathUtil.randRange(100d, 0d);
//			spikeData[n][1] = SachMathUtil.randRange(30d, 0d);
//		}
//		
//		// plot
//		int genNum = 1;
//    	String title = "GA generation " + genNum + " responses";
//    	final GAGenAnalysisPlot demo = new GAGenAnalysisPlot(title,spikeData);
//        demo.pack();
//        RefineryUtilities.centerFrameOnScreen(demo);
//        demo.setVisible(true);
//        
//        System.out.println(Arrays.deepToString(spikeData)); 
    	
//    	// test with db data:
    	// 138:9, 331:10, 260:10, 220:10, 167:10
    	long genId = 469;
    	int numGens = 10;
//    	GAGenAnalysisPlot demo = new GAGenAnalysisPlot(genId,setDbUtil());
    	GAGenAnalysisPlot demo = new GAGenAnalysisPlot(setDbUtil());
    	demo.run(genId);
    	demo.pack();
    	RefineryUtilities.centerFrameOnScreen(demo);
    	demo.setVisible(true);

    	for (int n=1;n<numGens;n++) {
	    	demo.run(genId+n);
	    	//demo.pack();
	    	demo.setVisible(true);
    	}
    	System.out.println("done");
    	
    }

	public StatisticalCategoryDataset getDataset() {
		return dataset;
	}

	public void setDataset(StatisticalCategoryDataset dataset) {
		this.dataset = dataset;
	}

	public List<byte[]> getImageset() {
		return imageset;
	}

	public void setImageset(List<byte[]> imageset) {
		this.imageset = imageset;
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
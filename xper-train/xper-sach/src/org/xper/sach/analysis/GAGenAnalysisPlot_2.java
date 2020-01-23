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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
import org.xper.sach.util.SachIOUtil;
import org.xper.sach.util.SachMathUtil;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * Plots firing rates and error (stdev) for the stimuli in a GA generation in a bar plot.
 * 
 * @author sach2
 *
 */

// TODO: want to split into two side-by-side panels (for each lineage) showing
// bar plots on top and images on bottom, segregated by lineage

public class GAGenAnalysisPlot_2 extends ApplicationFrame {

	private static final long serialVersionUID = 1L;

	SachDbUtil dbUtil;
	StatisticalCategoryDataset dataset;
	List<byte[]> imageset;
	double[][] spikeData = null;
	Set<Long> shownGenIds = new HashSet<Long>();
	double[] bkgdData = new double[2];
	
	CategoryPlot plot;
	JList[] list = new JList[2];
	
	int width = 858;//836;
	
	/**
     * Creates a new plot from the dataset.
     *
     * @param title  the frame title.
     */
    public GAGenAnalysisPlot_2(final String title, final StatisticalCategoryDataset dataset) {
        super(title);
        this.setDataset(dataset);
        createBehCatPrefPlot();
    }
    
    public GAGenAnalysisPlot_2(final String title, final double[][] data) {
        super(title);
        setDatasetFromData(data);        
        createBehCatPrefPlot();
    }

    public GAGenAnalysisPlot_2(SachDbUtil dbUtil) {
    	super("GA responses");
    	this.dbUtil = dbUtil;
    }
//    public GAGenAnalysisPlot_2(List<Long> genIds, SachDbUtil dbUtil) {
//    	super("GA responses. mult gen w/ firstGen: " + genIds.get(genIds.size()-1) );
//    	this.dbUtil = dbUtil;
//    	
//    	for (long genId : genIds) setDataFromGenId(genId);
//    	createBehCatPrefPlot();
//    	pack();
//		RefineryUtilities.centerFrameOnScreen(this);
//		setVisible(true);
//    }
    public GAGenAnalysisPlot_2(final long genId,SachDbUtil dbUtil) {
    	super("GA responses. single gen: " + genId + " responses");
    	this.dbUtil = dbUtil;
    	
    	setDataFromGenId(genId);
        createBehCatPrefPlot();   	
        pack();
		RefineryUtilities.centerFrameOnScreen(this);
		setVisible(true);
    }
    
    //public GAGenAnalysisPlot_2(SachDbUtil dbUtil) {
    //	super("GA responses");
    //	this.dbUtil = dbUtil;
    //}
    
    public void run(long genId) {
    	System.out.println("Plotting data from gen: " + genId);
    	if (spikeData==null) {
    		setDataFromGenId(genId); //this updates spikeData, which is why it needs to be inside both if-else statements, NOT outside
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
    	    	
//    	if (dbUtil.readString("ExpLog","type",new String[]{"globalGenId," + genId , "limit").equals("GAManual"))
    	Set<Long> allGenIds = new HashSet<Long>(dbUtil.readGAGenSetFromExpLog(genId));
    	if (allGenIds.isEmpty())
    		allGenIds.add(genId);
    	
    	Set<Long> newGenIds = new HashSet<Long>(allGenIds);
    	newGenIds.removeAll(shownGenIds);
    	shownGenIds.addAll(allGenIds);
    	List<Long> stimObjIds = new ArrayList<Long>();
    	for (long curG : newGenIds)  //for (long curG : allGenIds)
    		stimObjIds.addAll(dbUtil.readStimObjIdsFromGenId(curG));
    	
//    	List<SachStimDataEntry> stimData = dbUtil.readStimObjData(stimObjIds);
    	List<SachStimDataEntry> stimData = dbUtil.readListStimData(stimObjIds);
    	
//    	List<BsplineObjectSpec> stimSpecs = dbUtil.readStimObjSpecs(stimObjIds);
    	List<BsplineObjectSpec> stimSpecs = dbUtil.readListStimSpecs(stimObjIds);
    	
    	List<byte[]> imageData = new ArrayList<byte[]>();
    	    	
    	// put spike data into double[][] and pull images
    	int len = 0;
    	for (int n=0;n<stimData.size();n++)
    		if (!stimSpecs.get(n).isBlankStim())
    			len++;
    	
		double[][] thisSpikeData = new double[len][5];	// columns are [avgFR, stdev, stimObjId, birthGen,lineage]
		
		int addOffset = 0;
		for (int n=0;n<stimData.size();n++) {
			SachStimDataEntry d = stimData.get(n);
			BsplineObjectSpec s = stimSpecs.get(n);
			// don't use blanks here
			if (!s.isBlankStim()) {
				thisSpikeData[n-addOffset][0] = d.getAvgFR();
				thisSpikeData[n-addOffset][1] = d.getStdFR();
				thisSpikeData[n-addOffset][2] = d.getStimObjId();
				thisSpikeData[n-addOffset][3] = d.getBirthGen();
				thisSpikeData[n-addOffset][4] = d.getLineage();
			}
			else 
				addOffset++;
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
//    		if (n==40){
//    			System.out.print(n + "\n");
//    		}
    		double[] d = spikeData[n];
    		result.add(d[0],d[1],"",String.valueOf(n));
    		long id = (long)d[2];
    		
    		if (id==0) continue;
    		//System.out.println(id);
    		byte[] im;
    		try {
        		im = dbUtil.readThumbnail(id);
        		
    		} catch (Exception e) {
    			//System.out.println("no thumbnail found for " + id);
    			im = dbUtil.readThumbnailFromFile(id);
    		}
    		imageData.add(im);
//    		System.out.print(n + "\n");
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
        add(createStimImagePanel(0),BorderLayout.WEST,1);
        add(createStimImagePanel(1),BorderLayout.EAST,2);
       
    }
        
    private JScrollPane createStimImagePanel(int lineage) {
    	list[lineage] = new JList(); //data has type Object[]
    	list[lineage].setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    	list[lineage].setLayoutOrientation(JList.HORIZONTAL_WRAP);
    	list[lineage].setVisibleRowCount(-1);
    	
    	Vector<ImageIcon> images = generateImagesFromImageset(lineage);
    	list[lineage].setListData(images);
    	
    	// put into scroll panel
    	JScrollPane listScroller = new JScrollPane(list[lineage]);
    	listScroller.setPreferredSize(new Dimension(width/2,500));
    	
    	return listScroller;
    }
    
    private Vector<ImageIcon> generateImagesFromImageset(int lineage) {

    	// add images to list:
		double maxFR = spikeData[0][0];
		double bkgdFR = spikeData[spikeData.length-1][0];
    	
    	Vector<ImageIcon> images = new Vector<ImageIcon>();
    	//int maxNumImagesToShow = 80;
    	for (int n=0;n<imageset.size();n++) {	//(byte[] b : imageset) {
    		byte[] b = imageset.get(n);
    		
    		if (b != null && ((spikeData[n][4] == lineage) || (spikeData[n][4]==-1 && lineage==0))) {
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
//    			Color newColor = new Color(redness,greenness,blueness);
    	        Color newColor = new Color((int)(155*normFR)+50,50,50);
    	        changeColor(bimg,oldColor,newColor);
    	        
    			// add text
    			Graphics graphics = bimg.getGraphics();
    			Font f = new Font(Font.SANS_SERIF,Font.PLAIN,28);
//    			int rank = new Integer(n);
//    			if (lineage==1)
//    				rank = rank - imageset.size()/2;
    			
    			String objId  	= "r:" + n; //Long.toString((long)spikeData[n][2]);
    			String birthGen = Integer.toString((int)spikeData[n][3]);
//    			String lineageNum = Integer.toString((int)spikeData[n][4]);
//    	        graphics.setColor(Color.CYAN);
//    	        graphics.fillRect(0, 0, 50, 50);
    	        graphics.setFont(f);
//    	        graphics.setColor(Color.BLACK);	
    	        graphics.setColor(Color.WHITE);
    	        graphics.drawString(objId,8,25);
    			graphics.drawString(birthGen,8,290);
    			graphics.drawString(String.format("%.1f",spikeData[n][0]),230,25);
//    	        graphics.setColor(Color.WHITE);		
//    			graphics.drawString(lineageNum,277,290);
    			graphics.dispose();
    			
    			// rescale image
    			Image img = new ImageIcon(bimg).getImage();
    			img = img.getScaledInstance(100, 100,  java.awt.Image.SCALE_SMOOTH);  
    			
    			ImageIcon icon = new ImageIcon(img); 
    			images.add(icon);    	
    			
    			bimg = null;

    		}
    	}
		return images;
    }
    
    public void updatePlot() {
    	plot.setDataset(dataset);
    	list[0].setListData(generateImagesFromImageset(0));
    	list[1].setListData(generateImagesFromImageset(1));
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
    	SachDbUtil dbUtil = new SachDbUtil();
    	ComboPooledDataSource source = new ComboPooledDataSource();
    	source.setJdbcUrl("jdbc:mysql://172.30.6.48/shaggy_ecpc48_2016_07");
    	source.setUser("xper_rw");
		source.setPassword("up2nite");
		dbUtil.setDataSource(source);
    	
		String cellStr = SachIOUtil.promptString("cellNum (empty for active/last one) ");
    	long cellNum;
    	if (cellStr.isEmpty()){
    		cellNum = dbUtil.readLong("ExpRecTargets","isFinished=0;cellNum;-1");
    		if (cellNum==-1)
    			cellNum = dbUtil.readLong("ExpRecTargets","cellNum");
    	}
    	else	
    		cellNum = Long.parseLong(cellStr);
    	
    	System.out.println("cellNum = " + cellNum);
		
		long genId = dbUtil.readLong("ExpLog","globalGenId",new String[] {"type,'GA'" , "cellNum," + cellNum , "odl"});
		
    	GAGenAnalysisPlot_2 plot = new GAGenAnalysisPlot_2(dbUtil);
    	plot.run(genId);
    	plot.pack();
    	RefineryUtilities.centerFrameOnScreen(plot);
    	plot.setVisible(true);
    	
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

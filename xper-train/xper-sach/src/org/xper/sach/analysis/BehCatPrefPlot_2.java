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

public class BehCatPrefPlot_2 extends ApplicationFrame {

	private static final long serialVersionUID = 1L;

	SachDbUtil dbUtil;
	List<Long> genIds = new ArrayList<Long>();
	StatisticalCategoryDataset barGraphData;
	List<byte[]> imageSetSorted;
	List<String> labelsSorted;
	double[][] spikeData = null;
	double[] bkgdData = new double[2];
	
	CategoryPlot plot;
	JList list;
	
	int width = 1000;//836;
	
	/**
     * Creates a new plot from the barGraphData.
     *
     * @param title  the frame title.
     */
//    public BehCatPrefPlot_2(final String title, final StatisticalCategoryDataset barGraphData) {
//        super(title);
//        this.setDataset(barGraphData);
//        createBehCatPrefPlot();
//    }
    
//    public BehCatPrefPlot_2(final String title, final double[][] data) {
//        super(title);
//        setDatasetFromData(data);        
//        createBehCatPrefPlot();
//    }
    
	public BehCatPrefPlot_2(List<Long> genIds,SachDbUtil dbUtil){
		super("BehPlot: gen" + genIds.get(0));
    	this.genIds = genIds;
    	this.dbUtil = dbUtil;
	}
    public BehCatPrefPlot_2(long genId,SachDbUtil dbUtil){
    	super("BehPlot: gen" + genId);
    	genIds.add(genId);
    	this.dbUtil = dbUtil;
    }
    public BehCatPrefPlot_2(long genId,SachDbUtil dbUtil,String runNow){
    	super("BehPlot: gen" + genId);
    	genIds.add(genId);
    	this.dbUtil = dbUtil;
    	if (runNow.toLowerCase().equals("run")) {
    		run_pack_vis();
    	}
    }
    public BehCatPrefPlot_2(List<Long> genIds,SachDbUtil dbUtil,String runNow){
    	super("BehPlot: gen" + genIds.get(0));
    	this.genIds = genIds;
    	this.dbUtil = dbUtil;
    	if (runNow.toLowerCase().equals("run")) {
    		run_pack_vis();
    	}
    }
    
    public void run_pack_vis(){
    	run();
		pack();
		RefineryUtilities.centerFrameOnScreen(this);
		setVisible(true);
    }
    
    
//    public BehCatPrefPlot_2(final long genId,SachDbUtil dbUtil) {
//    	super("GA generation " + genId + " responses");
//    	this.dbUtil = dbUtil;
//    	
//    	setDataFromGenId(genId);
//        createBehCatPrefPlot();   	
//    	
//    }
    
//    public BehCatPrefPlot_2(SachDbUtil dbUtil) {
//    	super("GA responses");
//    	this.dbUtil = dbUtil;
//    }
    
    public void run() {
//    	System.out.println("Plotting data from gen: " + genId);
    	if (spikeData==null) {
    		setDataFromGenId();
    		createBehCatPrefPlot();
    	} else {
    		setDataFromGenId();
    		updatePlot();
    	}
    }
    
    private void setDataFromGenId() {
        // want to plot [stimObjId, avgFR, stdev, image] for each stim
        // 1. pass genId, grabbing stimObjIds, then avgFR, stdev, and image from there
    	barGraphData = null;
//    	imageSetSorted = null;
    	    	
    	List<Long> stimObjIds = new ArrayList<Long>();
    	for (long genId : genIds)
    		stimObjIds.addAll(dbUtil.readStimObjIdsFromGenId(genId));
    	
    	List<SachStimDataEntry> stimData = dbUtil.readListStimData(stimObjIds);
    	List<BsplineObjectSpec> stimSpecs = dbUtil.readListStimSpecs(stimObjIds);
    	List<byte[]> imageData = new ArrayList<byte[]>();
    	    	
    	// put spike data into double[][] and pull images
		int len = stimData.size();
		spikeData = new double[len][5];	// columns are [avgFR, stdev, stimObjId, birthGen,lineage]
		
		boolean isExptRep = false;
		int maxCat = -1;
		int minCat = 32;
		for (int n=0;n<len;n++) {
			SachStimDataEntry d = stimData.get(n);
			BsplineObjectSpec s = stimSpecs.get(n);
			// don't use blanks here
			if (!s.isBlankStim()) {
				spikeData[n][0] = d.getAvgFR();
				spikeData[n][1] = d.getStdFR();
				spikeData[n][2] = (double)s.getCategory();
				maxCat = Math.max((int)spikeData[n][2],maxCat);
				minCat = Math.min((int)spikeData[n][2],minCat);
				if (s.getMorphLim()==0)	{
					spikeData[n][3] = -1;
				}
				else {
					spikeData[n][3] = (double)d.getStimObjId();
					isExptRep = true;
				}
			}
			else {
				spikeData[n][0] = -1;
				spikeData[n][1] = -1;
				spikeData[n][2] = -1;
				spikeData[n][3] = -1;
			}
			spikeData[n][4] = n;
		}
		
    	String[] labels = new String[spikeData.length];
    	
		if (maxCat>15) maxCat=31;
		else if (maxCat>7) maxCat=15;
		else maxCat = 7;

		if (minCat>7) minCat=8;
		else minCat=0;
		
		// add this spike data to existing spike data:
//		if (spikeData == null) {
//			spikeData = thisSpikeData;
//		} else {
//			spikeData = SachMathUtil.mergeArrays(spikeData,thisSpikeData);
//		}
//		double[] bkgdData = new double[2];
		final DefaultStatisticalCategoryDataset result = new DefaultStatisticalCategoryDataset();
		String newLabel;
		if (isExptRep){
			int numMorphsPerCat = (spikeData.length/8)-2;
			for (int cc=minCat;cc<=maxCat;cc++) {
				int counter = 0;
				for (int sd=0;sd<spikeData.length;sd++){
					double[] curData = spikeData[sd];
					if ((int)curData[2]==cc && curData[3]==-1){
						counter++;
						if (counter==1)	{
							newLabel = cc + "m";
							labels[sd]=newLabel;
							result.add(curData[0],curData[1],"",newLabel);
						}
						else{            
							newLabel = cc + "n";
							labels[sd]=newLabel;
							result.add(curData[0],curData[1],"",newLabel);
							break;
						}
					}
				}
				counter=0;
				for (int sd=0;sd<spikeData.length;sd++){
					double[] curData = spikeData[sd];
					if ((int)curData[2]==cc && curData[3]!=-1){
						counter++;
						newLabel = String.valueOf((double)(cc)+(double)(counter)/10);
						labels[sd]=newLabel;
						result.add(curData[0],curData[1],"",newLabel);
						if (counter==numMorphsPerCat) break;
					}
				}
				
				if (cc<maxCat) result.add(0,0,"","e"+cc);
			}
			
			bkgdData[0] = 0;
			bkgdData[1] = 0;
		}
		else {
			for (int cc=0;cc<=maxCat;cc++) {
				for (int sd=0;sd<spikeData.length;sd++){
					double[] curData = spikeData[sd];
					if ((int)curData[2]==cc){
						if (cc<16)	{
							newLabel = String.valueOf(cc);
							labels[sd]=newLabel;
							result.add(curData[0],curData[1],"",newLabel);
						}
						else {
							newLabel = "U" + (cc-16);
							labels[sd]=newLabel;
							result.add(curData[0],curData[1],"",newLabel);
						}
						break;
					}
				}
				if (cc % 16 == 15) result.add(0,0,"","e"+cc);
			}
			
			// add background FR and StDev:
			bkgdData[0] = stimData.get(0).getBkgdAvgFR();
			bkgdData[1] = stimData.get(0).getBkgdStdFR();
			result.add(bkgdData[0], bkgdData[1],"","bkgd");
		}

    	this.setDataset(result);
		
    	// sort spike data
    	Arrays.sort(spikeData,new Comparator<double[]>() {
    		@Override
    		public int compare(final double[] e1,final double[] e2) {
    			final double d1 = e1[0];
    			final double d2 = e2[0];
    			return (int)Math.signum(d2-d1);
    		}
    	});
    	
    	List<String> sortLabels = new ArrayList<String>();
    	// add spike data for plotting and pull images
    	for (int n=0;n<spikeData.length;n++) {
    		//System.out.println(id);
    		byte[] im;
    		if (spikeData[n][3]!=-1) im = dbUtil.readThumbnail((long)spikeData[n][3]);
    		else if (spikeData[n][2]!=-1) im = dbUtil.readCanonicalThumbnail((int)spikeData[n][2]);
    		else continue;
    		imageData.add(im);
    		sortLabels.add(labels[(int)spikeData[n][4]]);
    	}
    	
    	this.setImageset(imageData);
    	this.setLabelsSorted(sortLabels);
    }
    
    
    private void createBehCatPrefPlot() {
    	
        Font axislabelfont = new Font("Helvetica", Font.PLAIN, 11);
        Font ticklabelfont = new Font("Helvetica", Font.PLAIN, 8);
//        final StatisticalCategoryDataset barGraphData = createDataset();

        // setup axes
        final CategoryAxis xAxis = new CategoryAxis("stimulus");
        final ValueAxis yAxis = new NumberAxis("response");

        xAxis.setLowerMargin(0.01d); // percentage of space before first bar
        xAxis.setUpperMargin(0.01d); // percentage of space after last bar
        xAxis.setCategoryMargin(0.0d); // percentage of space between categories
        xAxis.setLabelFont(axislabelfont);
        xAxis.setTickLabelPaint(new Color(0,0,0,255/2));
        xAxis.setTickLabelFont(ticklabelfont);
//        xAxis.setTickLabelsVisible(false);
//        xAxis.setTickMarksVisible(false);
        
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
        
//        final CategoryPlot plot = new CategoryPlot(barGraphData, xAxis, yAxis, renderer);
        plot = new CategoryPlot(barGraphData, xAxis, yAxis, renderer);
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
        add(createStimImagePanel(),BorderLayout.SOUTH,1);
       
    }
        
    private JScrollPane createStimImagePanel() {
    	list = new JList();
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
		double bkgdFR = bkgdData[0];
//		double bkgdFR = spikeData[spikeData.length-1][0];
    	
    	Vector<ImageIcon> images = new Vector<ImageIcon>();
    	//int maxNumImagesToShow = 80;
    	for (int n=0;n<imageSetSorted.size();n++) {	//(byte[] b : imageSetSorted) {
    		byte[] b = imageSetSorted.get(n);
    		String label = labelsSorted.get(n);
    		if (b != null && label!=null) {
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
//    			String category = Integer.toString((int)spikeData[n][2]);
//    	        graphics.setColor(Color.CYAN);
//    	        graphics.fillRect(0, 0, 50, 50);
    	        graphics.setFont(f);
//    	        graphics.setColor(Color.BLACK);	
    	        graphics.setColor(Color.WHITE);		
    			graphics.drawString(label,8,25);
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
    
    public void updatePlot(){
	    plot.setDataset(barGraphData);
		list.setListData(generateImagesFromImageset());
    }
   
//    private void setDatasetFromData(double[][] data) {
//    	// expect data as double[][] {{mean0,stdev0},{mean1,stdev1},...}
//    	
//    	// sort data
////    	Arrays.sort(data,new Comparator<double[]>() {
////    		@Override
////    		public int compare(final double[] e1,final double[] e2) {
////    			final double d1 = e1[0];
////    			final double d2 = e2[0];
////    			return (int)Math.signum(d2-d1);
////    		}
////    	});
//    	
//    	final DefaultStatisticalCategoryDataset result = new DefaultStatisticalCategoryDataset();
//    	    	
//    	for (int n=0;n<data.length;n++) {
//    		double[] d = data[n];
//    		
//    		result.add(d[0],d[1],"",String.valueOf(n));
//    	}
//    	
//    	this.setDataset(result);
//    }

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
    public static void main(String[] args) {
    	SachDbUtil dbUtil = new SachDbUtil();
    	ComboPooledDataSource source = new ComboPooledDataSource();
    	source.setJdbcUrl("jdbc:mysql://172.30.6.48/shaggy_ecpc48_2018_02");
    	source.setUser("xper_rw");
		source.setPassword("up2nite");
		dbUtil.setDataSource(source);

    	List<Long> genIds = new ArrayList<Long>();
    	
    	String cellStr = SachIOUtil.promptString("cellNum (empty for active/last one, negative number for genId) ");
    	long cellNum;
    	if (cellStr.isEmpty()){
    		cellNum = dbUtil.readLong("ExpRecTargets","isFinished=0;cellNum;-1");
//    		if (cellNum==-1)
//    			cellNum = dbUtil.readLong("ExpRecTargets","cellNum");
    	}
    	else{	
    		cellNum = Long.parseLong(cellStr);
    		if (cellNum<0){
    			genIds.add(-cellNum);
    			cellNum = -1;
    		}
    	}
    		
    	
    	System.out.println("cellNum = " + cellNum);
    	
    	char type = SachIOUtil.prompt("(q) for beh_quick, (b) for beh_morph, (e) for beh_expt ");
    	if (type=='q'){
    		if (cellNum==-1){
    			if (genIds.isEmpty())
    				genIds = dbUtil.readRowsLong("ExpLog","globalGenId",new String[]{"type,'BEH_quick'" , "status,'START'" , "odl"});
    		}
    		else
    			genIds = dbUtil.readRowsLong("ExpLog","globalGenId",new String[]{"type,'BEH_quick'" , "status,'STOP'" , "cellNum," + cellNum});
    	}
    	else if (type=='e'){
    		if (cellNum==-1){
    			if (genIds.isEmpty())
    				genIds = dbUtil.readRowsLong("ExpLog","globalGenId",new String[]{"type,'BEH_exptRepeat'" , "status,'STOP'" , "odl"});
    		}
    		else
    			genIds = dbUtil.readRowsLong("ExpLog","globalGenId",new String[]{"type,'BEH_exptRepeat'" , "status,'STOP'" , "cellNum," + cellNum});
    	}
    	else if (type=='b'){
    		if (cellNum==-1){
    			if (genIds.isEmpty())
    				genIds = dbUtil.readRowsLong("ExpLog","globalGenId",new String[]{"type,'BEH_morph'" , "status,'STOP'" , "odl"});
    		}
    		else
    			genIds = dbUtil.readRowsLong("ExpLog","globalGenId",new String[]{"type,'BEH_morph'" , "status,'STOP'" , "cellNum," + cellNum});
    	}
    	else {
    		System.out.println("do not recognize type");
    		return;
    	}
    	if (genIds.size()>1){
    		String plotStr = SachIOUtil.promptString("which one to plot? (0 to " + (genIds.size()-1) + ", empty for all) ");
    		if (!plotStr.isEmpty()){
    			long genId = genIds.get(Integer.parseInt(plotStr));
    			genIds = new ArrayList<Long>();
    			genIds.add(genId);
    		}
    	}
    	if (!genIds.isEmpty())
    		new BehCatPrefPlot_2(genIds,dbUtil,"run");
    	else
    		System.out.println("no such trials found for cell " + cellNum);
    }
    
    public void setGenIds(long genId){
    	genIds.add(genId);
    }
    public void setGenIds(List<Long> genIds){
    	this.genIds = genIds;
    }
    

	public StatisticalCategoryDataset getDataset() {
		return barGraphData;
	}

	public void setDataset(StatisticalCategoryDataset barGraphData) {
		this.barGraphData = barGraphData;
	}

	public List<byte[]> getImageset() {
		return imageSetSorted;
	}

	public void setImageset(List<byte[]> imageSetSorted) {
		this.imageSetSorted = imageSetSorted;
	}
	public void setLabelsSorted(List<String> sortLabels){
		this.labelsSorted = sortLabels;
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

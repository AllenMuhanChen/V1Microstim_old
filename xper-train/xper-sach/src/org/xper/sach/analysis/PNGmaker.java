package org.xper.sach.analysis;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import org.jzy3d.plot3d.rendering.image.GLImage;
import org.lwjgl.opengl.GL11;
import org.xper.drawing.Drawable;
import org.xper.sach.drawing.StimTestWindow;
import org.xper.sach.drawing.stimuli.BsplineObject;
import org.xper.sach.drawing.stimuli.BsplineObjectSpec;
import org.xper.sach.expt.SachExptSpecGenerator.BehavioralClass;
import org.xper.sach.expt.SachExptSpecGenerator.StimType;
import org.xper.sach.expt.generate.SachRandomGeneration.TrialType;
import org.xper.sach.util.CreateDbDataSource;
import org.xper.sach.util.SachDbUtil;
import org.xper.sach.util.SachMathUtil;

public class PNGmaker {

	SachDbUtil dbUtil;	
	List<BsplineObjectSpec> specs = new ArrayList<BsplineObjectSpec>();
//	List<BsplineObject> objs = new ArrayList<BsplineObject>();
//	List<Drawable> objs = new ArrayList<Drawable>();
	
	int height = 300;	// height & width of stim window	
	int width = 300;
	double mag = 3;		// magnification of stimulus

	public PNGmaker(BsplineObjectSpec spec) {
		specs.add(spec);
		createAndSavePNGs();
	}

	public PNGmaker() {}
	
	public PNGmaker(SachDbUtil dbUtil) {
		this.dbUtil = dbUtil;
	}
	
//	public PNGmaker(List<BsplineObjectSpec> specs) {
//		this.specs = specs;
//		createAndSavePNGs();
//	}

//	public PNGmaker(List<Long> stimObjIds) {
//		// use stimObjId to get spec from db, then create objs
//		List<Drawable> objs = spec2obj(id2spec(stimObjIds));
//		createAndSavePNGsfromObjs(objs,stimObjIds);
//	}
	
	public void MakeFromIds(List<Long> stimObjIds) {
		MakeFromIds(stimObjIds,"GA");
	}
	public void MakeFromIds(List<Long> stimObjIds,String imageFolder) {
		// use stimObjId to get spec from db, then create objs
		List<Drawable> objs = spec2obj(id2spec(stimObjIds));
		stimObjIds.addAll(stimObjIds);
		createAndSavePNGsfromObjs(objs,stimObjIds,imageFolder);
	}
	
	private List<BsplineObjectSpec> id2spec(List<Long> stimObjIds) {
		List<BsplineObjectSpec> specs = new ArrayList<BsplineObjectSpec>();
		for (Long id : stimObjIds) {
			BsplineObjectSpec spec;
			if (id>31L){
				// (default) input is stimObjId
//				String s = dbUtil.readStimSpecFromStimObjId(id).getSpec();
//				spec = BsplineObjectSpec.fromXml(s);
				spec = BsplineObjectSpec.fromXml(dbUtil.readSingleStimSpec(id).getSpec());
			}
			else {
				// category inputted instead of stimObjId
				spec = new BsplineObjectSpec();
				spec.setStimType(StimType.BEH_MedialAxis.toString());
				spec.setCategory(id.intValue());
				spec.setAnimation(false);
				spec.setMorphLim(0);
			}
			specs.add(spec);
		}
		return specs;
	}
	
	private List<Drawable> spec2obj(List<BsplineObjectSpec> specs) {
		List<Drawable> objs = new ArrayList<Drawable>();
		
		for (BsplineObjectSpec spec : specs) {
			BsplineObject obj = new BsplineObject();
			obj.setCantFail(true);
			obj.setSpec(spec.toXml());
			objs.add(obj);
		}
		for (BsplineObjectSpec spec : specs) {
			BsplineObject obj2 = new BsplineObject();
			obj2.setCantFail(true);
			obj2.setSpec(spec.toXml());
			obj2.drawNodes = true;
			obj2.drawNums = true;
			objs.add(obj2);
		}
		// specs -> objs, then pass to createAndSavePNGs
		return objs;
	}

	public void createAndSavePNGsfromObjs(List<Drawable> objs,List<Long> stimObjIds,String imageFolder) {

		StimTestWindow testWindow = new StimTestWindow(height,width,mag);
//		testWindow.setDoPause(true);
		testWindow.setSpeedInSecs(0.1);
		testWindow.setSavePngtoFile(true);
		testWindow.setPngMaker(this);
//		String pngPath = System.getProperty("user.dir") + "/images/" + imageFolder + "/";
		testWindow.setPngPath(imageFolder);
		
		System.out.println("creating and saving PNGs...");

		testWindow.setStimObjs(objs);
		testWindow.setStimObjIds(stimObjIds);
		
		testWindow.testDraw();				// draw object
		testWindow.close();
		System.out.println("...done saving PNGs");
	}
	
	public void createAndSavePNGs() {

		StimTestWindow testWindow = new StimTestWindow(height,width,mag);
//		testWindow.setDoPause(true);
		testWindow.setSpeedInSecs(1.0);
		testWindow.setSavePngtoFile(true);
		testWindow.setPngMaker(this);

		System.out.println("creating PNGs from specs...");

		for (BsplineObjectSpec spec : specs) {
			testWindow.setStimObjIds(spec.getStimObjId());
			BsplineObject obj = new BsplineObject();
			obj.setCantFail(true);
			obj.setSpec(spec.toXml());
			testWindow.setStimObjs(obj);			// add object to be drawn
		}

		testWindow.testDraw();				// draw object
//		testWindow.close();
		System.out.println("...done saving PNGs");
	}

	public void run() { // NOT USING NOW
		// this only works for one obj and it became to complicated to fix, so instead we will call 
		// PNG maker from StimTestWindow -shs
		//		int height = 200;	// height & width of stim window	
		//		int width = 200;
		//		double mag = 2;		// magnification of stimulus

		// -- for testing:
		CreateDbDataSource dataSourceMaker = new CreateDbDataSource();
		setDbUtil(new SachDbUtil(dataSourceMaker.getDataSource()));
		// --

		StimTestWindow testWindow = new StimTestWindow(height,width,mag);
		testWindow.setDoPause(true);
		testWindow.setSpeedInSecs(1.0);

		System.out.println("creating PNG from spec");

		for (BsplineObjectSpec spec : specs) {

			BsplineObject obj = new BsplineObject();
			obj.setCantFail(true);
			obj.setSpec(spec.toXml());
			testWindow.setStimObjs(obj);			// add object to be drawn
			//			System.out.println();

			//testWindow.experimentResume();

			testWindow.testDraw();				// draw object

			// capture image here:
			//		ImgBinData img = new ImgBinData();
			int h = testWindow.getHeight();
			int w = testWindow.getWidth();
			byte[] data = screenShotBinary(w,h);  

			System.out.println("the img data length is " + data.length);

			// save image:
			long id = spec.getStimObjId();

			dbUtil.writeThumbnail(id,data);
			System.out.println("saved stimSpecId: " + id);

			// test read:
			byte[] dataOut = dbUtil.readThumbnail(id);
			boolean b = SachMathUtil.isArrEqual(data,dataOut);
			System.out.println("dataIn = dataOut is " + b);

			//testWindow.experimentResume();

		}

		//testWindow.close();
	}
	
	
	// 2 June 2016
	public void saveFrameBufferToPng(long stimObjId, int height, int width) {
		// when this runs, it takes a screenshot and saves it with the stimObjId label
		// capture image here:
		//		ImgBinData img = new ImgBinData();
		byte[] data = screenShotBinary(width,height);  

//		System.out.println("the img data length is " + data.length);

		// save image:
//		if (stimObjId>31L) dbUtil.writeThumbnail(stimObjId,data);  //default
//		else dbUtil.writeCanonicalThumbnail(stimObjId,data);
		if (stimObjId>31L) 	dbUtil.writeLine("Thumbnail",new Object[]{stimObjId,data},true);
		else 				dbUtil.updateLine("CanonicalThumbnail","data",data,dbUtil.sArray(stimObjId));
		System.out.println("saved stimSpecId: " + stimObjId);
		
	}
	
	
	
	public void makePngFilefromScreenShot(int height, int width, String pngFilename) {
		// when this runs, it takes a screenshot and saves it with the stimObjId label
		// capture image here:
		//		ImgBinData img = new ImgBinData();
		//byte[] data =
		screenShotBinary(width,height, pngFilename);  

		//System.out.println("the img data length is " + data.length);

		// save image:
		//dbUtil.writeThumbnail(stimObjId,data);
		//System.out.println("saved stimSpecId: " + stimObjId);

		//		// test read:
		//		byte[] dataOut = dbUtil.readThumbnail(stimObjId);
		//		boolean b = SachMathUtil.isArrEqual(data,dataOut);
		//		System.out.println("dataIn = dataOut is " + b);
	}

	private byte[] screenShotBinary(int width, int height) 
	{
		return screenShotBinary(width,height,"");
	}

	private byte[] screenShotBinary(int width, int height, String filename) 
	{
		// allocate space for RBG pixels
		//System.out.print("In screenShot to binary\n");

		ByteBuffer framebytes = allocBytes(width * height * 3);

		int[] pixels = new int[width * height];
		int bindex;
		// grab a copy of the current frame contents as RGB (has to be UNSIGNED_BYTE or colors come out too dark)
		GL11.glReadPixels(0, 0, width, height, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, framebytes);
		// copy RGB data from ByteBuffer to integer array
		for (int i = 0; i < pixels.length; i++) {
			bindex = i * 3;
			pixels[i] =
					0xFF000000                                          // A
					| ((framebytes.get(bindex)   & 0x000000FF) << 16)   // R
					| ((framebytes.get(bindex+1) & 0x000000FF) <<  8)   // G
					| ((framebytes.get(bindex+2) & 0x000000FF) <<  0);  // B
		}
		// free up this memory
		framebytes = null;
		// flip the pixels vertically (opengl has 0,0 at lower left, java is upper left)
		pixels = GLImage.flipPixels(pixels, width, height);

		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			// Create a BufferedImage with the RGB pixels then save as PNG
			BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			image.setRGB(0, 0, width, height, pixels, 0, width);

			//javax.imageio.ImageIO.write(image, "png", new File(saveFilename));

			javax.imageio.ImageIO.write(image, "png", out);
			byte[] data = out.toByteArray();

			System.out.println("the img data length is " + data.length);

			// I decide to also save the file to harddisk this moment since it will be easier to read from Matlab
			if (filename.length() > 0) {
				// JK 2 June 2016
				//System.out.println("PNGmaker screenShotBinary() : writing file " + filename);
				
				//				String dir = "./matlabSimpleAnalysis/matlabPng/";
				//				String saveFilename = dir + filename + ".png";
				//				javax.imageio.ImageIO.write(image, "png", new File(saveFilename));
				javax.imageio.ImageIO.write(image, "png", new File(filename));
			}

			return data;
		}
		catch (Exception e) {
			System.out.println("screenShot(): exception " + e);
			return null;
		}
	}

	/**
	locate memory, subFunction of screenShot
	 */
	public static ByteBuffer allocBytes(int howmany) {
		final int SIZE_BYTE = 4;
		return ByteBuffer.allocateDirect(howmany * SIZE_BYTE).order(ByteOrder.nativeOrder());
	}



	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// for testing
		System.out.println("__test drawSnapShotModule__");

		long stimId = SachMathUtil.randRange(100000,0);
		List<BsplineObjectSpec> specs = new ArrayList<BsplineObjectSpec>();
		List<Drawable> objs = new ArrayList<Drawable>();
		List<Long> ids = new ArrayList<Long>();

		for (int k=0;k<5;k++) {
			System.out.println("\n>>rand_obj #"+k+" ");
			BsplineObject obj = new BsplineObject();
			obj.makeRandObj();
			BsplineObjectSpec spec = obj.getSpec();
			spec.setStimObjId(stimId+k);
			specs.add(spec);
			objs.add(obj);
			ids.add(stimId+k);
		}

		PNGmaker snapper = new PNGmaker();
		snapper.createDbUtil();
//		snapper.setSpecs(specs);
//		snapper.createAndSavePNGs();
		
		snapper.createAndSavePNGsfromObjs(objs, ids,"GA");
	}
	
	private void createDbUtil() {
		// -- for testing only
		CreateDbDataSource dataSourceMaker = new CreateDbDataSource();
		setDbUtil(new SachDbUtil(dataSourceMaker.getDataSource()));
	}

	public void setDbUtil(SachDbUtil dbUtil) {
		this.dbUtil = dbUtil;
	}

	public List<BsplineObjectSpec> getSpecs() {
		return specs;
	}

	public void setSpecs(List<BsplineObjectSpec> specs) {
		this.specs = specs;
	}


}

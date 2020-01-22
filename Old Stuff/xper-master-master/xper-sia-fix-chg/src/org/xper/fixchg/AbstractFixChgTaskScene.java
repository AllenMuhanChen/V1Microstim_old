package org.xper.fixchg;

import org.lwjgl.opengl.GL11;
import org.xper.Dependency;
import org.xper.classic.vo.TrialContext;
import org.xper.drawing.Context;
import org.xper.drawing.Drawable;
import org.xper.drawing.ScreenMarker;
import org.xper.drawing.TaskScene;
import org.xper.drawing.renderer.AbstractRenderer;
import org.xper.util.XmlUtil;

public abstract class AbstractFixChgTaskScene implements TaskScene {
	
	@Dependency
	protected AbstractRenderer stereoRenderer;
	@Dependency
	protected AbstractRenderer monoRenderer;
	
	@Dependency
	protected Drawable blankScreen;
	@Dependency
	protected ScreenMarker marker;
	
	protected AbstractRenderer renderer;
	protected int width;
	protected int height;

	@Override
	public void drawBlank(Context context, final boolean fixationOn, final boolean markerOn) {
		blankScreen.draw(null);
		renderer.draw(new Drawable() {
			public void draw(Context context) {		
				if (fixationOn) {
					drawFixation((TrialContext)context);
				}
				if (markerOn) {
					marker.draw(context);
				} else {
					marker.drawAllOff(context);
				}
			}}, context);
	}

	@Override
	public void drawTask(Context context, final boolean fixationOn) {
		// clear the whole screen before define view ports in renderer
		blankScreen.draw(null);
		renderer.draw(new Drawable() {
			public void draw(Context context) {
				drawStimulus(context);
				if (fixationOn) {
					drawFixation((TrialContext)context);
				}
				marker.draw(context);
			}}, context);
	}

	@Override
	public void initGL(int w, int h) {
		width = w;
		height = h;
		GL11.glClearColor(0.5f, 0.5f, 0.5f, 1f);
	}

	@Override
	public void nextMarker() {
		marker.next();
	}
	
	@Override
	public void trialStart(TrialContext context) {
		if(XmlUtil.slideIsStereo(context.getCurrentTask())) {
			renderer = stereoRenderer;
		} else {
			renderer = monoRenderer;
		}
		renderer.init(width, height);
	}
	
	@Override
	public void trialStop(TrialContext context) {
	}
	
	protected abstract void drawFixation (TrialContext context);
	
	public Drawable getBlankScreen() {
		return blankScreen;
	}

	public void setBlankScreen(Drawable blankScreen) {
		this.blankScreen = blankScreen;
	}
	
	public ScreenMarker getMarker() {
		return marker;
	}

	public void setMarker(ScreenMarker marker) {
		this.marker = marker;
	}

	public AbstractRenderer getStereoRenderer() {
		return stereoRenderer;
	}

	public void setStereoRenderer(AbstractRenderer stereoRenderer) {
		this.stereoRenderer = stereoRenderer;
	}

	public AbstractRenderer getMonoRenderer() {
		return monoRenderer;
	}

	public void setMonoRenderer(AbstractRenderer monoRenderer) {
		this.monoRenderer = monoRenderer;
	}

}

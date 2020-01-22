package org.xper.drawing;

import org.lwjgl.opengl.GL11;

public class MatchChoiceGLUtil {
	public static void drawTarget(Drawable target, double x, double y, float r, float g, float b) {
		GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT);
		GL11.glColor3f(r, g, b);
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, 0.0);
		target.draw(null);
		GL11.glPopMatrix();
		GL11.glPopAttrib();
	}
}

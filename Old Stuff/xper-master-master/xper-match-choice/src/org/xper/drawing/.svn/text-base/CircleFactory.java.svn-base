package org.xper.drawing;

import org.xper.drawing.Drawable;
import org.xper.drawing.object.Circle;


public class CircleFactory implements DrawableFactory {

	public Drawable getDrawable(boolean solid, double size) {
		Circle circle = new Circle();
		circle.setRadius(size/2);
		circle.setSolid(solid);
		return circle;
	}
	
}

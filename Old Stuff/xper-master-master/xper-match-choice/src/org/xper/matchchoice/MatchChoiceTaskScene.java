package org.xper.matchchoice;

import org.xper.drawing.Context;

public interface MatchChoiceTaskScene {

	public void drawAllTargets(Context context);
	public void drawSelectedTarget(Context context, int sel);

}
package org.xper.example.match;

import org.xper.drawing.RGBColor;
import org.xper.experiment.StimSpecGenerator;
import org.xper.rfplot.GaborSpec;

public class MatchGaborSpecGenerator implements StimSpecGenerator {
	static RGBColor targetColors [] = {
		new RGBColor(1, 0, 0),
		new RGBColor(1, 1, 0),
		new RGBColor(0, 1, 1),
		new RGBColor(0, 0, 1)
	};
	
	public static MatchGaborSpec generate () {
		MatchGaborSpec g = new MatchGaborSpec();
		int n = (int)(Math.random() * 4) + 2;
		int matchIndex = (int)(Math.random() * n);
		if (matchIndex == 0) matchIndex = -1;
		g.setMatchIndex(matchIndex);
		GaborSpec s = new GaborSpec();
		for (int i = 0; i < n; i ++) {
			if (matchIndex > 0 && i == matchIndex) {
				s.setSize(50);
				g.addObjectSpec(new GaborSpec(s));
			} else {
				s.setXCenter(0);
				s.setYCenter(0);
				s.setSize(100);
				s.setOrientation(Math.random() * Math.PI);
				s.setFrequency(0.02);
				s.setPhase(Math.PI);
				s.setAnimation(true);
				g.addObjectSpec(new GaborSpec(s));
			}
		}
		for (int i = 0; i < n-1; i ++) {
			g.addTargetColor(targetColors[i]);
		}
		
		
		return g;
	}

	public String generateStimSpec() {
		return MatchGaborSpecGenerator.generate().toXml();
	}
}

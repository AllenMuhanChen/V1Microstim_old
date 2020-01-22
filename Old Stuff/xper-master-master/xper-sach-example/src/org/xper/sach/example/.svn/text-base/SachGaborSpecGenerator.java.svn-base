package org.xper.sach.example;

import org.xper.drawing.Coordinates2D;
import org.xper.experiment.StimSpecGenerator;
import org.xper.rfplot.GaborSpec;

public class SachGaborSpecGenerator implements StimSpecGenerator {
	
	public static SachGaborSpec generate () {
		SachGaborSpec g = new SachGaborSpec();
		int n = (int)(Math.random() * 4) + 2;
		
		GaborSpec s = new GaborSpec();
		for (int i = 0; i < n; i ++) {
			s.setXCenter(0);
			s.setYCenter(0);
			s.setSize(100);
			s.setOrientation(Math.random() * Math.PI);
			s.setFrequency(0.02);
			s.setPhase(Math.PI);
			s.setAnimation(true);
			g.addObjectSpec(new GaborSpec(s));
		}
		double x = Math.random() * 10 - 5;
		double y = 5;
		Coordinates2D targetPosition = new Coordinates2D(x, y);
		g.setTargetPosition(targetPosition);
		g.setTargetEyeWinSize(2);
		g.setReward((long)(Math.random() * 100 + 100));
		g.setTargetIndex(n-1);
		
		return g;
	}

	public String generateStimSpec() {
		return SachGaborSpecGenerator.generate().toXml();
	}
}

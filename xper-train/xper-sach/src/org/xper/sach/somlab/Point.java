package org.xper.sach.somlab;



public class Point {
	
	public int ch;
	public int id;
	public int delay;
	public int amp;
	public int sw;

	public  Point(Integer ch, Integer id, Integer delay, Integer amp, Integer sw){
		this.ch = ch;
		this.id = id;
		this.delay = delay;
		this.amp = amp;
		this.sw = sw;

	}
	
	public  Point(){
		this.ch = 0;
		this.id = 0;
		this.delay = 0;
		this.amp = 0;
		this.sw = 0;

	}
};

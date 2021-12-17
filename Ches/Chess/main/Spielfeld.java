package main;

import java.awt.Rectangle;

import figures.Figur;

public class Spielfeld {
	
	public static int width;
	public int x;
	public int y;
	public boolean marked = false;
	public boolean attackable = false;
	public String name;
	public Rectangle bounds;
	public Figur figur;
	
	public Spielfeld(int _x, int _y, Figur _figur, Rectangle _bounds, String _name) {
		super();
		this.x = _x;
		this.y = _y;
		this.figur = _figur;
		this.bounds = _bounds;
		this.name = _name;
	}
}

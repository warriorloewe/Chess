package main;

import java.awt.Rectangle;

import figures.Figur;

public class Spielfeld {
	public int x;
	public int y;
	public static int width;
	public Figur figur;
	public Rectangle bounds;
	public String name;
	public boolean marked = false;
	public boolean attackable = false;
	public Spielfeld(int _x, int _y, Figur _figur, Rectangle _bounds, String _name) {
		super();
		this.x = _x;
		this.y = _y;
		this.figur = _figur;
		this.bounds = _bounds;
		this.name = _name;
	}
}

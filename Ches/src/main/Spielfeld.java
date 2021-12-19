package main;

import java.awt.Rectangle;
import figures.Figure;

public class Spielfeld {
	/*
	 * only used for storing data
	 */
	
	public static int width;
	public int x;
	public int y;
	public boolean marked = false;
	public boolean attackable = false;
	public String name;
	public Rectangle bounds;
	public Figure figure;
	
	public Spielfeld(int _x, int _y, Figure _figure, Rectangle _bounds, String _name) {
		super();
		this.x = _x;
		this.y = _y;
		this.figure = _figure;
		this.bounds = _bounds;
		this.name = _name;
	}
}

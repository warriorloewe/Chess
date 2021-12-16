package main;
import figures.*;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

public class GameEnvironment implements Runnable, MouseListener, MouseMotionListener {
	
	public int width = 8;
	public boolean whitesMove = true;
	public boolean dragNDrop = false;
	public ArrayList<Figur> blackFigures;
	public ArrayList<Figur> whiteFigures;
	public Spielfeld[][] map;
	public static Spielfeld selectedField;
	public static Figur selectedFigur;
	
	SchachComponent sc;
	public GameEnvironment(SchachComponent _sc) {
		super();
		this.sc = _sc;
		Spielfeld.width = 1000/width;
		map = new Spielfeld[width][width];
		for(int i = 0; i < width; i++) {
			for(int j = 0; j < width; j++) {
				// a - h = 97 - 104
				String name = (char) (97 + j) + String.valueOf(8 - i);
				map[i][j] = new Spielfeld(j, i, null, new Rectangle(j * Spielfeld.width, i * Spielfeld.width, Spielfeld.width, Spielfeld.width), name);
			}
		}
		for(int i = 0; i < 8; i++) {
			map[1][i].figur = new Pawn(i, 1, "black", this);
			map[6][i].figur = new Pawn(i, 6, "white", this);
		}
		
		map[7][0].figur = new Rook(0, 7, "white", this);
		map[7][1].figur = new Knight(1, 7, "white", this);
		map[7][2].figur = new Bishop(2, 7, "white", this);
		map[7][3].figur = new Queen(3, 7, "white", this);
		map[7][4].figur = new King(4, 7, "white", this);
		map[7][5].figur = new Bishop(5, 7, "white", this);
		map[7][6].figur = new Knight(6, 7, "white", this);
		map[7][7].figur = new Rook(7, 7, "white", this);
		
		map[0][0].figur = new Rook(0, 0, "black", this);
		map[0][1].figur = new Knight(1, 0, "black", this);
		map[0][2].figur = new Bishop(2, 0, "black", this);
		map[0][3].figur = new Queen(3, 0, "black", this);
		map[0][4].figur = new King(4, 0, "black", this);
		map[0][5].figur = new Bishop(5, 0, "black", this);
		map[0][6].figur = new Knight(6, 0, "black", this);
		map[0][7].figur = new Rook(7, 0, "black", this);		
		
		sc.map = map;
		whiteFigures = new ArrayList<Figur>();
		blackFigures = new ArrayList<Figur>();
		for(int i = 0; i < map[0].length; i++) {
			for(int j = 0; j < map.length; j++) {
				if(map[i][j].figur != null) {
					if(map[i][j].figur.color == "white") {
						whiteFigures.add(map[i][j].figur);
					} else {
						blackFigures.add(map[i][j].figur);
					}
				}
			}
		}
		Thread run = new Thread(this);
		run.start();
	}
	
	public void move(Figur f, Spielfeld start, Spielfeld end) {
		start.figur = null;
		end.figur = f;
		f.x = end.x;
		f.y = end.y;
		selectedFigur = null;
		selectedField = null;
		f.moved = true;
		updateMarkers();
		updateEnPassant();
		if(f.name.contains("pawn")) {
			Pawn ff = (Pawn) f;
			if(Math.abs(start.y - end.y) == 2) {
				ff.enPassant = true;
			}
		} else if(f.name.contains("king")) {
			if(start.x - end.x == 2) {
				move(map[f.y][f.x-2].figur, map[f.y][f.x-2], map[f.y][f.x+1]);
				return;
			} else if(start.x - end.x == -2) {
				move(map[f.y][f.x+1].figur, map[f.y][f.x+1], map[f.y][f.x-1]);
				return;
			}
		}
		whitesMove = !whitesMove;
	}
	
	public void take(Figur f, Spielfeld start, Spielfeld end) {
		if(end.figur == null) { // en passant
			if(f.color == "white") {
				map[end.y+1][end.x].figur = null;
			} else {
				map[end.y-1][end.x].figur = null;
			}
		}
		if(f.color == "white") {
			blackFigures.remove(end.figur);
		} else {
			whiteFigures.remove(end.figur);
		}
		whitesMove = !whitesMove;
		start.figur = null;
		end.figur = f;
		f.x = end.x;
		f.y = end.y;
		selectedFigur = null;
		selectedField = null;
		f.moved = true;
		updateMarkers();
		updateEnPassant();
	}
	
	public void updateEnPassant() {
		for(Figur f : whiteFigures) {
			if(f.name.contains("pawn")) {
				Pawn ff = (Pawn) f;
				ff.enPassant = false;
			}
		}
		for(Figur f : blackFigures) {
			if(f.name.contains("pawn")) {
				Pawn ff = (Pawn) f;
				ff.enPassant = false;
			}
		}
	}
	
	public void updateMarkers() {
		for(int i = 0; i < map[0].length; i++) {
			for(int j = 0; j < map.length; j++) {
				map[i][j].marked = false;
				map[i][j].attackable = false;
			}
		}
		if(selectedFigur == null) {
			return;
		} else {
			for(Spielfeld sf : selectedFigur.getReachableFields()) {
				sf.marked = true;
			}
			for(Figur f : selectedFigur.getReachableEnemies()) {
				if(f.name.contains("pawn") && selectedFigur.name.contains("pawn")) {
					Pawn ff = (Pawn) f;
					if(selectedFigur.y - f.y == 0 && ff.enPassant) {
						if(selectedFigur.color == "white") {
							map[f.y-1][f.x].attackable = true;
						} else {
							map[f.y+1][f.x].attackable = true;
						}
					} else {
						map[f.y][f.x].attackable = true;
					}
				} else {
					map[f.y][f.x].attackable = true;
				}
			}
		}
	}
	
	
	@Override
	public void run() {
		while(true) {
			sc.repaint();
			try {
		        Thread.sleep(100);
		      } catch (InterruptedException e) {
		        e.printStackTrace();
		      }
		}
	}



	@Override
	public void mousePressed(MouseEvent e) {
		Point p = new Point(e.getX() - sc.offsetX, e.getY() - sc.offsetY);
		for(int i = 0; i < map[0].length; i++) {
			for(int j = 0; j < map.length; j++) {
				if(map[i][j].bounds.contains(p)) {
					if(map[i][j].marked) {
						move(selectedFigur, selectedField, map[i][j]);
					} else if(map[i][j].attackable) {
						take(selectedFigur, selectedField, map[i][j]);
					} else if(map[i][j].figur != null) {
						boolean rightMove = (whitesMove && map[i][j].figur.color == "white") || (!whitesMove && map[i][j].figur.color == "black");
						if(selectedField != map[i][j] && rightMove) {
							selectedField = map[i][j];
							selectedFigur = map[i][j].figur;
						} else {
							selectedField = null;
							selectedFigur = null;
						}
						updateMarkers();
					}
					return;
				}
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {}
	
	@Override
	public void mouseClicked(MouseEvent e) {}
	
	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}


	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}

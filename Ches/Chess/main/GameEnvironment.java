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
	public int timeLeftWhite;
	public int timeLeftBlack;
	public int mouseX = 0;
	public int mouseY = 0;
	public int lastMouseX = 0;
	public int lastMouseY = 0;
	public int increment;
	public boolean whitesMove = true;
	public boolean dragNDrop = false;
	public boolean gameOver = false;
	public ArrayList<Figur> blackFigures;
	public ArrayList<Figur> whiteFigures;
	public Spielfeld[][] map;
	public Spielfeld selectedField;
	public Figur selectedFigur;
	public King black_king;
	public King white_king;
	public String winner;
	public SchachComponent sc;
	
	public GameEnvironment(SchachComponent _sc, int time, int increment, boolean dad) {
		this.sc = _sc;
		this.timeLeftWhite = time;
		this.timeLeftBlack = time;
		this.increment = increment;
		this.dragNDrop = dad;
		Spielfeld.width = 1000/width;
		map = new Spielfeld[width][width];
		for(int i = 0; i < width; i++) {
			for(int j = 0; j < width; j++) {
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
		
		white_king = (King) map[7][4].figur;
		black_king = (King) map[0][4].figur;
		
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
	
	public boolean isLegal(Figur f, Spielfeld start, Spielfeld end) {
		boolean legal = true;
		boolean moved = f.moved;
		boolean castle = false;
		Figur endFigur = end.figur;
		start.figur = null;
		end.figur = f;
		f.x = end.x;
		f.y = end.y;
		f.moved = true;
		if(f.name.contains("king")) {
			if(start.x - end.x == 2) {
				map[f.y][f.x+1].figur = map[f.y][f.x-4].figur;
				map[f.y][f.x-4].figur = null;
				castle = true;
			} else if(start.x - end.x == -2) {
				map[f.y][f.x-1].figur = map[f.y][f.x+3].figur;
				map[f.y][f.x+3].figur = null;
				castle = true;
			}
		}
		if(f.color == "white") {
			for(Figur ff : blackFigures) {
				if(ff.isLongRange) {
					if(ff.canAttackKing(this.white_king) && (ff.x != end.x || ff.y != end.y)) {
						legal = false;
					}
				} else if(ff.canAttack(this.white_king) && (ff.x != end.x || ff.y != end.y)) {
					legal = false;
				}
			}
		} else {
			for(Figur ff : whiteFigures) {
				if(ff.isLongRange) {
					if(ff.canAttackKing(this.black_king) && (ff.x != end.x || ff.y != end.y)) {
						legal = false;
					}
				} else if(ff.canAttack(this.black_king) && (ff.x != end.x || ff.y != end.y)) {
					legal = false;
				}
			}
		}
		start.figur = f;
		end.figur = endFigur;
		f.x = start.x;
		f.y = start.y;
		f.moved = moved;
		if(castle) {
			if(start.x - end.x == 2) {
				map[f.y][f.x-2].figur = map[f.y][f.x+1].figur;
				map[f.y][f.x+1].figur = null;
			} else if(start.x - end.x == -2) {
				map[f.y][f.x+1].figur = map[f.y][f.x-1].figur;
				map[f.y][f.x-1].figur = null;
			}
		}
		return legal;
	}
	
	public void move(Figur f, Spielfeld start, Spielfeld end) {
		if(whitesMove) timeLeftWhite += increment;
		else timeLeftBlack += increment;
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
			if(Math.abs(start.y - end.y) == 2) ff.enPassant = true;
		} else if(f.name.contains("king")) {
			if(start.x - end.x == 2) {
				map[f.y][f.x+1].figur = map[f.y][f.x-2].figur;
				map[f.y][f.x-2].figur = null;
				map[f.y][f.x+1].figur.x = f.x+1;
				map[f.y][f.x+1].figur.moved = true;
			} else if(start.x - end.x == -2) {
				map[f.y][f.x-1].figur = map[f.y][f.x+1].figur;
				map[f.y][f.x+1].figur = null;
				map[f.y][f.x-1].figur.x = f.x-1;
				map[f.y][f.x-1].figur.moved = true;
			}
		}
		checkForMate();
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
		start.figur = null;
		end.figur = f;
		f.x = end.x;
		f.y = end.y;
		selectedFigur = null;
		selectedField = null;
		f.moved = true;
		updateMarkers();
		updateEnPassant();
		checkForMate();
		whitesMove = !whitesMove;
	}
	
	public void updateEnPassant() {
		for(Figur f : whiteFigures) {
			f.enPassant = false;
		}
		for(Figur f : blackFigures) {
			f.enPassant = false;
		}
	}
	
	public void updateMarkers() {
		for(int i = 0; i < map[0].length; i++) {
			for(int j = 0; j < map.length; j++) {
				map[i][j].marked = false;
				map[i][j].attackable = false;
			}
		}
		if(selectedFigur == null) return;
		else {
			for(Spielfeld sf : selectedFigur.getReachableFields()) {
				sf.marked = true;
			}
			for(Figur f : selectedFigur.getReachableEnemies()) {
				if(f.name.contains("pawn") && selectedFigur.name.contains("pawn") && selectedFigur.y - f.y == 0 && f.enPassant) {
					if(selectedFigur.color == "white") {
						map[f.y-1][f.x].attackable = true;
					} else {
						map[f.y+1][f.x].attackable = true;
					}
				} else {
					map[f.y][f.x].attackable = true;
				}
			}
		}
	}
	
	public void checkForMate() {
		if(whitesMove) {
			for(Figur f : whiteFigures) {
				if(f.canAttack(black_king)) {
					if(black_king.getReachableFields().size() > 0) {
						return;
					}
					for(Figur ff : blackFigures) {
						if(ff.getReachableEnemies().size() > 0 || ff.getReachableFields().size() > 0) {
							return;
						}
					}
					gameOver = true;
					winner = "white";
					return;
				}
			}
		} else {
			for(Figur f : blackFigures) {
				if(f.canAttack(black_king)) {
					if(black_king.getReachableFields().size() > 0) {
						return;
					}
					for(Figur ff : whiteFigures) {
						if(ff.getReachableEnemies().size() > 0 || ff.getReachableFields().size() > 0) {
							return;
						}
					}
					gameOver = true;
					winner = "black";
					return;
				}
			}
		}
	}
	
	public void run() {
		Timer timer = new Timer();
		timer.startTimer();
		int time = 0;
		while(true) {
			sc.repaint();
			if(timer.time > time && !gameOver) {
				time++;
				if(whitesMove) {
					timeLeftWhite--;
					if(timeLeftWhite <= 0) {
						gameOver = true;
						winner = "black";
					}
				}
				else {
					timeLeftBlack--;
					if(timeLeftBlack <= 0) {
						gameOver = true;
						winner = "white";
					}
				}
			}
			try {
		        Thread.sleep(20);
		      } catch (InterruptedException e) {
		        e.printStackTrace();
		      }
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(gameOver) return;
		Point p = new Point(e.getX() - sc.offsetX, e.getY() - sc.offsetY);
		for(int i = 0; i < map[0].length; i++) {
			for(int j = 0; j < map.length; j++) {
				if(map[i][j].bounds.contains(p)) {
					if(dragNDrop) {
						if(map[i][j].figur != null) {
							if(whitesMove && map[i][j].figur.color == "white" || (!whitesMove && map[i][j].figur.color == "black")) {
								selectedFigur = map[i][j].figur;
								selectedField = map[i][j];
								mouseX = lastMouseX;
								mouseY = lastMouseY;
								updateMarkers();
							}
						}
						return;
					} else {
						if(map[i][j].marked) {
							move(selectedFigur, selectedField, map[i][j]);
						} else if(map[i][j].attackable) {
							take(selectedFigur, selectedField, map[i][j]);
						} else if(map[i][j].figur != null) {
							if(selectedField != map[i][j] && (whitesMove && map[i][j].figur.color == "white") || (!whitesMove && map[i][j].figur.color == "black")) {
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
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(gameOver || !dragNDrop) return;
		Point p = new Point(e.getX() - sc.offsetX, e.getY() - sc.offsetY);
		for(int i = 0; i < map[0].length; i++) {
			for(int j = 0; j < map.length; j++) {
				if(map[i][j].bounds.contains(p)) {
					if(map[i][j].marked) {
						move(selectedFigur, selectedField, map[i][j]);
					} else if(map[i][j].attackable) {
						take(selectedFigur, selectedField, map[i][j]);
					}
				}
			}
		}
		selectedFigur = null;
		selectedField = null;
		mouseX = 0;
		mouseY = 0;
		updateMarkers();
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
	}


	@Override
	public void mouseMoved(MouseEvent e) {
		lastMouseX = e.getX();
		lastMouseY = e.getY();
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {}
	
	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}
	
	protected void finalize() {
		System.out.println("object is garbage collected ");
	}
}

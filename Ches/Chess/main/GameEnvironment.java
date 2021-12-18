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
	public int fiftyMoveRule = -1;
	public boolean fiftyMoveRuleLastMoveWhite = true;
	public boolean whitesMove = true;
	public boolean dragNDrop = false;
	public boolean gameOver = false;
	public ArrayList<Figur> blackFigures;
	public ArrayList<Figur> whiteFigures;
	public ArrayList<Spielfeld[][]> boards;
	public Spielfeld[][] map;
	public Spielfeld selectedField;
	public Figur selectedFigur;
	public King black_king;
	public King white_king;
	public String winningReason;
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
			map[1][i].figur = new Pawn(i, 1, "black", this, "", false);
			map[6][i].figur = new Pawn(i, 6, "white", this, "", false);
		}
		
		map[7][0].figur = new Rook(0, 7, "white", this, "", false);
		map[7][1].figur = new Knight(1, 7, "white", this, "", false);
		map[7][2].figur = new Bishop(2, 7, "white", this, "", false);
		map[7][3].figur = new Queen(3, 7, "white", this, "", false);
		map[7][4].figur = new King(4, 7, "white", this, "", false);
		map[7][5].figur = new Bishop(5, 7, "white", this, "", false);
		map[7][6].figur = new Knight(6, 7, "white", this, "", false);
		map[7][7].figur = new Rook(7, 7, "white", this, "", false);
		
		map[0][0].figur = new Rook(0, 0, "black", this, "", false);
		map[0][1].figur = new Knight(1, 0, "black", this, "", false);
		map[0][2].figur = new Bishop(2, 0, "black", this, "", false);
		map[0][3].figur = new Queen(3, 0, "black", this, "", false);
		map[0][4].figur = new King(4, 0, "black", this, "", false);
		map[0][5].figur = new Bishop(5, 0, "black", this, "", false);
		map[0][6].figur = new Knight(6, 0, "black", this, "", false);
		map[0][7].figur = new Rook(7, 0, "black", this, "", false);		
		
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
					map[i][j].figur.uniqueId = map[i][j].name;
				}
			}
		}
		boards = new ArrayList<Spielfeld[][]>();
		addBoard();
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
		if(whitesMove) {
			timeLeftWhite += increment;
			if(fiftyMoveRuleLastMoveWhite){
				fiftyMoveRule++;
			}
		}
		else {
			timeLeftBlack += increment;
			if(!fiftyMoveRuleLastMoveWhite){
				fiftyMoveRule++;
			}
		}
		if(f.name.contains("pawn")) {
			fiftyMoveRule = 0;
			fiftyMoveRuleLastMoveWhite = whitesMove;
		}
		if(fiftyMoveRule >= 50) {
			gameOver = true;
			winningReason = "Draw, fifty moves have been made without capturing or advancing a pawn";
		}
		update(f, start, end);
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
		updateMarkers();
		updateEnPassant();
		checkRepetition();
	}
	
	public void update(Figur f, Spielfeld start, Spielfeld end) {
		start.figur = null;
		end.figur = f;
		f.x = end.x;
		f.y = end.y;
		selectedFigur = null;
		selectedField = null;
		f.moved = true;
		checkForMate();
		whitesMove = !whitesMove;
		
	}
	
	public void take(Figur f, Spielfeld start, Spielfeld end) {
		fiftyMoveRule = 0;
		fiftyMoveRuleLastMoveWhite = whitesMove;
		if(end.figur == null) { // en passant
			if(f.color == "white") {
				map[end.y+1][end.x].figur = null;
			} else {
				map[end.y-1][end.x].figur = null;
			}
		}
		update(f, start, end);
		if(f.color == "white") {
			blackFigures.remove(end.figur);
		} else {
			whiteFigures.remove(end.figur);
		}
		updateMarkers();
		updateEnPassant();
		checkRepetition();
	}
	
	public void checkRepetition() {
		int counter = 0;
		int boardNmbr = -1;
		for(Spielfeld[][] board : boards) {
			boardNmbr++;
			System.out.println("--------------------");
			System.out.println(boardNmbr + ", " + ((boardNmbr % 2 == 0) == whitesMove));
			if(!((boardNmbr % 2 == 0) == whitesMove)) {
				continue;
			}
			boolean same = true;
			for(int i = 0; i < map[0].length; i++) {
				for(int j = 0; j < map.length; j++) {
					if(map[i][j].figur != null && board[i][j].figur != null && same) {
						Figur f = map[i][j].figur;
						Figur ff = board[i][j].figur;
						boolean sameFigur = f.uniqueId.contains(ff.uniqueId);
						boolean sameState = (f.enPassant == ff.enPassant);
						boolean sameCastleRights = true;
						if(sameFigur && f instanceof King) {
							if(!(f.canCastleShort() == ff.canCastleShort() && f.canCastleLong() == ff.canCastleLong() && f.moved == ff.moved)) {
								sameCastleRights = false;
							}
						}
						same = sameFigur && sameState && sameCastleRights;
						if(!same) {
							System.out.println(map[i][j].name + ", " + sameFigur + ", " + sameState + ", " + sameCastleRights);
						}
					} else if((map[i][j].figur == null) != (board[i][j].figur == null)) {
						same = false;
						System.out.println(map[i][j].name + ", " + ((map[i][j].figur == null) != (board[i][j].figur == null)));
					}
				}
			}
			if(same) {
				counter++;
				System.out.println("Counter: " + counter);
			}
			System.out.println("--------------------");
		}
		if(counter >= 2) {
			gameOver = true;
			winningReason = "Draw by repetition";
		}
		addBoard();
	}
	
	public void addBoard() {
		Spielfeld[][] newMap = new Spielfeld[map.length][map[0].length];
		for(int i = 0; i < map[0].length; i++) {
			for(int j = 0; j < map.length; j++) {
				newMap[i][j] = new Spielfeld(j, i, null, map[i][j].bounds, map[i][j].name);
				if(map[i][j].figur != null) {
					Figur f = map[i][j].figur;
					if(f.name.contains("king")) {
						newMap[i][j].figur = new King(j, i, f.color, this, f.uniqueId, f.enPassant);
						newMap[i][j].figur.moved = f.moved;
					} else if (f.name.contains("rook")) {
						newMap[i][j].figur = new Rook(j, i, f.color, this, f.uniqueId, f.enPassant);
					} else if (f.name.contains("queen")) {
						newMap[i][j].figur = new Queen(j, i, f.color, this, f.uniqueId, f.enPassant);
					} else if (f.name.contains("bishop")) {
						newMap[i][j].figur = new Bishop(j, i, f.color, this, f.uniqueId, f.enPassant);
					} else if (f.name.contains("pawn")) {
						newMap[i][j].figur = new Pawn(j, i, f.color, this, f.uniqueId, f.enPassant);
					} else if (f.name.contains("knight")) {
						newMap[i][j].figur = new Knight(j, i, f.color, this, f.uniqueId, f.enPassant);
					}
				}

			}
		}
		boards.add(newMap);
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
					winningReason = "White won because of Check Mate";
					return;
				}
			}
		} else {
			for(Figur f : blackFigures) {
				if(f.canAttack(white_king)) {
					if(white_king.getReachableFields().size() > 0) {
						return;
					}
					for(Figur ff : whiteFigures) {
						if(ff.getReachableEnemies().size() > 0 || ff.getReachableFields().size() > 0) {
							return;
						}
					}
					gameOver = true;
					winningReason = "Black won because of Check Mate";
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

						winningReason = "Black won because whites time ran out";
					}
				}
				else {
					timeLeftBlack--;
					if(timeLeftBlack <= 0) {
						gameOver = true;
						winningReason = "White won because blacks time ran out";
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

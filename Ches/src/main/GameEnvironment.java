package main;

import figures.*;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class GameEnvironment implements Runnable, MouseListener, MouseMotionListener {
	
	public int width = 8;
	public int mouseX = 0;
	public int mouseY = 0;
	public int lastMouseX = 0;
	public int lastMouseY = 0;
	public int increment; // increment added everytime a move has been made
	public int fiftyMoveRule = -1; // counts how many moves have been made since a piece has been captured or a pawn has been moved
	
	public boolean fiftyMoveRuleLastMoveWhite = true; //  stores if white has made the last move interrupting the 50 move rule
	public boolean whitesMove = true;
	public boolean dragNDrop = false;
	public boolean gameOver = false;
	public boolean started = false;
	
	public ArrayList<Figure> blackFigures; //  stores all black pieces currently on the board
	public ArrayList<Figure> whiteFigures; //  stores all white pieces currently on the board
	public ArrayList<Spielfeld[][]> boards; // used for 3 fold repetition
	
	public Spielfeld[][] map; // current board
	public Spielfeld selectedField;
	public Figure selectedFigure;
	public King black_king;
	public King white_king;
	
	public String winningReason;
	public SchachComponent sc;
	public Timer blackTimer;
	public Timer whiteTimer;
	
	public GameEnvironment() {
		Spielfeld.width = 1000/width;
		map = new Spielfeld[width][width];
		/*
		 * board is created and every piece is put on its starting square
		 */
		for(int i = 0; i < width; i++) {
			for(int j = 0; j < width; j++) {
				map[i][j] = new Spielfeld(j, i, null, new Rectangle(j * Spielfeld.width, i * Spielfeld.width, Spielfeld.width, Spielfeld.width), (char) (97 + j) + String.valueOf(8 - i));
			}
		}
		for(int i = 0; i < 8; i++) {
			map[1][i].figure = new Pawn(i, 1, "black", this, "", false);
			map[6][i].figure = new Pawn(i, 6, "white", this, "", false);
		}
		
		map[7][0].figure = new Rook(0, 7, "white", this, "", false);
		map[7][1].figure = new Knight(1, 7, "white", this, "", false);
		map[7][2].figure = new Bishop(2, 7, "white", this, "", false);
		map[7][3].figure = new Queen(3, 7, "white", this, "", false);
		map[7][4].figure = new King(4, 7, "white", this, "", false);
		map[7][5].figure = new Bishop(5, 7, "white", this, "", false);
		map[7][6].figure = new Knight(6, 7, "white", this, "", false);
		map[7][7].figure = new Rook(7, 7, "white", this, "", false);
		
		map[0][0].figure = new Rook(0, 0, "black", this, "", false);
		map[0][1].figure = new Knight(1, 0, "black", this, "", false);
		map[0][2].figure = new Bishop(2, 0, "black", this, "", false);
		map[0][3].figure = new Queen(3, 0, "black", this, "", false);
		map[0][4].figure = new King(4, 0, "black", this, "", false);
		map[0][5].figure = new Bishop(5, 0, "black", this, "", false);
		map[0][6].figure = new Knight(6, 0, "black", this, "", false);
		map[0][7].figure = new Rook(7, 0, "black", this, "", false);		
		
		white_king = (King) map[7][4].figure;
		black_king = (King) map[0][4].figure;
		blackFigures = new ArrayList<Figure>();
		for(int i = 0; i < 2; i++) {
			for(int j = 0; j < map.length; j++) {
				blackFigures.add(map[i][j].figure);
				map[i][j].figure.uniqueId = map[i][j].name;
			}
		}
		whiteFigures = new ArrayList<Figure>();
		for(int i = 6; i < 8; i++) {
			for(int j = 0; j < map.length; j++) {
				whiteFigures.add(map[i][j].figure);
				map[i][j].figure.uniqueId = map[i][j].name;
			}
		}
		boards = new ArrayList<Spielfeld[][]>();
		addBoard();
		Thread run = new Thread(this);
		run.start();
	}
	
	public boolean isLegal(Figure f, Spielfeld start, Spielfeld end) {
		/*
		 * this function actually plays the move on the board
		 * after that it checks if any enemy piece can attacking the king
		 * if so the move is illegal
		 * after checking move legality it returns to the original position
		 */
		boolean legal = true;
		boolean moved = f.moved;
		boolean castle = false;
		Figure endFigur = end.figure;
		start.figure = null;
		end.figure = f;
		f.x = end.x;
		f.y = end.y;
		f.moved = true;
		if(f.name.contains("king")) {
			if(start.x - end.x == 2) {
				map[f.y][f.x+1].figure = map[f.y][f.x-4].figure;
				map[f.y][f.x-4].figure = null;
				castle = true;
			} else if(start.x - end.x == -2) {
				map[f.y][f.x-1].figure = map[f.y][f.x+3].figure;
				map[f.y][f.x+3].figure = null;
				castle = true;
			}
		}
		if(f.color == "white") {
			for(Figure ff : blackFigures) {
				if(ff.isLongRange) {
					/*
					 * using an extra function for long range pieces because
					 * their canAttack() has a legality check in it 
					 * leading to a stackoverflow
					 */
					if(ff.canAttackKing(this.white_king) && (ff.x != end.x || ff.y != end.y)) {
						legal = false;
					}
				} else if(ff.canAttack(this.white_king) && (ff.x != end.x || ff.y != end.y)) {
					legal = false;
				}
			}
		} else {
			for(Figure ff : whiteFigures) {
				if(ff.isLongRange) {
					if(ff.canAttackKing(this.black_king) && (ff.x != end.x || ff.y != end.y)) {
						legal = false;
					}
				} else if(ff.canAttack(this.black_king) && (ff.x != end.x || ff.y != end.y)) {
					legal = false;
				}
			}
		}
		start.figure = f;
		end.figure = endFigur;
		f.x = start.x;
		f.y = start.y;
		f.moved = moved;
		if(castle) {
			if(start.x - end.x == 2) {
				map[f.y][f.x-2].figure = map[f.y][f.x+1].figure;
				map[f.y][f.x+1].figure = null;
			} else if(start.x - end.x == -2) {
				map[f.y][f.x+1].figure = map[f.y][f.x-1].figure;
				map[f.y][f.x-1].figure = null;
			}
		}
		return legal;
	}
	
	public void move(Figure f, Spielfeld start, Spielfeld end) {
		if((whitesMove && fiftyMoveRuleLastMoveWhite) || (!whitesMove && !fiftyMoveRuleLastMoveWhite)) {
			fiftyMoveRule++;
		}
		if(f.name.contains("pawn")) {
			fiftyMoveRule = 0;
			fiftyMoveRuleLastMoveWhite = whitesMove;
		} else if(fiftyMoveRule >= 50) {
			gameOver = true;
			winningReason = "Draw, fifty moves have been made without capturing or advancing a pawn";
		}
		update(f, start, end);
	}
	
	public void take(Figure f, Spielfeld start, Spielfeld end) {
		fiftyMoveRule = 0;
		fiftyMoveRuleLastMoveWhite = whitesMove;
		if(end.figure == null) {
			/*
			 * en passant
			 */
			if(f.color == "white") {
				map[end.y+1][end.x].figure = null;
				blackFigures.remove(map[end.y+1][end.x].figure);
			} else {
				map[end.y-1][end.x].figure = null;
				whiteFigures.remove(map[end.y-1][end.x].figure);
			}
		} else {
			if(f.color.contains("white")) {
				blackFigures.remove(end.figure);
			} else {
				whiteFigures.remove(end.figure);
			}
		}
		update(f, start, end);
	}
	
	public void update(Figure f, Spielfeld start, Spielfeld end) {
		updateEnPassant();
		boolean promoted = false;
		if(f.name.contains("pawn")) {
			if((end.y == 7 && f.color == "black") || (end.y == 0 && f.color == "white")) {
				/*
				 * promotion
				 */
				boolean chosen = false;
				while(!chosen) {
					String answer = JOptionPane.showInputDialog("Promotion: Knight/Bishop/Rook/Queen", "Queen");
					chosen = true;
					if(answer.contains("k") || answer.contains("K") && !(answer.contains("r") || answer.contains("R"))) {
						end.figure = new Knight(end.x, end.y, f.color, this, String.valueOf(Math.random()), false);
					} else if(answer.contains("b") || answer.contains("B")) {
						end.figure = new Bishop(end.x, end.y, f.color, this, String.valueOf(Math.random()), false);
					} else if(answer.contains("r") || answer.contains("R")) {
						end.figure = new Rook(end.x, end.y, f.color, this, String.valueOf(Math.random()), false);
					} else if(answer.contains("q") || answer.contains("Q")) {
						end.figure = new Queen(end.x, end.y, f.color, this, String.valueOf(Math.random()), false);
					} else {
						chosen = false;
					}
				}
				promoted = true;
				if(f.color == "black") {
					blackFigures.remove(f);
					blackFigures.add(end.figure);
				} else {
					whiteFigures.remove(f);
					whiteFigures.add(end.figure);
				}
			} else if(Math.abs(start.y - end.y) == 2) {
				f.enPassant = true;
			}
		} else if(f.name.contains("king")) {
			/*
			 * this updates the rook if the king has castled
			 */
			if(start.x - end.x == 2) {
				map[f.y][f.x-1].figure = map[f.y][f.x-4].figure;
				map[f.y][f.x-4].figure = null;
				map[f.y][f.x-1].figure.x = f.x-1;
				map[f.y][f.x-1].figure.moved = true;
			} else if(start.x - end.x == -2) {
				map[f.y][f.x+1].figure = map[f.y][f.x+3].figure;
				map[f.y][f.x+3].figure = null;
				map[f.y][f.x+1].figure.x = f.x+1;
				map[f.y][f.x+1].figure.moved = true;
			}
		}
		if(!promoted) {
			end.figure = f;
			f.x = end.x;
			f.y = end.y;
			f.moved = true;
		}
		start.figure = null;
		selectedFigure = null;
		selectedField = null;
		checkForMate();
		checkForStalemate();
		if(whitesMove) {
			whiteTimer.time += increment;
			blackTimer.running = true;
			whiteTimer.running = false;
		}
		else {
			blackTimer.time += increment;
			blackTimer.running = false;
			whiteTimer.running = true;
		}
		whitesMove = !whitesMove;
		updateMarkers();
		checkRepetition();
	}
	
	public void checkRepetition() {
		/*
		 * goes through every single board of this match and compares it to the current board
		 * if two matching boards have been found this position has occured three times
		 * this results in a draw
		 */
		int counter = 0;
		int boardNmbr = -1;
		for(Spielfeld[][] board : boards) {
			boardNmbr++;
			if(!((boardNmbr % 2 == 0) == whitesMove)) {
				/*
				 * only checks boards that have the same player moving
				 */
				continue;
			}
			boolean same = true;
			for(int i = 0; i < map[0].length; i++) {
				for(int j = 0; j < map.length; j++) {
					if(map[i][j].figure != null && board[i][j].figure != null && same) {
						/*
						 * if same has been set to false once it doesnt go in here anymore so it cant be overwritten
						 */
						Figure f = map[i][j].figure;
						Figure ff = board[i][j].figure;
						boolean sameFigure = f.uniqueId.contains(ff.uniqueId);
						boolean sameState = (f.enPassant == ff.enPassant);
						boolean sameCastleRights = true;
						if(sameFigure && f instanceof King) {
							if(!f.moved) {
								if(!(f.canCastleShort() == ff.canCastleShort() && f.canCastleLong() == ff.canCastleLong() && f.moved == ff.moved)) {
									sameCastleRights = false;
								}
							} else if(!f.moved == ff.moved){
								sameState = false;
							}
						}
						same = sameFigure && sameState && sameCastleRights;
					} else if((map[i][j].figure == null) != (board[i][j].figure == null)) {
						same = false;
					}
				}
			}
			if(same) {
				counter++;
			}		
		}
		if(counter >= 2) {
			gameOver = true;
			winningReason = "Draw by repetition";
		}
		addBoard();
	}
	
	public void addBoard() {
		/*
		 * records current board position in the boards arraylist
		 * used for 3 fold repetition
		 */
		Spielfeld[][] newMap = new Spielfeld[map.length][map[0].length];
		for(int i = 0; i < map[0].length; i++) {
			for(int j = 0; j < map.length; j++) {
				newMap[i][j] = new Spielfeld(j, i, null, map[i][j].bounds, map[i][j].name);
				if(map[i][j].figure != null) {
					Figure f = map[i][j].figure;
					if(f.name.contains("king")) {
						newMap[i][j].figure = new King(j, i, f.color, this, f.uniqueId, f.enPassant);
						newMap[i][j].figure.moved = f.moved;
					} else if (f.name.contains("rook")) {
						newMap[i][j].figure = new Rook(j, i, f.color, this, f.uniqueId, f.enPassant);
					} else if (f.name.contains("queen")) {
						newMap[i][j].figure = new Queen(j, i, f.color, this, f.uniqueId, f.enPassant);
					} else if (f.name.contains("bishop")) {
						newMap[i][j].figure = new Bishop(j, i, f.color, this, f.uniqueId, f.enPassant);
					} else if (f.name.contains("pawn")) {
						newMap[i][j].figure = new Pawn(j, i, f.color, this, f.uniqueId, f.enPassant);
					} else if (f.name.contains("knight")) {
						newMap[i][j].figure = new Knight(j, i, f.color, this, f.uniqueId, f.enPassant);
					}
				}

			}
		}
		boards.add(newMap);
	}
	
	public void updateEnPassant() {
		for(Figure f : whiteFigures) {
			f.enPassant = false;
		}
		for(Figure f : blackFigures) {
			f.enPassant = false;
		}
	}
	
	public void updateMarkers() {
		/*
		 * first clears all markers then if the selected figure isnt null
		 * it set the fields the figure can move to to marked
		 * and the ones it can attack to attackable
		 */
		for(int i = 0; i < map[0].length; i++) {
			for(int j = 0; j < map.length; j++) {
				map[i][j].marked = false;
				map[i][j].attackable = false;
			}
		}
		if(selectedFigure == null) return;
		for(Spielfeld sf : selectedFigure.getReachableFields()) {
			sf.marked = true;
		}
		for(Figure f : selectedFigure.getReachableEnemies()) {
			if(f.name.contains("pawn") && selectedFigure.name.contains("pawn") && selectedFigure.y - f.y == 0 && f.enPassant) {
				/*
				 * checks for en passant
				 */
				if(selectedFigure.color == "white") {
					map[f.y-1][f.x].attackable = true;
				} else {
					map[f.y+1][f.x].attackable = true;
				}
			} else {
				map[f.y][f.x].attackable = true;
			}
		}
	}
	
	public void checkForStalemate() {
		/*
		 * if the king is in check or any other piece can make a move its not a stale mate
		 */
		if(whitesMove) {
			for(Figure f : whiteFigures) {
				if(f.canAttack(black_king)) {
					return;
				}
			}
			for(Figure ff : blackFigures) {
				if(ff.getReachableEnemies().size() > 0 || ff.getReachableFields().size() > 0) {
					return;
				}
			}
			gameOver = true;
			winningReason = "Stalemate";
		} else {
			for(Figure f : blackFigures) {
				if(f.canAttack(white_king)) {
					return;
				}
			}
			for(Figure ff : whiteFigures) {
				if(ff.getReachableEnemies().size() > 0 || ff.getReachableFields().size() > 0) {
					return;
				}
			}
			gameOver = true;
			winningReason = "Draw by Stalemate";
		}
	}
	
	public void checkForMate() {
		/*
		 * if the enemy king is in check and none of its pieces can make a legal move its checkamte
		 */
		if(whitesMove) {
			for(Figure f : whiteFigures) {
				if(f.canAttack(black_king)) {
					for(Figure ff : blackFigures) {
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
			for(Figure f : blackFigures) {
				if(f.canAttack(white_king)) {
					for(Figure ff : whiteFigures) {
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
	
	public void getInputs() {
		blackTimer = new Timer(-1);
		while(blackTimer.time <= 0) {
			blackTimer = new Timer(Integer.valueOf(JOptionPane.showInputDialog("Time in seconds:", 300)) * 1000);
		}
		whiteTimer = new Timer(blackTimer.time);
		increment = -1;
		while(increment < 0) {
			increment = Integer.valueOf(JOptionPane.showInputDialog("Increment in seconds:", 5)) * 1000;
		}
		if(JOptionPane.showConfirmDialog(null, "Drag and Drop on?", "Drag and Drop", 0, 3) == 0) {
			dragNDrop = true;
		}
	}
	
	public void run() {
		getInputs();
		whiteTimer.running = true;
		while(true) {
			sc.repaint();
			if(gameOver) {
				whiteTimer.running = false;
				blackTimer.running = false;
				JOptionPane.showMessageDialog(null, winningReason);
				System.exit(0);
			}
			if(whitesMove) {
				if(whiteTimer.time <= 0) {
					gameOver = true;
					winningReason = "Black won because whites time ran out";
				}
			}
			else {
				if(blackTimer.time <= 0) {
					gameOver = true;
					winningReason = "White won because blacks time ran out";
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
					/*
					 * finds the square containing the mouse click
					 * if the figure isnt null and drag and dropis active the figure is set to the active one
					 */
					if(dragNDrop) {
						if(map[i][j].figure != null) {
							if(whitesMove && map[i][j].figure.color == "white" || (!whitesMove && map[i][j].figure.color == "black")) {
								selectedFigure = map[i][j].figure;
								selectedField = map[i][j];
								mouseX = lastMouseX;
								mouseY = lastMouseY;
								updateMarkers();
							}
						}
						return;
					}
					/*
					 * if drag and drop isnt active you can either click on 
					 * a square the selected piece can move to
					 * a square the selected piece can attack 
					 * select or deselect a figure
					 * or click on an empty square
					 */
					if(map[i][j].marked) {
						move(selectedFigure, selectedField, map[i][j]);
					} else if(map[i][j].attackable) {
						take(selectedFigure, selectedField, map[i][j]);
					} else if(map[i][j].figure != null) {
						if(selectedField != map[i][j] && (whitesMove && map[i][j].figure.color == "white") || (!whitesMove && map[i][j].figure.color == "black")) {
							selectedField = map[i][j];
							selectedFigure = map[i][j].figure;
						} else {
							selectedField = null;
							selectedFigure = null;
						}
						updateMarkers();
					}
					return;
				}
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		/*
		 * if drag and drop isnt active this has no if effect
		 * otherwise you can either release on
		 * a square the selected piece can move to
		 * a square the selected piece can attack 
		 * or on an empty square which resets the piece back to its original position
		 */
		if(gameOver || !dragNDrop) return;
		Point p = new Point(e.getX() - sc.offsetX, e.getY() - sc.offsetY);
		for(int i = 0; i < map[0].length; i++) {
			for(int j = 0; j < map.length; j++) {
				if(map[i][j].bounds.contains(p)) {
					if(map[i][j].marked) {
						move(selectedFigure, selectedField, map[i][j]);
						break;
					} else if(map[i][j].attackable) {
						take(selectedFigure, selectedField, map[i][j]);
						break;
					}
				}
			}
		}
		selectedFigure = null;
		selectedField = null;
		mouseX = 0;
		mouseY = 0;
		updateMarkers();
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		/*
		 * is called when the mouse is moved while pressing down mouse button
		 */
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
}

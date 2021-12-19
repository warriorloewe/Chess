package figures;

import java.util.ArrayList;

import main.GameEnvironment;
import main.Spielfeld;

public abstract class Figure {
	
	public int x;
	public int y;
	public int[][] directions = {{-1, -1}, {1, -1}, {-1, 1}, {1, 1}, {0, 1}, {0, -1}, {1, 0}, {-1, 0}}; // stores the 8 possible different direction when on a grid
	public boolean moved = false;
	public boolean isLongRange = false; // true for bishop rook and queen
	public boolean enPassant = false; // only used with pawns
	public String color;
	public String name;
	public String uniqueId; // for all normal figures its the starting square, for pawns after promoting its a random number between 0 and 1
	public GameEnvironment ge;
	public Figure(int _x, int _y, String _color, String _name, GameEnvironment _ge, String _uniqueId, boolean _enPassant) {
		this.x = _x;
		this.y = _y;
		this.color = _color;
		this.name = _name;
		this.ge = _ge;
		this.uniqueId = _uniqueId;
		this.enPassant = _enPassant;
	}
	
	public abstract boolean canAttack(Figure f);
	public abstract boolean canAttackKing(Figure f);
	public abstract boolean canReach(Spielfeld sf);
	
	public ArrayList<Spielfeld> checkDirection(int xDirection, int yDirection) {
		/*
		 * moves in a certain direction on the game board 
		 * returns all possible and legal free squares
		 * until it either hits the edge or another piece
		 */
		int xx = this.x + xDirection;
		int yy = this.y + yDirection;
		ArrayList<Spielfeld> reachableFields = new ArrayList<Spielfeld>();
		while(!(xx < 0 || xx > 7 || yy < 0 || yy > 7)) {
			if(this.ge.map[yy][xx].figure == null) {
				if(this.ge.isLegal(this, this.ge.map[this.y][this.x], this.ge.map[yy][xx])) {
					reachableFields.add(this.ge.map[yy][xx]);
				}
			} else {
				return reachableFields;
			}
			xx += xDirection;
			yy += yDirection;
		}
		return reachableFields;
	}
	
	public Figure checkAttackable(int xDirection, int yDirection) {
		/*
		 * moves in a certain direction on the game board 
		 * until it either hits the edge or another piece
		 * returns null if the piece is friendly or if it reaches the edge
		 */
		int xx = this.x + xDirection;
		int yy = this.y + yDirection;
		while(!(xx < 0 || xx > 7 || yy < 0 || yy > 7)) {
			if(this.ge.map[yy][xx].figure != null) {
				return this.ge.map[yy][xx].figure.color == this.color || !this.ge.isLegal(this, this.ge.map[this.y][this.x], this.ge.map[yy][xx]) ? null : this.ge.map[yy][xx].figure;
			}
			xx += xDirection;
			yy += yDirection;
		}
		return null;
	}
	
	public boolean checkAttackableKing(int xDirection, int yDirection) {
		/*
		 * same as last func but also returns null if the enemy piece is not a king
		 * important it doesnt use a legality check since this function is only used in legality checks itself
		 */
		int xx = this.x + xDirection;
		int yy = this.y + yDirection;
		while(!(xx < 0 || xx > 7 || yy < 0 || yy > 7)) {
			if(this.ge.map[yy][xx].figure != null) {
				return !(this.ge.map[yy][xx].figure.color == this.color || !this.ge.map[yy][xx].figure.name.contains("king"));
			}
			xx += xDirection;
			yy += yDirection;
		}
		return false;
	}
	
	public ArrayList<Spielfeld> getReachableFields() {
		ArrayList<Spielfeld> reachableFields = new ArrayList<Spielfeld>();
		for(int i = 0; i < this.ge.map[0].length; i++) {
			for(int j = 0; j < this.ge.map.length; j++) {
				if(this.canReach(this.ge.map[i][j]) && this.ge.map[i][j].figure == null) {
					if(ge.isLegal(this, ge.map[this.y][this.x], ge.map[i][j])) {
						reachableFields.add(this.ge.map[i][j]);
					}
				}
			}
		}
		if(this.name.contains("king")) {
			reachableFields.addAll(checkForCastle());
		}
		return reachableFields;
	}
	
	public ArrayList<Figure> getReachableEnemies() {
		ArrayList<Figure> reachableEnemies = new ArrayList<Figure>();
		if(this.color == "white") {
			for(Figure f : this.ge.blackFigures) {
				if(canAttack(f)) {
					if(ge.isLegal(this, ge.map[this.y][this.x], ge.map[f.y][f.x])) {
						reachableEnemies.add(f);
					}
				}
			}
		} else {
			for(Figure f : this.ge.whiteFigures) {
				if(canAttack(f)) {
					if(ge.isLegal(this, ge.map[this.y][this.x], ge.map[f.y][f.x])) {
						reachableEnemies.add(f);
					}
				}
			}
		}
		return reachableEnemies;
	}
	
	public ArrayList<Spielfeld> checkForCastle() {
		/*
		 * only used with kings
		 * return an empty list if the king is attacked or has moved before
		 * returns the square two to the left of the king if long castling is possible
		 * and/or the square two to the right of the king if short castling is possible
		 */
		ArrayList<Spielfeld> reachableFields = new ArrayList<Spielfeld>();
		if(this.moved) return reachableFields;
		if(this.color.contains("white")) {
			for(Figure f : this.ge.blackFigures) {
				if(f.canAttack(this)) {
					return reachableFields;
				}
			}
		} else {
			for(Figure f : this.ge.whiteFigures) {
				if(f.canAttack(this)) {
					return reachableFields;
				}
			}
		}
		boolean longC = true;
		boolean shortC = true;
		/*
		 * when castling the king cant pass through any squares that are in check
		 */
		if(this.color == "white") {
			for(Figure f : this.ge.blackFigures) {
				if(f.canReach(this.ge.map[this.y][this.x-1]) || f.canReach(this.ge.map[this.y][this.x-2])) {
					longC = false;
				}
				if(f.canReach(this.ge.map[this.y][this.x+1]) || f.canReach(this.ge.map[this.y][this.x+2])) {
					shortC = false;
				}
			}
		} else {
			for(Figure f : this.ge.whiteFigures) {
				if(f.canReach(this.ge.map[this.y][this.x-1]) || f.canReach(this.ge.map[this.y][this.x-2])) {
					longC = false;
				}
				if(f.canReach(this.ge.map[this.y][this.x+1]) || f.canReach(this.ge.map[this.y][this.x+2])) {
					shortC = false;
				}
			}
		}
		if(canCastleLong() && longC) reachableFields.add(this.ge.map[this.y][this.x-2]);
		if(canCastleShort() && shortC) reachableFields.add(this.ge.map[this.y][this.x+2]);
		return reachableFields;
	}
	
	public boolean canCastleLong() {
		boolean rook = false;
		Spielfeld f = this.ge.map[this.y][this.x-4];
		if(f.figure != null) {
			if(f.figure.name.contains(this.color + "_rook") && !f.figure.moved) {
				rook = true;
			}
		}
		return rook && this.ge.map[this.y][this.x-1].figure == null && this.ge.map[this.y][this.x-2].figure == null && this.ge.map[this.y][this.x-3].figure == null;
	}
	
	public boolean canCastleShort() {
		boolean rook = false;
		Spielfeld f = this.ge.map[this.y][this.x+3];
		if(f.figure != null) {
			if(f.figure.name.contains(this.color + "_rook") && !f.figure.moved) {
				rook = true;
			}
		}
		return rook && this.ge.map[this.y][this.x+1].figure == null && this.ge.map[this.y][this.x+2].figure == null;
	}
}

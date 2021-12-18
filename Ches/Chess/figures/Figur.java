package figures;

import java.util.ArrayList;

import main.GameEnvironment;
import main.Spielfeld;

public abstract class Figur {
	
	public int x;
	public int y;
	public int[][] directions = {{-1, -1}, {1, -1}, {-1, 1}, {1, 1}, {0, 1}, {0, -1}, {1, 0}, {-1, 0}};
	public boolean moved = false;
	public boolean isLongRange = false;
	public boolean enPassant = false;
	public String color;
	public String name;
	public GameEnvironment ge;
	public String uniqueId;
	public Figur(int _x, int _y, String _color, String _name, GameEnvironment _ge, String _uniqueId, boolean _enPassant) {
		this.x = _x;
		this.y = _y;
		this.color = _color;
		this.name = _name;
		this.ge = _ge;
		this.uniqueId = _uniqueId;
		this.enPassant = _enPassant;
	}
	
	public abstract boolean canAttack(Figur f);
	public abstract boolean canAttackKing(Figur f);
	public abstract boolean canReach(Spielfeld sf);
	
	public ArrayList<Spielfeld> checkDirection(int xDirection, int yDirection) {
		int xx = this.x + xDirection;
		int yy = this.y + yDirection;
		ArrayList<Spielfeld> reachableFields = new ArrayList<Spielfeld>();
		while(!(xx < 0 || xx > 7 || yy < 0 || yy > 7)) {
			if(this.ge.map[yy][xx].figur == null) {
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
	
	public Figur checkAttackable(int xDirection, int yDirection) {
		int xx = this.x + xDirection;
		int yy = this.y + yDirection;
		while(!(xx < 0 || xx > 7 || yy < 0 || yy > 7)) {
			if(this.ge.map[yy][xx].figur != null) {
				return this.ge.map[yy][xx].figur.color == this.color || !this.ge.isLegal(this, this.ge.map[this.y][this.x], this.ge.map[yy][xx]) ? null : this.ge.map[yy][xx].figur;
			}
			xx += xDirection;
			yy += yDirection;
		}
		return null;
	}
	
	public boolean checkAttackableKing(int xDirection, int yDirection) {
		int xx = this.x + xDirection;
		int yy = this.y + yDirection;
		while(!(xx < 0 || xx > 7 || yy < 0 || yy > 7)) {
			if(this.ge.map[yy][xx].figur != null) {
				return !(this.ge.map[yy][xx].figur.color == this.color || !this.ge.map[yy][xx].figur.name.contains("king"));
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
				if(this.canReach(this.ge.map[i][j]) && this.ge.map[i][j].figur == null) {
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
	public ArrayList<Figur> getReachableEnemies() {
		ArrayList<Figur> reachableEnemies = new ArrayList<Figur>();
		if(this.color == "white") {
			for(Figur f : this.ge.blackFigures) {
				if(canAttack(f)) {
					if(ge.isLegal(this, ge.map[this.y][this.x], ge.map[f.y][f.x])) {
						reachableEnemies.add(f);
					}
				}
			}
		} else {
			for(Figur f : this.ge.whiteFigures) {
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
		ArrayList<Spielfeld> reachableFields = new ArrayList<Spielfeld>();
		if(this.color.contains("white")) {
			for(Figur f : this.ge.blackFigures) {
				if(f.canAttack(this)) {
					return reachableFields;
				}
			}
		} else {
			for(Figur f : this.ge.whiteFigures) {
				if(f.canAttack(this)) {
					return reachableFields;
				}
			}
		}
		if(this.moved) return reachableFields;
		boolean longC = true;
		boolean shortC = true;
		if(this.color == "white") {
			for(Figur f : this.ge.blackFigures) {
				if(f.canReach(this.ge.map[this.y][this.x-1]) || f.canReach(this.ge.map[this.y][this.x-2])) {
					longC = false;
				}
				if(f.canReach(this.ge.map[this.y][this.x+1]) || f.canReach(this.ge.map[this.y][this.x+2])) {
					shortC = false;
				}
			}
		} else {
			for(Figur f : this.ge.whiteFigures) {
				if(f.canReach(this.ge.map[this.y][this.x-1]) || f.canReach(this.ge.map[this.y][this.x-2])) {
					longC = false;
				}
				if(f.canReach(this.ge.map[this.y][this.x+1]) || f.canReach(this.ge.map[this.y][this.x+2])) {
					shortC = false;
				}
			}
		}
		if(canCastleLong() && longC) reachableFields.add(this.ge.map[this.y][this.x-2]);
		else if(canCastleShort() && shortC) reachableFields.add(this.ge.map[this.y][this.x+2]);
		return reachableFields;
	}
	
	public boolean canCastleLong() {
		boolean rook = false;
		Spielfeld f = this.ge.map[this.y][this.x-4];
		if(f.figur != null) {
			if(f.figur.name.contains(this.color + "_rook") && !f.figur.moved) {
				rook = true;
			}
		}
		return rook && this.ge.map[this.y][this.x-1].figur == null && this.ge.map[this.y][this.x-2].figur == null && this.ge.map[this.y][this.x-3].figur == null;
	}
	
	public boolean canCastleShort() {
		boolean rook = false;
		Spielfeld f = this.ge.map[this.y][this.x+3];
		if(f.figur != null) {
			if(f.figur.name.contains(this.color + "_rook") && !f.figur.moved) {
				rook = true;
			}
		}
		return rook && this.ge.map[this.y][this.x+1].figur == null && this.ge.map[this.y][this.x+2].figur == null;
	}
}

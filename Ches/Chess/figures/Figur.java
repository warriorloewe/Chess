package figures;

import java.util.ArrayList;

import main.GameEnvironment;
import main.Spielfeld;

public abstract class Figur {
	public String color;
	public String name;
	public GameEnvironment ge;
	public int x;
	public int y;
	public boolean moved = false;
	public int[][] directions = {{-1, -1}, {1, -1}, {-1, 1}, {1, 1}, {0, 1}, {0, -1}, {1, 0}, {-1, 0}};
	public Figur(int _x, int _y, String _color, String _name, GameEnvironment _ge) {
		this.x = _x;
		this.y = _y;
		this.color = _color;
		this.name = _name;
		this.ge = _ge;
	}
	
	public ArrayList<Spielfeld> checkDirection(int xDirection, int yDirection) {
		int xx = this.x + xDirection;
		int yy = this.y + yDirection;
		ArrayList<Spielfeld> reachableFields = new ArrayList<Spielfeld>();
		while(true) {
			if(xx < 0 || xx > 7 || yy < 0 || yy > 7) {
				return reachableFields;
			}
			if(this.ge.map[yy][xx].figur == null) {
				reachableFields.add(this.ge.map[yy][xx]);
				xx += xDirection;
				yy += yDirection;
				
			} else {
				return reachableFields;
			}
		}
	}
	
	public Figur checkAttackable(int xDirection, int yDirection) {
		int xx = this.x + xDirection;
		int yy = this.y + yDirection;
		while(true) {
			if(xx < 0 || xx > 7 || yy < 0 || yy > 7) {
				return null;
			} else if(this.ge.map[yy][xx].figur == null) {
				xx += xDirection;
				yy += yDirection;
			} else if (this.ge.map[yy][xx].figur.color == this.color){
				return null;
			} else {
				return this.ge.map[yy][xx].figur;
			}
		}
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
		ArrayList<Spielfeld> reachableFields = new ArrayList<Spielfeld>();
		if(this.moved) return reachableFields;
		else if(canCastleLong() && longC) reachableFields.add(this.ge.map[this.y][this.x-2]);
		else if(canCastleShort() && shortC) reachableFields.add(this.ge.map[this.y][this.x+2]);
		return reachableFields;
	}
	
	public boolean canCastleLong() {
		if(!(this.ge.map[this.y][this.x-1].figur == null && this.ge.map[this.y][this.x-2].figur == null && this.ge.map[this.y][this.x-3].figur == null)) {
			return false;
		}
		Spielfeld f = this.ge.map[this.y][this.x-4];
		if(f.figur != null) {
			if(!(f.figur.name.contains(this.color + "_rook") && !f.figur.moved)) {
				return false;
			}
			return true;
		}
		return false;
	}
	
	public boolean canCastleShort() {
		if(!(this.ge.map[this.y][this.x+1].figur == null && this.ge.map[this.y][this.x+2].figur == null)) {
			return false;
		}
		Spielfeld f = this.ge.map[this.y][this.x+3];
		if(f.figur != null) {
			if(!(f.figur.name.contains(this.color + "_rook") && !f.figur.moved)) {
				return false;
			}
			return true;
		}
		return false;
	}
	public abstract boolean canAttack(Figur f);
	public abstract boolean canReach(Spielfeld sf);
}

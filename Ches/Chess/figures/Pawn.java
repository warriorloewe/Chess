package figures;

import main.GameEnvironment;
import main.Spielfeld;

public class Pawn extends Figur{

	public boolean enPassant = false;
	
	public Pawn(int _x, int _y, String _color, GameEnvironment ge) {
		super(_x, _y, _color, _color + "_pawn", ge);
	}

	public void move() {
		
	}
	
	public boolean canReach(Spielfeld sf) {
		if(this.color == "black") {
			if(!moved) {
				return sf.y - this.y > 0 && sf.y - this.y <= 2 && sf.x - this.x == 0;
			}
			return sf.y - this.y == 1 && sf.x - this.x == 0;
		}
		if(!moved) {
			return this.y - sf.y > 0 && this.y - sf.y<= 2 && sf.x - this.x == 0;
		}
		return this.y - sf.y == 1 && sf.x - this.x == 0;
	}
	
	@Override
	public boolean canAttack(Figur f) {
		if(this.color == "black") {
			if(f.name.contains("pawn")) {
				Pawn ff = (Pawn) f;
				if(ff.enPassant) {
					return (Math.abs(f.x - this.x) == 1 && f.y - this.y == 0) || (Math.abs(f.x - this.x) == 1 && f.y - this.y == 1);
				}
			}
			return Math.abs(f.x - this.x) == 1 && f.y - this.y == 1;
		} else {
			if(f.name.contains("pawn")) {
				Pawn ff = (Pawn) f;
				if(ff.enPassant) {
					return (Math.abs(f.x - this.x) == 1 && f.y - this.y == 0) || (Math.abs(f.x - this.x) == 1 && this.y - f.y == 1);
				}
			}
			return Math.abs(f.x - this.x) == 1 && this.y - f.y == 1;
		}
	}
	
	@Override
	public boolean canAttackKing(Figur f) {
		return true;
	}
}


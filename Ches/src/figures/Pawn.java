package figures;

import main.GameEnvironment;
import main.Spielfeld;

public class Pawn extends Figure{
	
	public Pawn(int _x, int _y, String _color, GameEnvironment ge, String uniqueId, boolean enPassant) {
		super(_x, _y, _color, _color + "_pawn", ge, uniqueId, enPassant);
	}

	public boolean canReach(Spielfeld sf) {
		/*
		 * if the pawn hasnt moved he can go 2 squares
		 * otherwise only one
		 */
		if(this.color == "black") {
			return moved ? sf.y - this.y == 1 && sf.x - this.x == 0 : sf.y - this.y > 0 && sf.y - this.y <= 2 && sf.x - this.x == 0;
		}
		return moved ? this.y - sf.y == 1 && sf.x - this.x == 0 : this.y - sf.y > 0 && this.y - sf.y<= 2 && sf.x - this.x == 0;
	}
	
	@Override
	public boolean canAttack(Figure f) {
		if(this.color == "black") {
			return f.enPassant ? (Math.abs(f.x - this.x) == 1 && f.y - this.y == 0) || (Math.abs(f.x - this.x) == 1 && f.y - this.y == 1) : Math.abs(f.x - this.x) == 1 && f.y - this.y == 1;
		}
		return f.enPassant ? (Math.abs(f.x - this.x) == 1 && f.y - this.y == 0) || (Math.abs(f.x - this.x) == 1 && this.y - f.y == 1) : Math.abs(f.x - this.x) == 1 && this.y - f.y == 1;
	}
	
	@Override
	public boolean canAttackKing(Figure f) {
		return true;
	}
}


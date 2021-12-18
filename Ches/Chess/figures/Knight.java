package figures;

import main.GameEnvironment;
import main.Spielfeld;

public class Knight extends Figur{

	public Knight(int _x, int _y, String _color, GameEnvironment ge, String uniqueId, boolean enPassant) {
		super(_x, _y, _color, _color + "_knight", ge, uniqueId, enPassant);
	}
	
	@Override
	public boolean canReach(Spielfeld sf) {
		return Math.abs(this.x - sf.x) >= 1 && Math.abs(this.y - sf.y) >= 1 && Math.abs(this.y - sf.y) + Math.abs(this.x - sf.x) == 3;
	}

	@Override
	public boolean canAttack(Figur f) {
		return Math.abs(this.x - f.x) >= 1 && Math.abs(this.y - f.y) >= 1 && Math.abs(this.y - f.y) + Math.abs(this.x - f.x) == 3;
	}

	@Override
	public boolean canAttackKing(Figur f) {
		return true;
	}
}

package figures;

import main.GameEnvironment;
import main.Spielfeld;

public class King extends Figure {

	public King(int _x, int _y, String _color, GameEnvironment ge, String uniqueId, boolean enPassant) {
		super(_x, _y, _color, _color + "_king", ge, uniqueId, enPassant, 0);
	}

	@Override
	public boolean canReach(Spielfeld sf) {
		return Math.abs(this.x - sf.x) <= 1 && Math.abs(this.y - sf.y) <= 1;
	}

	@Override
	public boolean canAttack(Figure f) {
		return Math.abs(this.x - f.x) <= 1 && Math.abs(this.y - f.y) <= 1;
	}
	
	@Override
	public boolean canAttackKing(Figure f) {
		return true;
	}
}

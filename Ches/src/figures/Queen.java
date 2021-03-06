package figures;

import java.util.ArrayList;
import main.GameEnvironment;
import main.Spielfeld;

public class Queen extends Figure {

	public Queen(int _x, int _y, String _color, GameEnvironment ge, String uniqueId, boolean enPassant) {
		super(_x, _y, _color, _color + "_queen", ge, uniqueId, enPassant, 9);
		this.isLongRange = true;
	}

	@Override
	public ArrayList<Spielfeld> getReachableFields() {
		ArrayList<Spielfeld> reachableFields =  new ArrayList<Spielfeld>();
		for(int i = 0; i < 8; i++) {
			reachableFields.addAll(this.checkDirection(this.directions[i][0], this.directions[i][1]));
		}
		return reachableFields;
	}

	@Override
	public ArrayList<Figure> getReachableEnemies() {
		ArrayList<Figure> attackableFigures = new ArrayList<Figure>();
		for(int i = 0; i < 8; i++) {
			Figure f = this.checkAttackable(this.directions[i][0], this.directions[i][1]);
			if(f != null) {
				attackableFigures.add(f);
			}
		}
		return attackableFigures;
	}
	
	@Override
	public boolean canReach(Spielfeld sf) {
		return getReachableFields().contains(sf);
	}

	@Override
	public boolean canAttack(Figure f) {
		return getReachableEnemies().contains(f);
	}
	
	@Override
	public boolean canAttackKing(Figure f) {
		for(int i = 0; i < 8; i++) {
			if(this.checkAttackableKing(this.directions[i][0], this.directions[i][1])) {
				return true;
			}
		}
		return false;
	}
}

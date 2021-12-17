package figures;
import java.util.ArrayList;

import main.GameEnvironment;
import main.Spielfeld;
public class Queen extends Figur{

	public Queen(int _x, int _y, String _color, GameEnvironment ge) {
		super(_x, _y, _color, _color + "_queen", ge);
		this.isLongRange = true;
	}

	public void move() {
		
	}

	@Override
	public ArrayList<Spielfeld> getReachableFields() {
		System.out.println("1");
		ArrayList<Spielfeld> reachableFields = this.checkDirection(0, -1);
		reachableFields.addAll(this.checkDirection(0, 1));
		reachableFields.addAll(this.checkDirection(-1, 0));
		reachableFields.addAll(this.checkDirection(1, 0));
		reachableFields.addAll(this.checkDirection(-1, -1));
		reachableFields.addAll(this.checkDirection(1, -1));
		reachableFields.addAll(this.checkDirection(-1, 1));
		reachableFields.addAll(this.checkDirection(1, 1));
		return reachableFields;
	}

	@Override
	public ArrayList<Figur> getReachableEnemies() {
		System.out.println("2");
		ArrayList<Figur> attackableFigures = new ArrayList<Figur>();
		for(int i = 0; i < 8; i++) {
			Figur f = this.checkAttackable(this.directions[i][0], this.directions[i][1]);
			if(f == null) {
				continue;
			} else {
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
	public boolean canAttack(Figur f) {
		return getReachableEnemies().contains(f);
	}
	
	@Override
	public boolean canAttackKing(Figur f) {
		for(int i = 0; i < 8; i++) {
			if(this.checkAttackableKing(this.directions[i][0], this.directions[i][1])) {
				return true;
			}
		}
		return false;
	}
}

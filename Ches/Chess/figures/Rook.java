package figures;
import java.util.ArrayList;

import main.GameEnvironment;
import main.Spielfeld;
public class Rook extends Figur{

	public Rook(int _x, int _y, String _color, GameEnvironment ge) {
		super(_x, _y, _color, _color + "_rook", ge);
	}

	public void move() {
		
	}

	@Override
	public ArrayList<Spielfeld> getReachableFields() {
		ArrayList<Spielfeld> reachableFields = this.checkDirection(0, -1);
		reachableFields.addAll(this.checkDirection(0, 1));
		reachableFields.addAll(this.checkDirection(-1, 0));
		reachableFields.addAll(this.checkDirection(1, 0));
		return reachableFields;
	}

	
	@Override
	public ArrayList<Figur> getReachableEnemies() {
		ArrayList<Figur> attackableFigures = new ArrayList<Figur>();
		for(int i = 4; i < 8; i++) {
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
}

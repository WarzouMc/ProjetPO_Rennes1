package aw.common.unit.impl.aerien;

import aw.common.unit.Unit;
import aw.common.unit.UnitType;
import aw.common.unit.Weapon;
import aw.core.game.grid.location.Location;

public class Bombarder extends Aerien {
	public Bombarder() {
		super(20000, "Bombardier",
				new Weapon[] {new Weapon("Bomb")},
				UnitType.BOMBER, 0, 50, 7);
	}

	private Bombarder(Bombarder bombarder, Location location, byte player, boolean available) {
		super(bombarder, location, player, available);
	}

	@Override
	public Unit clone(Location location, byte player, boolean available) {
		return new Bombarder(this, location, player, available);
	}

	@Override
	public int removedFuelByTurn() {
		return 5;
	}
}

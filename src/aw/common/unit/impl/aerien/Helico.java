package aw.common.unit.impl.aerien;

import aw.common.unit.Unit;
import aw.common.unit.UnitType;
import aw.common.unit.Weapon;
import aw.core.game.grid.location.Location;

public class Helico extends Aerien {
	public Helico() {
		super(12000, "helicopter",
				new Weapon[] {new Weapon("Missile")},
				UnitType.HELICOPTER, 1, 90, 6);
	}

	private Helico(Helico helico, Location location, byte player, boolean available) {
		super(helico, location, player, available);
	}

	@Override
	public Unit clone(Location location, byte player, boolean available) {
		return new Helico(this, location, player, available);
	}

	@Override
	public int removedFuelByTurn() {
		return 2;
	}
}

package aw.common.unit.impl;

import aw.common.unit.Unit;
import aw.common.unit.UnitType;
import aw.common.unit.Weapon;
import aw.core.game.grid.location.Location;

public class Cruiser extends Unit {
	public Cruiser() {
		super(8000, "croiseur",
				new Weapon[] {new Weapon("Cannon"), new Weapon("HMG")},
				UnitType.CRUISER, 0, 70, 6);
	}

	private Cruiser(Cruiser cruiser, Location location, byte player, boolean available) {
		super(cruiser, location, player, available);
	}

	@Override
	public Unit clone(Location location, byte player, boolean available) {
		return new Cruiser(this, location, player, available);
	}
}

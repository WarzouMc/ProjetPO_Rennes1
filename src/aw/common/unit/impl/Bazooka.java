package aw.common.unit.impl;

import aw.common.unit.Unit;
import aw.common.unit.UnitType;
import aw.common.unit.Weapon;
import aw.core.game.grid.location.Location;

public class Bazooka extends Unit {
	public Bazooka() {
		super(3500, "bazooka",
				new Weapon[] {new Weapon("Canon"), new Weapon("AAGun")},
				UnitType.BAZOOKA, 0, 500, 2);
	}

	private Bazooka(Bazooka bazooka, Location location, byte player, boolean available) {
		super(bazooka, location, player, available);
	}

	@Override
	public Unit clone(Location location, byte player, boolean available) {
		return new Bazooka(this, location, player, available);
	}
}

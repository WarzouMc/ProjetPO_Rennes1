package aw.common.unit.impl;

import aw.common.unit.Unit;
import aw.common.unit.UnitType;
import aw.common.unit.Weapon;
import aw.core.game.grid.location.Location;

public class Infantry extends Unit {
	public Infantry() {
		super(1500, "infantry",
				new Weapon[] {new Weapon("AAGun")},
				UnitType.INFANTRY,
				0,
				"", "", "", "",
				500, 3);
	}

	private Infantry(Infantry infantry, Location location, byte player, boolean available) {
		super(infantry, location, player, available);
	}

	@Override
	public Unit clone(Location location, byte player, boolean available) {
		return new Infantry(this, location, player, available);
	}
}

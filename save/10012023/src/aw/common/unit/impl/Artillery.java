package aw.common.unit.impl;

import aw.common.unit.Unit;
import aw.common.unit.UnitType;
import aw.common.unit.Weapon;
import aw.core.game.grid.location.Location;

public class Artillery extends Unit {
	public Artillery() {
		super(6000, "artillery",
				new Weapon[] {new Weapon("mortar")},
				UnitType.ARTILLERY,
				0,
				"", "", "", "",
				50, 5);
	}

	private Artillery(Artillery artillery, Location location, byte player, boolean available) {
		super(artillery, location, player, available);
	}

	@Override
	public Unit clone(Location location, byte player, boolean available) {
		return new Artillery(this, location, player, available);
	}
}

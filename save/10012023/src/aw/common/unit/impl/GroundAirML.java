package aw.common.unit.impl;

import aw.common.unit.Unit;
import aw.common.unit.UnitType;
import aw.common.unit.Weapon;
import aw.core.game.grid.location.Location;

public class GroundAirML extends Unit {
	public GroundAirML() {
		super(12000, "Lance-missile Sol-Air",
				new Weapon[] {new Weapon("GAMissile")},
				UnitType.TANK,
				0,
				"", "", "", "",
				50, 5);
	}

	private GroundAirML(GroundAirML groundAirML, Location location, byte player, boolean available) {
		super(groundAirML, location, player, available);
	}

	@Override
	public Unit clone(Location location, byte player, boolean available) {
		return new GroundAirML(this, location, player, available);
	}
}

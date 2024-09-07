package aw.common.unit.impl;

import aw.common.unit.Unit;
import aw.common.unit.UnitType;
import aw.common.unit.Weapon;
import aw.core.game.grid.location.Location;

public class Tank extends Unit {

	public Tank() {
		super(7000, "tank",
				new Weapon[] {new Weapon("AAGun"),new Weapon("Canon")},
				UnitType.TANK, 0, 50, 6);
	}

	private Tank(Tank tank, Location location, byte player, boolean available) {
		super(tank, location, player, available);
	}

	@Override
	public Unit clone(Location location, byte player, boolean available) {
		return new Tank(this, location, player, available);
	}
}

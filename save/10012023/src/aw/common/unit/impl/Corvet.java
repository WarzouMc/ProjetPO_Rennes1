package aw.common.unit.impl;

import aw.common.unit.Unit;
import aw.common.unit.UnitType;
import aw.common.unit.Weapon;
import aw.core.game.grid.location.Location;

public class Corvet extends Unit {
	public Corvet() {
		super(10000, "corvette",
				new Weapon[] {new Weapon("Canon")},
				UnitType.GUNBOAT,
				1,
				"", "", "", "",
				85, 7);
	}

	private Corvet(Corvet corvet, Location location, byte player, boolean available) {
		super(corvet, location, player, available);
	}

	@Override
	public Unit clone(Location location, byte player, boolean available) {
		return new Corvet(this, location, player, available);
	}
}

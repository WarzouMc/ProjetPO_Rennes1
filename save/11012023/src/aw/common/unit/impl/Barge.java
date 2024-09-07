package aw.common.unit.impl;

import aw.common.unit.Unit;
import aw.common.unit.UnitType;
import aw.common.unit.Weapon;
import aw.core.game.grid.location.Location;

public class Barge extends Unit {
	public Barge() {
		super(4000, "barge",
				new Weapon[0],
				UnitType.LANDER, 2, 95, 6);
	}

	private Barge(Barge barge, Location location, byte player, boolean available) {
		super(barge, location, player, available);
	}

	@Override
	public Unit clone(Location location, byte player, boolean available) {
		return new Barge(this, location, player, available);
	}
}

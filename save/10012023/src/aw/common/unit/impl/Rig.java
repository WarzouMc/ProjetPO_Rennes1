package aw.common.unit.impl;

import aw.common.unit.Unit;
import aw.common.unit.UnitType;
import aw.core.game.grid.location.Location;

public class Rig extends Unit {
	public Rig() {
		super(4000, "convoi",
				null,
				UnitType.CONVOY,
				1,
				"", "", "", "",
				80, 6);
	}

	private Rig(Rig rig, Location location, byte player, boolean available) {
		super(rig, location, player, available);
	}

	@Override
	public Unit clone(Location location, byte player, boolean available) {
		return new Rig(this, location, player, available);
	}
}

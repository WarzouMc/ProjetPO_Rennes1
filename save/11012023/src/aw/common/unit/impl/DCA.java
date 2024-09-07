package aw.common.unit.impl;

import aw.common.unit.Unit;
import aw.common.unit.UnitType;
import aw.common.unit.Weapon;
import aw.core.game.grid.location.Location;

public class DCA extends Unit {
	public DCA() {
		super(6000, "DCA",
				new Weapon[] {new Weapon("HMG")},
				UnitType.DAA, 0, 50, 6);
	}

	private DCA(DCA dca, Location location, byte player, boolean available) {
		super(dca, location, player, available);
	}

	@Override
	public Unit clone(Location location, byte player, boolean available) {
		return new DCA(this, location, player, available);
	}
}

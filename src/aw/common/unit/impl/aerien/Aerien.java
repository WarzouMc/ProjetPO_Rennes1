package aw.common.unit.impl.aerien;

import aw.common.unit.Unit;
import aw.common.unit.UnitType;
import aw.common.unit.Weapon;
import aw.core.game.grid.location.Location;

public abstract class Aerien extends Unit {

    public Aerien(int price, String name, Weapon[] weaponery, UnitType unitType, int transportCapa, int fuel, int movementPoints) {
        super(price, name, weaponery, unitType, transportCapa, fuel, movementPoints);
    }

    protected Aerien(Unit unit, Location location, byte player, boolean available) {
        super(unit, location, player, available);
    }

    /**
     * @return le nombre de point de fuel a enlever à chaque tour
     */
    public abstract int removedFuelByTurn();
}

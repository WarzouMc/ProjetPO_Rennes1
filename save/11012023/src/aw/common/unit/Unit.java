package aw.common.unit;

import aw.core.game.grid.location.Location;
import ressources.Chemins;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Unit {//cette classe definie le squelette des unites
	protected byte player;
	protected int price;//le prix de l'unite
	protected String name;//son nom
	protected Weapon[] weaponery;//la liste de ses armes et de leurs munitions
	protected UnitType unitType;//type de l'unite
	protected int transportCapa;//la capacite de transport de l'unite
	protected List<Unit> unit;//les unitees transportees par cette unite
	protected boolean used;//si l'unite est utilisee ou non
	protected int fuel;//le carburant de l'unite si cela est pertinent
	protected int movementPoints;//ses pointes de mouvements
	protected int lifePoints;//les points de vie de l'unite
	protected boolean available;
	protected Location location;

	private final boolean loadClass;
	
	public Unit(int price, String name, Weapon[] weaponery, UnitType unitType,
			int transportCapa, int fuel, int movementPoints) {
		this.price = price;
		this.name = name;
		this.weaponery = weaponery;
		this.unitType = unitType;
		this.transportCapa = transportCapa;
		this.unit= new ArrayList<>();//toute unite commence la partie sans en transporter une autre
		this.used=false;//les unites commencent la partie en n'etant pas utilisees
		this.fuel=fuel;
		this.movementPoints=movementPoints;
		this.lifePoints=10;//toutes les unites commencent la partie a 10 points de vie
		this.location = null;
		this.loadClass = true;
	}

	protected Unit(Unit unit, Location location, byte player, boolean available) {
		this.price = unit.price;
		this.name = unit.name;
		this.weaponery = copy(unit.weaponery);
		this.unitType = unit.unitType;
		this.transportCapa = unit.transportCapa;
		this.unit=new ArrayList<>(unit.unit);//toute unite commence la partie sans en transporter une autre
		this.used=unit.used;//les unites commencent la partie en n'etant pas utilisees
		this.fuel=unit.fuel;
		this.movementPoints=unit.movementPoints;
		this.lifePoints=unit.lifePoints;//toutes les unites commencent la partie a 10 points de vie
		this.location = location;
		this.player = player;
		this.available = available;
		this.loadClass = false;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public byte getPlayer() {
		return player;
	}

	public void setPlayer(byte player) {
		this.player = player;
	}

	public abstract Unit clone(Location location, byte player, boolean available);

	public Location getLocation() {
		return this.location;
	}

	public String getImagePath() {
		return Chemins.getCheminUnite(this.player, this.available, this.unitType.getImageName());
	}

	public int getFuel() {
		return fuel;
	}

	public void setFuel(int fuel) {
		this.fuel = fuel;
	}

	public int getMovementPoints() {
		return movementPoints;
	}

	public void setMovementPoints(int movementPoints) {
		this.movementPoints = movementPoints;
	}

	public UnitType getUnitType() {
		return unitType;
	}

	public boolean isLoadClass() {
		return this.loadClass;
	}

	private void loadClassCheckThrow() {
		if (this.loadClass)
			throw new IllegalAccessError();
	}

	protected void loadType(UnitType type) {
		this.unitType = type;
	}

	private <T> T[] copy(T[] array) {
		return Arrays.copyOf(array, array.length);
	}
}

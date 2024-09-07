package aw.common.unit;

import aw.core.component.Drawable;
import aw.core.game.grid.location.Location;
import ressources.Chemins;

import java.util.ArrayList;
import java.util.List;

public abstract class Unit implements Drawable {//cette classe definie le squelette des unites
	protected byte player;
	protected int price;//le prix de l'unite
	protected String name;//son nom
	protected Weapon[] weaponery;//la liste de ses armes et de leurs munitions
	protected UnitType unitType;//type de l'unite
	protected int transportCapa;//la capacite de transport de l'unite
	protected List<Unit> unit;//les unitees transportees par cette unite
	protected boolean used;//si l'unite est utilisee ou non
	protected int fuel;//le carburant de l'unite si cela est pertinent
	protected int fuelMax;//la capacite initiale de carburant de l'unite
	protected int movementPoints;//ses pointes de mouvements
	private int MaxMouvementPoint;//ses points de mouvement initiaux
	protected int lifePoints;//les points de vie de l'unite
	protected boolean available;//disponibilite de l'unite
	protected Location location;//localisation de l'unite
	private final boolean loadClass;//si l'instance de la class est utilisable pour en charger une autre.
	
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
		this.fuelMax=fuel;
		this.movementPoints=movementPoints;
		this.MaxMouvementPoint=movementPoints;
		this.lifePoints=10;//toutes les unites commencent la partie a 10 points de vie
		this.location = null;
		this.loadClass = true;
	}

	protected Unit(Unit unit, Location location, byte player, boolean available) {
		this.price = unit.price;
		this.name = unit.name;
		this.weaponery = copyWeapons(unit.weaponery);
		this.unitType = unit.unitType;
		this.transportCapa = unit.transportCapa;
		this.unit=new ArrayList<>(unit.unit);//toute unite commence la partie sans en transporter une autre
		this.used=unit.used;//les unites commencent la partie en n'etant pas utilisees
		this.fuel=unit.fuel;
		this.fuelMax=unit.fuelMax;
		this.movementPoints=unit.movementPoints;
		this.MaxMouvementPoint=unit.MaxMouvementPoint;
		this.lifePoints=unit.lifePoints;//toutes les unites commencent la partie a 10 points de vie
		this.location = location;
		this.player = player;
		this.available = available;
		this.loadClass = false;
	}

	public Weapon[] getWeaponery() {
		return weaponery;
	}

	public void setWeaponery(Weapon[] weaponery) {
		this.weaponery = weaponery;
	}

	//public boolean isAvailable() {
	//	return available;
	//}

	public int getLifePoints() {
		return lifePoints;
	}

	public void setLifePoints(int lifePoints) {
		this.lifePoints = lifePoints;
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
	public void setLocation(Location location) {
		this.location = location;
	}

	/**
	 * Creer une nouvelle instance de cette unite.
	 * @param location localisation de la nouvelle unite
	 * @param player joueur possedant l'unite
	 * @param available disponibilite de l'unite
	 * @return une nouvelle instance de cette unite
	 */
	public abstract Unit clone(Location location, byte player, boolean available);

	public Location getLocation() {
		return this.location;
	}

	@Override
	public String getImagePath() {
		return Chemins.getCheminUnite(this.player, this.available, this.unitType.getImageName());
	}

	public int getFuel() {
		return fuel;
	}

	public void setFuel(int fuel) {
		this.fuel = fuel;
	}

	public int getFuelMax() {
		return fuelMax;
	}

	public void setFuelMax(int fuelMax) {
		this.fuelMax = fuelMax;
	}

	public int getMovementPoints() {
		return movementPoints;
	}

	public int getMaxMouvementPoint() {
		return MaxMouvementPoint;
	}

	public void setMaxMouvementPoint(int maxMouvementPoint) {
		MaxMouvementPoint = maxMouvementPoint;
	}

	public void setMovementPoints(int movementPoints) {
		this.movementPoints = movementPoints;
	}

	public UnitType getUnitType() {
		return unitType;
	}

	private void loadClassCheckThrow() {
		if (this.loadClass)
			throw new IllegalAccessError();
	}

	/**
	 * Permet de charger le type de l'unite depuis l'enumeration {@link UnitType}
	 * @param type type de l'unite
	 */
	void loadType(UnitType type) {
		this.unitType = type;
	}

	/**
	 * @param array tableau source
	 * @param <T> type generic
	 * @return une copy de l'array en parametre
	 */
    /**
     * @param array tableau source
     * @return une copy de l'array en parametre
     */
    private Weapon[] copyWeapons(Weapon[] array) {
        Weapon[] newArray = new Weapon[array.length];
        for (int i = 0; i < array.length; i++) {
            newArray[i] = array[i].clone();
        }
        return newArray;
    }
}

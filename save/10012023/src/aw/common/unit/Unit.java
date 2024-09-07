package aw.common.unit;

import aw.core.game.grid.location.Location;
import ressources.Chemins;

import java.util.Arrays;

public abstract class Unit {//cette classe definie le squelette des unites
	protected byte player;
	protected int price;//le prix de l'unite
	protected String name;//son nom
	protected Weapon[] weaponery;//la liste de ses armes et de leurs munitions
	protected UnitType unitType;//si l'unite est de type terrestre,marin, aerien...
	protected int transportCapa;//la capacite de transport de l'unite
	protected Unit[] unit;//les unitees transportees par cette unite 
	protected String imagenotUsedRed;//le chemin de l'image non utilisee en rouge
	protected String imagenotUsedBlue;//le chemin de l'image non utilisee en bleu
	protected String imageUsedRed;//le chemin de l'image utilisee en rouge
	protected String imageUsedBlue;//le chemin de l'image utilisee en bleu
	protected boolean used;//si l'unite est utilisee ou non
	protected int fuel;//le carburant de l'unite si cela est pertinent
	protected int movementPoints;//ses pointes de mouvements
	protected int lifePoints;//les points de vie de l'unite
	protected boolean available;
	protected Location location;

	private final boolean loadClass;
	
	public Unit(int price, String name, Weapon[] weaponery, UnitType unitType,
			int transportCapa, String imagenotUsedRed,String imagenotUsedBlue,
		String imageUsedRed, String imageUsedBlue,
		int fuel, int movementPoints) {
		this.price = price;
		this.name = name;
		this.weaponery = weaponery;
		this.unitType = unitType;
		this.transportCapa = transportCapa;
		this.unit=null;//toute unite commence la partie sans en transporter une autre
		this.imagenotUsedBlue=imagenotUsedBlue;
		this.imagenotUsedRed=imagenotUsedRed;
		this.imageUsedBlue=imageUsedBlue;
		this.imageUsedRed=imageUsedRed;
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
		this.unit=copy(unit.unit);//toute unite commence la partie sans en transporter une autre
		this.imagenotUsedBlue=unit.imagenotUsedBlue;
		this.imagenotUsedRed=unit.imagenotUsedRed;
		this.imageUsedBlue=unit.imageUsedBlue;
		this.imageUsedRed=unit.imageUsedRed;
		this.used=unit.used;//les unites commencent la partie en n'etant pas utilisees
		this.fuel=unit.fuel;
		this.movementPoints=unit.movementPoints;
		this.lifePoints=unit.lifePoints;//toutes les unites commencent la partie a 10 points de vie
		this.location = location;
		this.player = player;
		this.available = available;
		this.loadClass = false;
	}

	public abstract Unit clone(Location location, byte player, boolean available);

	public Location getLocation() {
		return this.location;
	}

	public String getImagePath() {
		return Chemins.getCheminUnite(this.player, this.available, this.unitType.getImageName());
	}

	public boolean isLoadClass() {
		return this.loadClass;
	}

	private void loadClassCheckThrow() {
		if (this.loadClass)
			throw new IllegalAccessError();
	}

	private <T> T[] copy(T[] array) {
		return Arrays.copyOf(array, array.length);
	}
}

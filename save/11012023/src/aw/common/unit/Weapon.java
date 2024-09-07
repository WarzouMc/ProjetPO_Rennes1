package aw.common.unit;

public class Weapon {//cette classe définie tous les types d'armes différents
	private String name;//le nom de l'arme
	private int ammunitions;//ses munitions
	private int[] efficiency;//ses efficacitees sur les differentes unites
	//dans l'ordre suivant infantery, bazooka, tank, DCA, RIG, Helico, Bombardier, unites marines, barges, artillerie
	private int maxrange;//distance maximale de tir
	private int minrange;//distance minimale de tir, ces deux derienrs attributs sont
	//necessaires dans le cadre des tirs à distance
	public Weapon(String name) {
		int[] efficiencyAAGun = {60,55,15,10,30,0,40,20,40,10};
		int[] efficiencyMortar = {40,50,50,50,50,50,0,0,40,50};
		int[] efficiencyCanon = {0,0,55,60,70,0,0,50,55,60};
		int[] efficiencyBomb = {100,100,100,70,100,0,0,55,100,70};
		int[] efficiencyHMG = {100,80,30,30,50,110,70,30,40,30};
		int[] efficiencyGAMissile = {0,0,0,0,0,120,120,0,0,0};
		int[] efficiencyMissile = {0,0,70,40,70,0,0,55,70,45};
		this.name=name;
		if(name.equals("AAGun")) {
			this.ammunitions=9;
			this.efficiency= efficiencyAAGun;
			this.maxrange=1;
			this.minrange=1;
		} else if(name.equals("Mortar")) {
			this.ammunitions=4;
			this.efficiency= efficiencyMortar;
			this.maxrange=2;
			this.minrange=3;
		} else if(name.equals("Canon")) {
			this.ammunitions=6;
			this.efficiency= efficiencyCanon;
			this.maxrange=1;
			this.minrange=1;
		} else if(name.equals("Bomb")) {
			this.ammunitions=3;
			this.efficiency= efficiencyBomb;
			this.maxrange=1;
			this.minrange=1;
		} else if(name.equals("HMG")) {
			this.ammunitions=8;
			this.efficiency= efficiencyHMG;
			this.maxrange=1;
			this.minrange=1;
		} else if(name.equals("GAMissile")) {
			this.ammunitions=4;
			this.efficiency= efficiencyGAMissile;
			this.maxrange=3;
			this.minrange=6;
		} else if(name.equals("Missile")) {
			this.ammunitions=6;
			this.efficiency= efficiencyMissile;
			this.maxrange=1;
			this.minrange=1;
		} else {
			this.ammunitions=0;
			this.efficiency= null;
			this.maxrange=0;
			this.minrange=0;

		}
	}
}

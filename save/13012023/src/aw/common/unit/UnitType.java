package aw.common.unit;

import aw.common.unit.impl.*;
import aw.core.game.grid.location.Location;
import ressources.Chemins;

import java.util.Arrays;

/**
 * Enumeration de tous les types d'unite
 */
public enum UnitType {

	INFANTRY("Infanterie", Chemins.FICHIER_INFANTERIE, SubType.FOOT, new Infantry()),
	BAZOOKA("Bazooka", Chemins.FICHIER_BAZOOKA, SubType.FOOT, new Bazooka()),
	TANK("Tank", Chemins.FICHIER_TANK, SubType.CHENILLE, new Tank()),
	DAA("DCA", Chemins.FICHIER_ANTIAIR, SubType.CHENILLE, new DCA()),
	HELICOPTER("Helico", Chemins.FICHIER_HELICOPTERE, SubType.AERIAL, new Helico()),
	BOMBER("Bombardier", Chemins.FICHIER_BOMBARDIER, SubType.AERIAL, new Bombarder()),
	CRUISER("Croiseur" , Chemins.FICHIER_CROISEUR, SubType.MARIN, new Cruiser()),
	GUNBOAT("Corvette" , Chemins.FICHIER_CORVETTE, SubType.MARIN, new Corvet()),
	LANDER("Barge" , Chemins.FICHIER_BARGE, SubType.MARIN, new Barge()),
	AIRCRAFT_CARRIER("Porte Avion" , Chemins.FICHIER_PORTE_AVION, SubType.AERIAL, null /*todo ???*/),
	CUIRASSIER("Cuirassier" , Chemins.FICHIER_CUIRASSIER, SubType.MARIN, null /*todo ???*/),
	SUBMARINE("Sous-Marin" , Chemins.FICHIER_SOUS_MARIN, SubType.MARIN, null /*todo ???*/),
	CONVOY("Convoit", Chemins.FICHIER_GENIE, SubType.CHENILLE, new Rig()),
	ARTILLERY("Artillerie", Chemins.FICHIER_ARTILLERIE, SubType.CHENILLE, new Artillery()),
	;

	static {
		Arrays.stream(values()).filter(value -> value.loadClass != null).forEach(value -> value.loadClass.loadType(value));
	}

	private final String rawName; // nom de l'unite, doit correspondre aux noms dans le squelette
	private final String imageName; // nom du fichier image
	private final SubType subType; // sous type du type
	private final Unit loadClass; // class permettant l'instanciation de d'une nouvelle unité de ce type dans avoir un gros bloc if-else

	/**
	 * @param rawName nom dans le fichier .awdcmap
	 * @param imageName nom du fichier image
	 * @param subType sous type de l'unite
	 * @param loadClass instance de l'unité permettant une nouvelle instanciation sans passer par la reflection
	 */
	UnitType(String rawName, String imageName, SubType subType, Unit loadClass) {
		this.rawName = rawName;
		this.imageName = imageName;
		this.subType = subType;
		this.loadClass = loadClass;
	}

	/**
	 * @return nom du fichier image
	 */
	public String getImageName() {
		return this.imageName;
	}

	/**
	 * @return sous type de l'unite
	 */
	public SubType getSubType() {
		return this.subType;
	}

	/**
	 * @param unit nom de l'unite, doit correspondre aux noms dans le squelette
	 * @return le type de l'unite depuis un string
	 */
	public static UnitType fromString(String unit) {
		return Arrays.stream(values())
				.filter(type -> type.rawName.equalsIgnoreCase(unit))
				.findFirst()
				.orElseThrow();
	}

	/**
	 * @param unit type de l'unite
	 * @param location localisation de l'unite
	 * @param player joueur possedant l'unite
	 * @param available disponibilite de l'unite
	 * @return une nouvelle instance de Unit
	 */
	public static Unit instantiateUnit(String unit, Location location, byte player, boolean available) {
		return fromString(unit).loadClass.clone(location, player, available);
	}

	/**
	 * Enumer tout les sous type d'unite
	 */
	public enum SubType {
		FOOT(new int[] {1, 1, 1, 2, 0, 1, 0}),
		CHENILLE(new int[] {1, 1, 2, 1, 0, 2, 0}),
		AERIAL(new int[]{1, 1, 1, 1, 1, 1, 1}),
		MARIN(new int[] {1, 0, 0, 0, 1, 0, 1}),
		;

		private final int[] movementCost;

		/**
		 * @param movementCost cout des mouvements par biome
		 */
		SubType(int[] movementCost) {
			this.movementCost = movementCost;
		}

		/**
		 * @return le cout de mouvement par biome
		 */
		public int[] getMovementCost() {
			return this.movementCost;
		}
	}
}

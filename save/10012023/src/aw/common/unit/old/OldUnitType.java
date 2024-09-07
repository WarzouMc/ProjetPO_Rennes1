package aw.common.unit.old;

import aw.common.weapons.WeaponsEfficiencies;
import aw.common.weapons.WeaponsType;
import aw.core.game.grid.location.movement.MovementType;
import ressources.Chemins;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Deprecated
public enum OldUnitType {

    INFANTRY("Infanterie", Chemins.FICHIER_INFANTERIE, new UnitMovement(MovementType.FOOT, 3),
            1500, new WeaponsEfficiencies(0.6f, 0.45f, 1, 0.5f, 1),
            List.of(WeaponsType.LIGHT_MACHINE_GUN)),
    BAZOOKA("Bazooka", Chemins.FICHIER_BAZOOKA, new UnitMovement(MovementType.FOOT, 2),
            3500, new WeaponsEfficiencies(0.55f, 0.45f, 0.8f, 0.5f, 1),
            List.of(WeaponsType.CANON)),
    TANK("Tank", Chemins.FICHIER_TANK, new UnitMovement(MovementType.CATERPILLAR, 6),
            7000, new WeaponsEfficiencies(0.15f, 0.55f, 0.3f, 0.7f, 1),
            List.of(WeaponsType.CANON)),
    DAA("DCA", Chemins.FICHIER_ANTIAIR, new UnitMovement(MovementType.CATERPILLAR, 6),
            6000, new WeaponsEfficiencies(0.1f, 0.6f,0.3f, 0.4f, 0.7f),
            List.of(WeaponsType.HEAVY_MACHINE_GUN)),
    HELICOPTER("Helico", Chemins.FICHIER_HELICOPTERE, new UnitMovement(MovementType.AERIAL, 6),
            12000, new WeaponsEfficiencies(0.3f, 0.3f, 1.1f, 0.7f, 0),
            List.of(WeaponsType.MISSILE)),
    BOMBER("Bombardier", Chemins.FICHIER_BOMBARDIER, new UnitMovement(MovementType.AERIAL, 7),
            20000, new WeaponsEfficiencies(0, 0, 0.7f, 0.7f, 0),
            List.of(WeaponsType.BOMB)),
    CRUISER("Croiseur" , Chemins.FICHIER_CROISEUR, new UnitMovement(MovementType.MARIN,6 ),
            16000,null /*todo*/,
            List.of(WeaponsType.ANTI_SHIP_MISSILES, WeaponsType.HEAVY_MACHINE_GUN)),
    Gunboat("Corvette" , Chemins.FICHIER_CORVETTE, new UnitMovement(MovementType.MARIN,7 ),
            6000,null /*todo*/,
            List.of(WeaponsType.ANTI_SHIP_MISSILES)),
    Lander("Barge" , Chemins.FICHIER_BARGE, new UnitMovement(MovementType.BARGE,6 ),
            12000,null /*todo*/,
            List.of()),
    AircraftCarrier("Porte Avion" , Chemins.PORTEAVION, new UnitMovement(MovementType.MARIN,4 ),
            28000,null /*todo*/,
            List.of(WeaponsType.HEAVY_MACHINE_GUN)),
    Cuirassier("Cuirassier" , Chemins.FICHIER_CUIRASSIER, new UnitMovement(MovementType.MARIN,4 ),
            25000,null /*todo*/,
            List.of(WeaponsType.CANON)),
    Submarine("Sous-Marin" , Chemins.FICHIER_SOUSMARIN, new UnitMovement(MovementType.SUBMARIN,6 ),
            2000,new WeaponsEfficiencies(0,0,0,0,0/*todo*/),
            List.of(WeaponsType.TORPEDO)),
    CONVOY("Convoit", Chemins.FICHIER_ARTILLERIE /*todo changer le fichier ici*/, new UnitMovement(MovementType.CATERPILLAR, 6),
            5000, new WeaponsEfficiencies(0.4f, 0.7f, 0.5f, 0.7f, 1),
            Collections.emptyList()),
    ARTILLERY("Artillerie", Chemins.FICHIER_ARTILLERIE, new UnitMovement(/*todo*/ null, -1),
            6000, null /*todo*/,
            List.of(WeaponsType.MORTAR)),
    ;

    private final String rawString;
    private final String imageFile;
    private final UnitMovement unitMovement;
    private final int price;
    private final WeaponsEfficiencies weaponsEfficiencies;
    private final WeaponsType[] weapons;

    OldUnitType(String rawString, String imageFile, UnitMovement unitMovement, int price, WeaponsEfficiencies weaponsEfficiencies, List<WeaponsType> weapons) {
        this.rawString = rawString;
        this.imageFile = imageFile;
        this.unitMovement = unitMovement;
        this.price = price;
        this.weaponsEfficiencies = weaponsEfficiencies;
        this.weapons = weapons.toArray(new WeaponsType[] {});
    }

    public String getRawString() {
        return this.rawString;
    }

    public String getImageFile() {
        return this.imageFile;
    }

    public UnitMovement getUnitMovement() {
        return this.unitMovement;
    }

    public int getPrice() {
        return this.price;
    }

    public WeaponsEfficiencies getWeaponsEfficiencies() {
        return this.weaponsEfficiencies;
    }

    public WeaponsType[] getWeapons() {
        return this.weapons;
    }

    public static OldUnitType fromString(String unit) {
        return Arrays.stream(values())
                .filter(tileBiomeType -> tileBiomeType.rawString.equalsIgnoreCase(unit))
                .findFirst()
                .orElseThrow();
    }
}

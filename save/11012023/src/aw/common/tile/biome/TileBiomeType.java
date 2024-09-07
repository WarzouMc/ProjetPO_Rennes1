package aw.common.tile.biome;

import aw.core.game.grid.location.movement.MovementCost;
import ressources.Chemins;

import java.util.Arrays;

public enum TileBiomeType {

    PROPERTY_HQ("QG", Chemins.FICHIER_QG, true, new MovementCost(1, 1)),
    PROPERTY_CITY("Ville", Chemins.FICHIER_VILLE, true, new MovementCost(1, 1)),
    PROPERTY_FACTORY("Usine", Chemins.FICHIER_USINE, true, new MovementCost(1, 1)),
    PLAIN("Plaine", Chemins.FICHIER_PLAINE, false, new MovementCost(1, 1)),
    FOREST("Foret", Chemins.FICHIER_FORET, false, new MovementCost(1, 2)),
    MOUNTAIN("Montagne", Chemins.FICHIER_MONTAGNE, false, new MovementCost(2, -1)),
    SEA("Eau", Chemins.FICHIER_EAU, false, new MovementCost(-1, -1)),
    ;

    private final String rawString;
    private final String imageFile;
    private final boolean property;
    private final MovementCost movementCost;

    TileBiomeType(String rawString, String imageFile, boolean property, MovementCost movementCost) {
        this.rawString = rawString;
        this.imageFile = imageFile;
        this.property = property;
        this.movementCost = movementCost;
    }

    public String getRawString() {
        return this.rawString;
    }

    String getImageFile() {
        return this.imageFile;
    }

    public boolean isProperty() {
        return this.property;
    }

    public MovementCost getMovementCost() {
        return this.movementCost;
    }

    public static TileBiomeType fromString(String biome) {
        return Arrays.stream(values())
                .filter(tileBiomeType -> tileBiomeType.rawString.equalsIgnoreCase(biome))
                .findFirst()
                .orElseThrow();
    }
}

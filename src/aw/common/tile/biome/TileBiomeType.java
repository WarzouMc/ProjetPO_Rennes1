package aw.common.tile.biome;

import ressources.Chemins;

import java.util.Arrays;

/**
 * Enumeration de tous les types de biome
 */
public enum TileBiomeType {

    PROPERTY_HQ("QG", Chemins.FICHIER_QG, true, 10),
    PROPERTY_CITY("Ville", Chemins.FICHIER_VILLE, true, 10),
    PROPERTY_FACTORY("Usine", Chemins.FICHIER_USINE, true, 10),
    PLAIN("Plaine", Chemins.FICHIER_PLAINE, false, 0),
    FOREST("Foret", Chemins.FICHIER_FORET, false, 0),
    MOUNTAIN("Montagne", Chemins.FICHIER_MONTAGNE, false, 0),
    SEA("Eau", Chemins.FICHIER_EAU, false, 0),
    ;

    private final String rawString;
    private final String imageFile;
    private final boolean property;
    private int pc;

    /**
     * @param rawString nom dans le fichier .awdcmap
     * @param imageFile nom du fichier image
     * @param property si le biome est une propriete
     * @param pc point de controle
     */
    TileBiomeType(String rawString, String imageFile, boolean property, int pc) {
        this.rawString = rawString;
        this.imageFile = imageFile;
        this.property = property;
        this.pc =pc;
    }
    /**
    *
    * @return si la tuile est un QG
    */
   public boolean isQG() {
       return this == PROPERTY_HQ;
   }
    /**
     * @return point de controle
     */
    int getPC() {
        return pc;
    }

    /**
     * @return nom du fichier image du biome
     */
    String getImageFile() {
        return this.imageFile;
    }

    /**
     * @return si le biome est une propriete
     */
    public boolean isProperty() {
        return this.property;
    }

    /**
     * @param biome nom du biome
     * @return type du biome depuis un string
     */
    public static TileBiomeType fromString(String biome) {
        return Arrays.stream(values())
                .filter(tileBiomeType -> tileBiomeType.rawString.equalsIgnoreCase(biome))
                .findFirst()
                .orElseThrow();
    }
}

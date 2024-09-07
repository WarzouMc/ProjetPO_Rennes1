package aw.common.tile.biome;

import aw.core.component.Drawable;
import ressources.Chemins;

public class TileBiome implements Drawable {

    private final TileBiomeType biomeType;
    private byte player;

    public TileBiome(TileBiomeType biomeType, byte player) {
        this.biomeType = biomeType;
        this.player = player;
    }

    @Override
    public String getImagePath() {
        return this.biomeType.isProperty()
                ? Chemins.getCheminPropriete(this.biomeType.getImageFile(), this.player)
                : Chemins.getCheminTerrain(this.biomeType.getImageFile());
    }

    public TileBiomeType getBiomeType() {
        return this.biomeType;
    }

    @Override
    public String toString() {
        return "TileBiome{" +
                "biomeType=" + biomeType +
                ", player=" + player +
                '}';
    }
}

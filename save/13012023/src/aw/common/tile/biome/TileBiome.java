package aw.common.tile.biome;

import aw.core.component.Drawable;
import ressources.Chemins;

public class TileBiome implements Drawable {

    private final TileBiomeType biomeType;
    private byte player;
    private int pc;

    /**
     * @param biomeType type du biome
     * @param player joueur a qui appartient le biome (neutre si ce n'ai pas une propriete)
     */
    public TileBiome(TileBiomeType biomeType, byte player) {
        this.biomeType = biomeType;
        this.player = player;
        this.pc = biomeType.getPC();
    }

    /**
     * @return entier representent le type du biome (ce n'est pas un UID)
     */
    public int id() {
        return this.biomeType.ordinal();
    }

    /**
     * @return les point de controle
     */
    public int getPC() {
        return pc;
    }

    /**
     * @param pc point de controle
     */
    public void setPC(int pc) {
        this.pc = pc;
    }

    @Override
    public String getImagePath() {
        return this.biomeType.isProperty()
                ? Chemins.getCheminPropriete(this.biomeType.getImageFile(), this.player)
                : Chemins.getCheminTerrain(this.biomeType.getImageFile());
    }

    /**
     * @return type du biome
     */
    public TileBiomeType getBiomeType() {
        return this.biomeType;
    }

    /**
     * @return joueur possedant le biome
     */
    public byte getPlayer() {
        return this.player;
    }

    /**
     * @param player joueur possedant le biome
     */
    public void setPlayer(byte player) {
        this.player = player;
    }

    @Override
    public String toString() {
        return "TileBiome{" +
                "biomeType=" + biomeType +
                ", player=" + player +
                '}';
    }
}

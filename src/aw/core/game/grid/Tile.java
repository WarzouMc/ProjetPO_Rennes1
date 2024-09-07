package aw.core.game.grid;

import aw.common.tile.biome.TileBiome;
import aw.common.unit.Unit;

import java.util.Optional;

/**
 * Characterise une tuile sur la grille du jeu
 */
public class Tile {

    private final int x;
    private final int y;
    private Optional<Unit> optionalUnit; //todo Unit class
    private final TileBiome biome;

    /**
     * @param x abscisse
     * @param y ordonne
     * @param unit unite sur la tuile (null si il y en a pas)
     * @param biome biome de la tuile
     */
    Tile(int x, int y, Unit unit, TileBiome biome) {
        this.x = x;
        this.y = y;
        this.optionalUnit = Optional.ofNullable(unit);
        this.biome = biome;
    }

    /**
     * @return abscisse de la tuile
     */
    public int getX() {
        return this.x;
    }

    /**
     * @return ordonne de la tuile
     */
    public int getY() {
        return this.y;
    }

    /**
     * @return biome de la tuile
     */
    public TileBiome getBiome() {
        return this.biome;
    }

    /**
     * @return une option sur l'{@link Unit} de la tuile
     */
    public Optional<Unit> getOptionalUnit() {
        return optionalUnit;
    }

    /**
     * @param optionalUnit une option d'{@link Unit}
     */
    public void setOptionalUnit(Optional<Unit> optionalUnit) {
        this.optionalUnit = optionalUnit;
    }

    @Override
    public String toString() {
        return "Tile{" +
                "x=" + x +
                ", y=" + y +
                ", optionalUnit=" + optionalUnit +
                ", biome=" + biome +
                '}';
    }
}

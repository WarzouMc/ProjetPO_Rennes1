package aw.core.game.grid;

import aw.common.tile.biome.TileBiome;
import aw.common.unit.Unit;
import aw.common.unit.old.OldUnit;

import java.util.Optional;

public class Tile {

    private final int x;
    private final int y;
    private Optional<Unit> optionalUnit; //todo Unit class
    private boolean cursor; //todo see who we do that
    private final TileBiome biome;

    Tile(int x, int y, Unit unit, TileBiome biome) {
        this.x = x;
        this.y = y;
        this.optionalUnit = Optional.ofNullable(unit);
        this.biome = biome;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public TileBiome getBiome() {
        return this.biome;
    }

    public Optional<Unit> getOptionalUnit() {
        return this.optionalUnit;
    }

    @Override
    public String toString() {
        return "Tile{" +
                "x=" + x +
                ", y=" + y +
                ", optionalUnit=" + optionalUnit +
                ", cursor=" + cursor +
                ", biome=" + biome +
                '}';
    }
}

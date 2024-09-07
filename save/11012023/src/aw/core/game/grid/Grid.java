package aw.core.game.grid;

import aw.common.tile.biome.TileBiome;
import aw.common.tile.biome.TileBiomeType;
import aw.common.unit.Unit;
import aw.common.unit.UnitType;
import aw.core.game.grid.location.Location;

import java.util.Arrays;

public class Grid {

    private final int width;
    private final int height;
    private final Tile[] grid;

    public Grid(String[][] grid) {
        this.height = grid.length;
        if (this.height == 0)
            throw new IllegalArgumentException("Invalid grid !");
        this.width = grid[0].length;
        this.grid = new Tile[this.width * this.height];
        buildGrid(grid);
    }

    public Tile[] getGrid() {
        return this.grid; // todo remove
    }

    public Tile getTile(int x, int y) {
        return this.grid[bijection(x, y)];
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    private int bijection(int x, int y) {
        return x * this.height + y;
    }

    private int[] bijection(int location) {
        return new int[] {location / height, location % this.height};
    }

    private void buildGrid(String[][] grid) {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                String tileString = grid[i][j];
                this.grid[bijection(this.width - j - 1, this.height - i - 1)] = createTile(tileString, i, j);
            }
        }
    }

    private Tile createTile(String tileString, int rawX, int rawY) {
        String[] tileSplit = tileString.split(";");
        if (tileSplit.length == 0)
            throw new RuntimeException(String.format("Illegal tile format at location (%s ; %s) !", rawX, rawY));

        String[] biomeSplit = tileSplit[0].split(":");
        TileBiome tileBiome = createBiome(biomeSplit, rawX, rawY);

        String[] unitSplit = tileSplit.length > 1 ? tileSplit[1].split(":") : new String[0];
        Unit unit = createUnit(unitSplit, rawX, rawY);
        return new Tile(this.width - rawY - 1, this.height - rawX - 1, unit, tileBiome);
    }

    private TileBiome createBiome(String[] biomeSplit, int rawX, int rawY) {
        if (biomeSplit.length == 0)
            throw new RuntimeException(String.format("Illegal tile format at location (%s ; %s) !", rawX, rawY));

        String biome = biomeSplit[0];
        byte biomePlayer = biomeSplit.length > 1 ? Byte.parseByte(biomeSplit[1]) : 0;

        return new TileBiome(TileBiomeType.fromString(biome), biomePlayer);
    }

    private Unit createUnit(String[] unitSplit, int rawX, int rawY) {
        String unitString = unitSplit.length == 0 ? null : unitSplit[0];
        byte unitPlayer = unitSplit.length > 1 ? Byte.parseByte(unitSplit[1]) : 0;
        return unitString == null ? null : UnitType.instantiateUnit(unitString,
                new Location(this.width - rawY - 1, this.height - rawX - 1),
                unitPlayer,
                true /*todo*/);
    }

    @Override
    public String toString() {
        return "Grid{" +
                "width=" + width +
                ", height=" + height +
                ", grid=" + Arrays.toString(grid) +
                '}';
    }
}

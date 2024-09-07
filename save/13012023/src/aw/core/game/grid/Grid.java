package aw.core.game.grid;

import aw.common.tile.biome.TileBiome;
import aw.common.tile.biome.TileBiomeType;
import aw.common.unit.Unit;
import aw.common.unit.UnitType;
import aw.core.game.grid.location.Location;
import ressources.Affichage;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

/**
 * Represente la grille du jeu, avec toutes les tuiles et tous les joueurs, ainsi que la taille de la grille
 */
public class Grid {

    private final int width;
    private final int height;
    private final Tile[] grid;

    /**
     * @param grid version split d'un fichier .awdcmap
     */
    public Grid(String[][] grid) {
        this.height = grid.length;
        if (this.height == 0)
            throw new IllegalArgumentException("Invalid grid !");
        this.width = grid[0].length;
        this.grid = new Tile[this.width * this.height];
        buildGrid(grid);
    }

    /**
     * Dessine la grille sur la fenetre du jeu
     */
    public void draw() {
        for (Tile tile : this.grid) {
            Affichage.dessineImageDansCase(tile.getX(), tile.getY(), tile.getBiome().getImagePath());
            Optional<Unit> unitOptional = tile.getOptionalUnit();
            if (unitOptional.isEmpty())
                continue;
            Unit unit = unitOptional.get();
            Affichage.dessineImageDansCase(tile.getX(), tile.getY(), unit.getImagePath());
        }
    }

    /**
     * @param x abscisse
     * @param y ordonne
     * @return la {@link Tile} de coordonne (x ; y)
     */
    public Tile getTile(int x, int y) {
        return this.grid[bijection(x, y)];
    }

    /**
     * @return la longueur de la carte
     */
    public int getWidth() {
        return this.width;
    }

    /**
     * @return la hauteur de la carte
     */
    public int getHeight() {
        return this.height;
    }

    /**
     * @param x un entier
     * @param y un entier
     * @return x * {@link Grid#height} + y
     */
    private int bijection(int x, int y) {
        return x * this.height + y;
    }

    /**
     * @param location un entier
     * @return {location / {@link Grid#height} ; location % {@link Grid#height}}
     */
    private int[] bijection(int location) {
        return new int[] {location / height, location % this.height};
    }

    /**
     * Construit le tableau {@link Grid#grid} a partir d'un spit d'un fichier .awdcmap
     * @param grid version split d'un fichier .awdcmap
     */
    private void buildGrid(String[][] grid) {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                String tileString = grid[i][j];
                this.grid[bijection(this.width - j - 1, this.height - i - 1)] = createTile(tileString, i, j);
            }
        }
    }

    /**
     * @param tileString string de la tuile dans le fichier .awdcmap
     * @param rawX coordonne brut x
     * @param rawY coordonne brut y
     * @return une instance de Tile obtenu à partir d'un string qui la represente
     */
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

    /**
     * @param biomeSplit type de biome de la tuile
     * @param rawX coordonne brut x
     * @param rawY coordonne brut y
     * @return une instance de Biome obtenu à partir d'un string qui la represente
     */
    private TileBiome createBiome(String[] biomeSplit, int rawX, int rawY) {
        if (biomeSplit.length == 0)
            throw new RuntimeException(String.format("Illegal tile format at location (%s ; %s) !", rawX, rawY));

        String biome = biomeSplit[0];
        byte biomePlayer = biomeSplit.length > 1 ? Byte.parseByte(biomeSplit[1]) : 0;

        return new TileBiome(TileBiomeType.fromString(biome), biomePlayer);
    }

    /**
     * @param unitSplit tableau de string qui characterise une unité
     * @param rawX coordonne brut x
     * @param rawY coordonne brut y
     * @return une instance de Unit obtenue à partir d'un tableau de string qui la represente
     */
    private Unit createUnit(String[] unitSplit, int rawX, int rawY) {
        String unitString = unitSplit.length == 0 ? null : unitSplit[0];
        byte unitPlayer = unitSplit.length > 1 ? Byte.parseByte(unitSplit[1]) : 0;
        return unitString == null ? null : UnitType.instantiateUnit(unitString,
                new Location(this.width - rawY - 1, this.height - rawX - 1),
                unitPlayer,
                true);
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

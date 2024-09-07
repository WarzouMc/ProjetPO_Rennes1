package aw.core.game.grid.location;

/**
 * Class representent une localisation dans un plan 2D
 */
public class Location {

    private int x;
    private int y;

    /**
     * @param x abscisse
     * @param y ordonnee
     */
    public Location(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * @param x une abscisse
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * @param y une ordonnee
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * @return l'abscisse de la localisation
     */
    public int getX() {
        return this.x;
    }

    /**
     * @return l'ordonnee de la localisation
     */
    public int getY() {
        return this.y;
    }
}

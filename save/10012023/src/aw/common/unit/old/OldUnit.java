package aw.common.unit.old;

import aw.core.component.Drawable;
import aw.core.game.grid.location.Location;
import aw.core.game.grid.location.movement.Movable;
import aw.core.game.grid.location.movement.MovementType;
import ressources.Chemins;

@Deprecated
public class OldUnit implements Movable, Drawable {

    public static final int INITIAL_PV = 10;

    private final OldUnitType type;
    private float hp;
    private final Location location;
    private boolean available;
    private byte player;

    public OldUnit(OldUnitType type, Location location, boolean available, byte player) {
        this.type = type;
        this.location = location;
        this.available = available;
        this.hp = INITIAL_PV;
        this.player = player;
    }

    @Override
    public MovementType getMovementType() {
        return this.type.getUnitMovement().getMovementType();
    }

    @Override
    public void setLocation(int x, int y) {
        //todo (il y a une update graphique à faire surrement, à voir comment on organsie ce bordele)
    }

    @Override
    public int getX() {
        return this.location.getX();
    }

    @Override
    public int getY() {
        return this.location.getY();
    }

    public float getHP() {
        return this.hp;
    }

    public byte getPlayer() {
        return this.player;
    }

    public OldUnitType getType() {
        return this.type;
    }

    @Override
    public String getImagePath() {
        return Chemins.getCheminUnite(this.player, this.available, this.type.getImageFile());
    }

    @Override
    public String toString() {
        return "Unit{" +
                "type=" + type +
                ", hp=" + hp +
                ", player=" + player +
                '}';
    }
}

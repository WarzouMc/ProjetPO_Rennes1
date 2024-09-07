package aw.core.game.grid.location.movement;

public interface Movable {

    MovementType getMovementType();

    void setLocation(int x, int y);

    int getX();

    int getY();
}

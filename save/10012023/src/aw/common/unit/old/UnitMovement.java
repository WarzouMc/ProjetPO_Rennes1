package aw.common.unit.old;

import aw.core.game.grid.location.movement.MovementType;

@Deprecated
public class UnitMovement {

    private final MovementType movementType;
    private final int maxMovement;

    public UnitMovement(MovementType movementType, int maxMovement) {
        this.movementType = movementType;
        this.maxMovement = maxMovement;
    }

    public MovementType getMovementType() {
        return this.movementType;
    }

    public int getMaxMovement() {
        return this.maxMovement;
    }
}

package aw.core.game.grid.location.movement;

import java.util.NoSuchElementException;

public class MovementCost {
    private final byte footCost;
    private final byte caterpillarCost;

    public MovementCost(int footCost, int caterpillarCost) {
        this.footCost = (byte) footCost;
        this.caterpillarCost = (byte) caterpillarCost;
    }

    public byte getFootCost() {
        return this.footCost;
    }

    public byte getCaterpillarCost() {
        return this.caterpillarCost;
    }

    public byte getCostFor(MovementType type) {
        return switch (type.toString()) {
            case "FOOT" -> this.footCost;
            case "CATERPILLAR" -> this.caterpillarCost;
            case "AERIAL" -> 1;
            default -> throw new NoSuchElementException();
        };
    }

    public boolean isMovableWith(MovementType type) {
        return getCostFor(type) != -1;
    }
}

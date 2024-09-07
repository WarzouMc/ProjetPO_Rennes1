package aw.core.game.grid.location.movement;

import aw.common.tile.biome.TileBiomeType;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public enum MovementType {

    FOOT, CATERPILLAR, AERIAL;

    private final Map<TileBiomeType, Byte> costMap = Arrays.stream(TileBiomeType.values())
            .collect(Collectors.toMap(biomeType ->
                    biomeType, biomeType -> biomeType.getMovementCost().getCostFor(this), (a, b) -> b, TreeMap::new));


    public int getCost(TileBiomeType biomeType) {
        return this.costMap.get(biomeType);
    }
}

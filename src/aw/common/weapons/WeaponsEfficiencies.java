package aw.common.weapons;

import java.util.Map;
import java.util.TreeMap;

public class WeaponsEfficiencies {

    private final Map<WeaponsType, Float> map = new TreeMap<>();

    public WeaponsEfficiencies(float lmg, float canon, float hmg, float missile, float bomb) {
        map.put(WeaponsType.LIGHT_MACHINE_GUN, lmg);
        map.put(WeaponsType.CANON, canon);
        map.put(WeaponsType.HEAVY_MACHINE_GUN, hmg);
        map.put(WeaponsType.MISSILE, missile);
        map.put(WeaponsType.BOMB, bomb);

        if (map.keySet().size() != WeaponsType.values().length)
            throw new RuntimeException("Missing some weapons !");
    }

    public float getWeaponEfficiency(WeaponsType type) {
        return map.get(type);
    }

}

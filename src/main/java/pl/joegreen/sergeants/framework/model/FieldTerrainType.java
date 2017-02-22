package pl.joegreen.sergeants.framework.model;

import com.google.common.collect.ImmutableMap;

public enum FieldTerrainType {
    EMPTY, MOUNTAIN, FOG, FOG_OBSTACLE, OWNED;
    static final ImmutableMap<Integer, FieldTerrainType> NUMBER_TO_TERRAIN_TYPE = ImmutableMap.of(
            -1, EMPTY,
            -2, MOUNTAIN,
            -3, FOG,
            -4, FOG_OBSTACLE
    );
}

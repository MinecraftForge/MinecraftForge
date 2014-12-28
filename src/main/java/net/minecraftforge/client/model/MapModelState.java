package net.minecraftforge.client.model;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

/*
 * Simple implementation of IModelState via a map and a default value.
 */
public class MapModelState implements IModelState
{
    private final ImmutableMap<IModelPart, TRSRTransformation> map;
    private final TRSRTransformation def;

    public MapModelState(Map<IModelPart, TRSRTransformation> map)
    {
        this(map, TRSRTransformation.identity());
    }

    public MapModelState(Map<IModelPart, TRSRTransformation> map, TRSRTransformation def)
    {
        this.map = ImmutableMap.copyOf(map);
        this.def = def;
    }

    public TRSRTransformation apply(IModelPart part)
    {
        if(!map.containsKey(part)) return def;
        return map.get(part);
    }
}

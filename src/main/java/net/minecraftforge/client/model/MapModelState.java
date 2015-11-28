package net.minecraftforge.client.model;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

/*
 * Simple implementation of IModelState via a map and a default value.
 */
public class MapModelState implements IModelState
{
    private final ImmutableMap<IModelPart, IModelState> map;
    private final IModelState def;

    public MapModelState(Map<IModelPart, ? extends IModelState> map)
    {
        this(map, TRSRTransformation.identity());
    }

    public MapModelState(Map<IModelPart, ? extends IModelState> map, TRSRTransformation def)
    {
        this(map, (IModelState)def);
    }

    public MapModelState(Map<IModelPart, ? extends IModelState> map, IModelState def)
    {
        this.map = ImmutableMap.<IModelPart, IModelState>copyOf(map);
        this.def = def;
    }
	
    public TRSRTransformation apply(IModelPart part)
    {
        if(!map.containsKey(part)) return def.apply(part);
        return map.get(part).apply(part);
    }

    public IModelState getState(IModelPart part)
    {
        if(!map.containsKey(part)) return def;
        return map.get(part);
    }
}

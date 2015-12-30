package net.minecraftforge.client.model;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

/*
 * Simple implementation of IModelState via a map and a default value.
 */
public class SimpleModelState implements IModelState
{
    private final ImmutableMap<? extends IModelPart, TRSRTransformation> map;
    private final Optional<TRSRTransformation> def;

    public SimpleModelState(ImmutableMap<? extends IModelPart, TRSRTransformation> map)
    {
        this(map, Optional.<TRSRTransformation>absent());
    }

    public SimpleModelState(ImmutableMap<? extends IModelPart, TRSRTransformation> map, Optional<TRSRTransformation> def)
    {
        this.map = map;
        this.def = def;
    }

    public Optional<TRSRTransformation> apply(Optional<? extends IModelPart> part)
    {
        if(!part.isPresent() || !map.containsKey(part.get())) return def;
        return Optional.fromNullable(map.get(part.get()));
    }
}

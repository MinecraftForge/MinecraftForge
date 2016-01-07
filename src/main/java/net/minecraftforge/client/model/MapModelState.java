package net.minecraftforge.client.model;

import java.util.Map;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

/*
 * Simple implementation of IModelState via a map and a default value. Provides a full state for each part.
 * You probably don't want to use this.
 */
public class MapModelState implements IModelState
{
    private final ImmutableMap<Wrapper, IModelState> map;
    private final IModelState def;

    public MapModelState(Map<Wrapper, IModelState> map)
    {
        this(map, TRSRTransformation.identity());
    }

    public MapModelState(Map<Wrapper, IModelState> map, TRSRTransformation def)
    {
        this(map, (IModelState)def);
    }

    public MapModelState(Map<Wrapper, IModelState> map, IModelState def)
    {
        this.map = ImmutableMap.<Wrapper, IModelState>copyOf(map);
        this.def = def;
    }
	
    public Optional<TRSRTransformation> apply(Optional<? extends IModelPart> part)
    {
        if(!part.isPresent() || !map.containsKey(part.get())) return def.apply(part);
        return map.get(part.get()).apply(Optional.<IModelPart>absent());
    }

    public IModelState getState(Object obj)
    {
        Wrapper w = wrap(obj);
        if(!map.containsKey(w)) return def;
        return map.get(w);
    }

    public static class Wrapper implements IModelPart
    {
        private final Object obj;

        public Wrapper(Object obj)
        {
            this.obj = obj;
        }

        @Override
        public int hashCode()
        {
            return obj.hashCode();
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Wrapper other = (Wrapper)obj;
            return Objects.equal(this.obj, other.obj);
        }
    }

    public static Wrapper wrap(Object obj)
    {
        return new Wrapper(obj);
    }
}

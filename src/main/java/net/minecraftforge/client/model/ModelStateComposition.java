package net.minecraftforge.client.model;

import com.google.common.base.Optional;

public class ModelStateComposition implements IModelState
{
    private final IModelState first;
    private final IModelState second;

    public ModelStateComposition(IModelState first, IModelState second)
    {
        this.first = first;
        this.second = second;
    }

    public Optional<TRSRTransformation> apply(Optional<? extends IModelPart> part)
    {
        Optional<TRSRTransformation> f = first.apply(part), s = second.apply(part);
        if(f.isPresent() && s.isPresent())
        {
            return Optional.of(f.get().compose(s.get()));
        }
        return f.or(s);
    }
}

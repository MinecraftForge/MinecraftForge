package net.minecraftforge.client.model;

public class ModelStateComposition implements IModelState
{
    private final IModelState first;
    private final IModelState second;

    public ModelStateComposition(IModelState first, IModelState second)
    {
        this.first = first;
        this.second = second;
    }

    public TRSRTransformation apply(IModelPart part)
    {
        return first.apply(part).compose(second.apply(part));
    }
}

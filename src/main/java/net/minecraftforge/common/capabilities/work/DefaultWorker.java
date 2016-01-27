package net.minecraftforge.common.capabilities.work;

/**
 * Default implementation of an {@link IWorker}
 * @author rubensworks
 */
public class DefaultWorker implements IWorker
{
    @Override
    public boolean hasWork()
    {
        return false;
    }

    @Override
    public boolean canWork()
    {
        return false;
    }
}

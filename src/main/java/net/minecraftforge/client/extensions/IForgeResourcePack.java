package net.minecraftforge.client.extensions;

public interface IForgeResourcePack
{
    default boolean isHidden()
    {
        return false;
    }
}

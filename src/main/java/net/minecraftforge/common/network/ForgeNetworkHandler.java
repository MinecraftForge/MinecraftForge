package net.minecraftforge.common.network;

import net.minecraftforge.common.ForgeDummyContainer;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkModHandler;

public class ForgeNetworkHandler extends NetworkModHandler
{
    public ForgeNetworkHandler(ForgeDummyContainer container)
    {
        super(container,container.getClass().getAnnotation(NetworkMod.class));
        configureNetworkMod(container);
    }

    @Override
    public boolean acceptVersion(String version)
    {
        return true;
    }
}

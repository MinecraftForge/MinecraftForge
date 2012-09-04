package cpw.mods.fml.common.modloader;

import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.discovery.ASMDataTable;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkModHandler;

public class ModLoaderNetworkHandler extends NetworkModHandler
{

    private BaseModProxy baseMod;
    public ModLoaderNetworkHandler(ModLoaderModContainer mlmc)
    {
        super(mlmc, null);
    }

    public void setBaseMod(BaseModProxy baseMod)
    {
        this.baseMod = baseMod;
    }

    @Override
    public boolean requiresClientSide()
    {
        return false;
    }

    @Override
    public boolean requiresServerSide()
    {
        return false;
    }

    @Override
    public boolean acceptVersion(String version)
    {
        return baseMod.getVersion().equals(version);
    }
    @Override
    public boolean isNetworkMod()
    {
        return true;
    }
}

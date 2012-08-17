package cpw.mods.fml.common.event;

import net.minecraft.server.MinecraftServer;
import cpw.mods.fml.common.LoaderState.ModState;

public class FMLServerStartingEvent extends FMLStateEvent
{

    private MinecraftServer server;

    public FMLServerStartingEvent(Object... data)
    {
        super(data);
        this.server = (MinecraftServer) data[0];
    }
    @Override
    public ModState getModState()
    {
        return ModState.AVAILABLE;
    }

    public MinecraftServer getServer()
    {
        return server;
    }
}

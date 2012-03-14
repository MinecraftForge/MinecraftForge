package net.minecraft.src;

import java.util.logging.Logger;

public abstract class BaseModMp extends BaseMod
{
    public final int getId()
    {
        return this.toString().hashCode();
    }

    public void modsLoaded()
    {
        ModLoaderMp.initialize();
        super.modsLoaded();
    }

    public void handlePacket(Packet230ModLoader var1, EntityPlayerMP var2) {}

    public void handleLogin(EntityPlayerMP var1) {}

    public void handleSendKey(EntityPlayerMP var1, int var2) {}

    public void getCommandInfo(ICommandListener var1) {}

    public boolean handleCommand(String var1, String var2, Logger var3, boolean var4)
    {
        return false;
    }

    public boolean hasClientSide()
    {
        return true;
    }
}

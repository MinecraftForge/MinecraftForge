package net.minecraftforge.permissions.opbasedimpl;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraftforge.permissions.api.PermBuilder;
import net.minecraftforge.permissions.api.context.IContext;
import cpw.mods.fml.common.FMLCommonHandler;

public class Builder implements PermBuilder<Builder>
{
    IContext user, target;
    String node;
    EntityPlayer player;

    @Override
    public boolean check()
    {
        if (OpPermFactory.deniedPerms.contains(node))
            return false;
        else if (OpPermFactory.allowedPerms.contains(node))
            return true;
        else if (OpPermFactory.opPerms.contains(node))
            return isOp(player.getGameProfile().getName());
        else
            throw new UnregisterredPermissionException(node);
    }

    private static boolean isOp(String username)
    {
        MinecraftServer server = FMLCommonHandler.instance().getSidedDelegate().getServer();

        // SP and LAN
        if (server.isSinglePlayer())
        {
            if (server instanceof IntegratedServer)
                return server.getServerOwner().equalsIgnoreCase(username);
            else
                return server.getConfigurationManager().getOps().contains(username);
        }

        // SMP
        return server.getConfigurationManager().getOps().contains(username);
    }
    
    @Override
    public Builder setUser(EntityPlayer player)
    {
        this.player = player;
        return this;
        
    }

    @Override
    public Builder setPermNode(String node)
    {
        this.node = node;
        return this;
    }

    @Override
    public Builder setTargetContext(IContext context)
    {
        this.target = context;
        return this;
    }

    @Override
    public Builder setUserContext(IContext context)
    {
        this.user = context;
        return this;
    }

    private static class UnregisterredPermissionException extends RuntimeException
    {
        public final String node;
        public UnregisterredPermissionException(String node)
        {
            super("Unregisterred Permission encountered! "+node);
            this.node = node;
        }
    }
}

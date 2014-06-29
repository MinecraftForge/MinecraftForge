package net.minecraftforge.permissions.opbasedimpl;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.permissions.api.PermBuilder;
import net.minecraftforge.permissions.api.PermissionsManager;
import net.minecraftforge.permissions.api.context.IContext;

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
            return PermissionsManager.getGroupForName("OP").isPlayerInGroup(player);
        else
            throw new UnregisterredPermissionException(node);
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

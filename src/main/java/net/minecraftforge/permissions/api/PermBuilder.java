package net.minecraftforge.permissions.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.permissions.api.context.IContext;

public interface PermBuilder<T extends PermBuilder>
{
    boolean check();

    T setUser(EntityPlayer player);

    T setPermNode(String node);

    T setTargetContext(IContext context);

    T setUserContext(IContext context);
}
package net.minecraftforge.event.entity.minecart;

import cpw.mods.fml.common.eventhandler.Cancelable;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;

@Cancelable
public class MinecartInteractEvent extends MinecartEvent
{
    public final EntityPlayer player;

    public MinecartInteractEvent(EntityMinecart minecart, EntityPlayer player)
    {
        super(minecart);
        this.player = player;
    }
}

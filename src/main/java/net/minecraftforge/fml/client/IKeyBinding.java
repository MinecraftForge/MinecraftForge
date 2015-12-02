package net.minecraftforge.fml.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public interface IKeyBinding
{
    boolean onKeyDown(Minecraft minecraft, EntityPlayer player, String context);
}

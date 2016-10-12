package net.minecraftforge.fml.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public abstract class IKeyBinding
{
    /**
     * This method is called on the initial key press.
     * @param minecraft
     * @param player
     * @param context
     * @param ctrl
     * @param shift
     * @param alt
     * @return
     */
    public boolean onKeyDown(Minecraft minecraft, EntityPlayer player, String context, boolean ctrl, boolean shift, boolean alt)
    {
        return false;
    }

    /**
     * TODO: This method is called every tick when key is being held down.
     * @param minecraft
     * @param player
     * @param context
     * @param ctrl
     * @param shift
     * @param alt
     * @return
     */
    public boolean onKeyHold(Minecraft minecraft, EntityPlayer player, String context, boolean ctrl, boolean shift, boolean alt)
    {
        return false;
    }
}

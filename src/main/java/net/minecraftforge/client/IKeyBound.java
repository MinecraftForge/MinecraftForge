package net.minecraftforge.client;

import net.minecraft.entity.player.EntityPlayer;

public interface IKeyBound {
    public void keyPressed(EntityPlayer player);

    public String getKeyName();

    public int[] getKeys();

    public KeyBoundType getKeyBoundType();

    public static enum KeyBoundType {
        IN_WORLD,
        EQUIPPED,
    }
}

package net.minecraftforge.client;

import net.minecraft.client.Minecraft;
import net.minecraft.src.WorldClient;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

public class SkyProviderEmpty extends SkyProvider
{
    public static final SkyProviderEmpty instance = new SkyProviderEmpty();

    @Override
    @SideOnly(Side.CLIENT)
    public void render(float partialTicks, WorldClient world, Minecraft mc)
    {
    }
}

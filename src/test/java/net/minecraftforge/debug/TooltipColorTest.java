package net.minecraftforge.debug;

import net.minecraft.init.Items;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = TooltipColorTest.MODID, name = "Tooltip Color Test", version = "0.1", clientSideOnly = true)
public class TooltipColorTest
{
    public static final String MODID = "tooltipcolortest";

    private static final boolean ENABLE = false;

    public TooltipColorTest()
    {
        if (ENABLE)
        {
            MinecraftForge.EVENT_BUS.register(this);
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void enterDimension(RenderTooltipEvent.Color event)
    {
        if (event.getStack().getItem() == Items.APPLE)
        {
            event.setBackground(0xF0510404);
            event.setBorderStart(0xF0bc0909);
            event.setBorderEnd(0xF03f0f0f);
        }
    }
}
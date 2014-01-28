package net.minecraft.client;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientBrandRetriever
{
    private static final String __OBFID = "CL_00001460";

    public static String getClientModName()
    {
        return FMLCommonHandler.instance().getModName();
    }
}
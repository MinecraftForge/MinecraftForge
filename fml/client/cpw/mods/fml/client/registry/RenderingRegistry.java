package cpw.mods.fml.client.registry;

import java.util.Arrays;

import com.google.common.collect.ObjectArrays;

import net.minecraft.src.RenderPlayer;
import cpw.mods.fml.common.ObfuscationReflectionHelper;

public class RenderingRegistry
{
    public static int addNewArmourRendererPrefix(String armor)
    {
        RenderPlayer.field_77110_j = ObjectArrays.concat(RenderPlayer.field_77110_j, armor);
        return RenderPlayer.field_77110_j.length-1;
    }

}

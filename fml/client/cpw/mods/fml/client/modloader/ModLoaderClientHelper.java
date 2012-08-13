package cpw.mods.fml.client.modloader;

import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Maps;

import net.minecraft.client.Minecraft;
import net.minecraft.src.BaseMod;
import net.minecraft.src.Entity;
import net.minecraft.src.Render;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.modloader.IModLoaderSidedHelper;
import cpw.mods.fml.common.modloader.ModLoaderHelper;
import cpw.mods.fml.common.modloader.ModLoaderModContainer;

public class ModLoaderClientHelper implements IModLoaderSidedHelper
{

    /**
     * @param mod
     * @param inventoryRenderer
     * @return
     */
    public static int obtainBlockModelIdFor(BaseMod mod, boolean inventoryRenderer)
    {
        int renderId=RenderingRegistry.getNextAvailableRenderId();
        ModLoaderBlockRendererHandler bri=new ModLoaderBlockRendererHandler(renderId, inventoryRenderer, mod);
        RenderingRegistry.registerBlockHandler(bri);
        return renderId;
    }


    public static void handleFinishLoadingFor(ModLoaderModContainer mc, Minecraft game)
    {
        BaseMod mod = (BaseMod) mc.getMod();

        Map<Class<? extends Entity>, Render> renderers = Maps.newHashMap();

        mod.addRenderer(renderers);

        for (Entry<Class<? extends Entity>, Render> e : renderers.entrySet())
        {
            RenderingRegistry.registerEntityRenderingHandler(e.getKey(), e.getValue());
        }

        mod.registerAnimation(game);
    }

    public ModLoaderClientHelper(Minecraft client)
    {
        this.client = client;
        ModLoaderHelper.sidedHelper = this;
    }

    private Minecraft client;

    @Override
    public void finishModLoading(ModLoaderModContainer mc)
    {
        handleFinishLoadingFor(mc, client);
    }
}

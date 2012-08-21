package cpw.mods.fml.client.modloader;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.client.Minecraft;
import net.minecraft.src.BaseMod;
import net.minecraft.src.Entity;
import net.minecraft.src.KeyBinding;
import net.minecraft.src.Render;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.modloader.BaseModProxy;
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
        keyBindingContainers = Multimaps.newMultimap(Maps.<ModLoaderModContainer, Collection<ModLoaderKeyBindingHandler>>newHashMap(), new Supplier<Collection<ModLoaderKeyBindingHandler>>()
        {
            @Override
            public Collection<ModLoaderKeyBindingHandler> get()
            {
                return Collections.singleton(new ModLoaderKeyBindingHandler());
            }
        });
    }

    private Minecraft client;
    private static Multimap<ModLoaderModContainer, ModLoaderKeyBindingHandler> keyBindingContainers;

    @Override
    public void finishModLoading(ModLoaderModContainer mc)
    {
        handleFinishLoadingFor(mc, client);
    }


    public static void registerKeyBinding(BaseModProxy mod, KeyBinding keyHandler, boolean allowRepeat)
    {
        ModLoaderModContainer mlmc = (ModLoaderModContainer) Loader.instance().activeModContainer();
        ModLoaderKeyBindingHandler handler = Iterables.getOnlyElement(keyBindingContainers.get(mlmc));
        handler.setModContainer(mlmc);
        handler.addKeyBinding(keyHandler, allowRepeat);
        KeyBindingRegistry.registerKeyBinding(handler);
    }
}

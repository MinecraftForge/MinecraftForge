package cpw.mods.fml.client.modloader;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import net.minecraft.client.Minecraft;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.src.BaseMod;
import net.minecraft.client.*;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.multiplayer.NetClientHandler;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import com.google.common.base.Equivalences;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.MapDifference;
import com.google.common.collect.MapDifference.ValueDifference;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.modloader.BaseModProxy;
import cpw.mods.fml.common.modloader.IModLoaderSidedHelper;
import cpw.mods.fml.common.modloader.ModLoaderHelper;
import cpw.mods.fml.common.modloader.ModLoaderModContainer;
import cpw.mods.fml.common.network.EntitySpawnPacket;
import cpw.mods.fml.common.registry.EntityRegistry.EntityRegistration;

public class ModLoaderClientHelper implements IModLoaderSidedHelper
{
    public static int obtainBlockModelIdFor(BaseMod mod, boolean inventoryRenderer)
    {
        int renderId=RenderingRegistry.getNextAvailableRenderId();
        ModLoaderBlockRendererHandler bri=new ModLoaderBlockRendererHandler(renderId, inventoryRenderer, mod);
        RenderingRegistry.registerBlockHandler(bri);
        return renderId;
    }


    public static void handleFinishLoadingFor(ModLoaderModContainer mc, Minecraft game)
    {
        FMLLog.finer("Handling post startup activities for ModLoader mod %s", mc.getModId());
        BaseMod mod = (BaseMod) mc.getMod();

        Map<Class<? extends Entity>, Render> renderers = Maps.newHashMap(RenderManager.field_78727_a.field_78729_o);

        try
        {
            FMLLog.finest("Requesting renderers from basemod %s", mc.getModId());
            mod.addRenderer(renderers);
            FMLLog.finest("Received %d renderers from basemod %s", renderers.size(), mc.getModId());
        }
        catch (Exception e)
        {
            FMLLog.log(Level.SEVERE, e, "A severe problem was detected with the mod %s during the addRenderer call. Continuing, but expect odd results", mc.getModId());
        }

        MapDifference<Class<? extends Entity>, Render> difference = Maps.difference(RenderManager.field_78727_a.field_78729_o, renderers, Equivalences.identity());

        for ( Entry<Class<? extends Entity>, Render> e : difference.entriesOnlyOnLeft().entrySet())
        {
            FMLLog.warning("The mod %s attempted to remove an entity renderer %s from the entity map. This will be ignored.", mc.getModId(), e.getKey().getName());
        }

        for (Entry<Class<? extends Entity>, Render> e : difference.entriesOnlyOnRight().entrySet())
        {
            FMLLog.finest("Registering ModLoader entity renderer %s as instance of %s", e.getKey().getName(), e.getValue().getClass().getName());
            RenderingRegistry.registerEntityRenderingHandler(e.getKey(), e.getValue());
        }

        for (Entry<Class<? extends Entity>, ValueDifference<Render>> e : difference.entriesDiffering().entrySet())
        {
            FMLLog.finest("Registering ModLoader entity rendering override for %s as instance of %s", e.getKey().getName(), e.getValue().rightValue().getClass().getName());
            RenderingRegistry.registerEntityRenderingHandler(e.getKey(), e.getValue().rightValue());
        }

        try
        {
            mod.registerAnimation(game);
        }
        catch (Exception e)
        {
            FMLLog.log(Level.SEVERE, e, "A severe problem was detected with the mod %s during the registerAnimation call. Continuing, but expect odd results", mc.getModId());
        }
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


    @Override
    public Object getClientGui(BaseModProxy mod, EntityPlayer player, int ID, int x, int y, int z)
    {
        return ((net.minecraft.src.BaseMod)mod).getContainerGUI((EntityClientPlayerMP) player, ID, x, y, z);
    }


    @Override
    public Entity spawnEntity(BaseModProxy mod, EntitySpawnPacket input, EntityRegistration er)
    {
        return ((net.minecraft.src.BaseMod)mod).spawnEntity(er.getModEntityId(), client.field_71441_e, input.scaledX, input.scaledY, input.scaledZ);
    }


    @Override
    public void sendClientPacket(BaseModProxy mod, Packet250CustomPayload packet)
    {
        ((net.minecraft.src.BaseMod)mod).clientCustomPayload(client.field_71439_g.field_71174_a, packet);
    }

    private Map<INetworkManager,NetHandler> managerLookups = new MapMaker().weakKeys().weakValues().makeMap();
    @Override
    public void clientConnectionOpened(NetHandler netClientHandler, INetworkManager manager, BaseModProxy mod)
    {
        managerLookups.put(manager, netClientHandler);
        ((BaseMod)mod).clientConnect((NetClientHandler)netClientHandler);
    }


    @Override
    public boolean clientConnectionClosed(INetworkManager manager, BaseModProxy mod)
    {
        if (managerLookups.containsKey(manager))
        {
            ((BaseMod)mod).clientDisconnect((NetClientHandler) managerLookups.get(manager));
            return true;
        }
        return false;
    }
}

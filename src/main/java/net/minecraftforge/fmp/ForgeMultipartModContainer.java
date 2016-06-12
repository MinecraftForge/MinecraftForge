package net.minecraftforge.fmp;

import java.util.Arrays;
import java.util.Map;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLMissingMappingsEvent;
import net.minecraftforge.fml.common.event.FMLMissingMappingsEvent.MissingMapping;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.Type;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fmp.block.BlockMultipartContainer;
import net.minecraftforge.fmp.block.TileCoverable;
import net.minecraftforge.fmp.block.TileMultipartContainer;
import net.minecraftforge.fmp.capabilities.CapabilityItemSaw;
import net.minecraftforge.fmp.capabilities.CapabilityMicroblockContainerTile;
import net.minecraftforge.fmp.capabilities.CapabilityMultipartContainer;
import net.minecraftforge.fmp.capabilities.CapabilitySlottedCapProvider;
import net.minecraftforge.fmp.client.multipart.MultipartContainerSpecialRenderer.TileCoverableSpecialRenderer;
import net.minecraftforge.fmp.client.multipart.MultipartContainerSpecialRenderer.TileMultipartSpecialRenderer;
import net.minecraftforge.fmp.client.multipart.MultipartStateMapper;
import net.minecraftforge.fmp.event.FMPEventHandler;
import net.minecraftforge.fmp.network.MultipartNetworkHandler;

public final class ForgeMultipartModContainer extends DummyModContainer
{
    public static final String MODID = "forgemultipart";

    public static BlockMultipartContainer multipart;
    public static boolean registered = false;

    public ForgeMultipartModContainer()
    {
        super(new ModMetadata());
        ModMetadata meta = getMetadata();
        meta.modId = MODID;
        meta.name = "Forge Multipart";
        meta.version = ForgeVersion.getVersion();
        meta.credits = "Made possible with help from everybody in #MCMultiPart";
        meta.authorList = Arrays.asList("amadornes");
        meta.description = "Forge Multipart provides the ability for mods to have more than one \"block\" " +
                "in the same blockspace. ";
        meta.url = "https://github.com/MinecraftForge/comingsoon/wiki";
        meta.screenshots = new String[0];
        meta.logoFile = "";
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller)
    {
        bus.register(this);
        return true;
    }

    @Subscribe
    public void preInit(FMLPreInitializationEvent event)
    {
        multipart = new BlockMultipartContainer();
        MinecraftForge.EVENT_BUS.register(new FMPEventHandler());

        CapabilityItemSaw.register();
        CapabilityMicroblockContainerTile.register();
        CapabilityMultipartContainer.register();
        CapabilitySlottedCapProvider.register();
    }

    @Subscribe
    public void init(FMLInitializationEvent event)
    {
        MultipartNetworkHandler.init();
    }

    @SideOnly(Side.CLIENT)
    @Subscribe
    public void initClient(FMLInitializationEvent event)
    {
        ClientRegistry.bindTileEntitySpecialRenderer(TileMultipartContainer.class, new TileMultipartSpecialRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileCoverable.class, new TileCoverableSpecialRenderer<TileCoverable>());

        MinecraftForge.EVENT_BUS.register(this);
    }

    @Subscribe
    public void postInit(FMLPostInitializationEvent event)
    {
    }

    @Subscribe
    public void onMissingMappings(FMLMissingMappingsEvent event)
    {
        for (MissingMapping mapping : event.getAll())
        {
            if (mapping.type == Type.BLOCK
                    && (mapping.resourceLocation.getResourceDomain().equals(MODID)
                            || mapping.resourceLocation.getResourceDomain().equals("mcmultipart"))
                    && mapping.resourceLocation.getResourcePath().equals("multipart"))
            {
                if (registered)
                {
                    mapping.remap(multipart);
                }
                else
                {
                    mapping.ignore();
                }
            }
        }
    }

    @NetworkCheckHandler
    public void onConnectionAttempt(Map<String, String> versions, Side side)
    {

    }

    public static void registerMultipartBlock()
    {
        if (registered)
        {
            return;
        }
        registered = true;

        // Register the multipart container Block and TileEntity
        GameRegistry.register(multipart, new ResourceLocation(ForgeMultipartModContainer.MODID, "multipart"));
        GameRegistry.registerTileEntityWithAlternatives(TileMultipartContainer.Ticking.class, MODID + ":multipart.ticking",
                "mcmultipart:multipart.ticking", "mcmultipart:multipart");
        GameRegistry.registerTileEntityWithAlternatives(TileMultipartContainer.class, MODID + ":multipart.nonticking",
                "mcmultipart:multipart.nonticking");
        // Register the default coverable tile for use with blocks that want to host covers, but don't require a TE
        GameRegistry.registerTileEntityWithAlternatives(TileCoverable.class, MODID + "coverable",
                "mcmultipart:coverable");

        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
        {
            registerModelMapper();
        }
    }

    @SideOnly(Side.CLIENT)
    private static void registerModelMapper()
    {
        ModelLoader.setCustomStateMapper(ForgeMultipartModContainer.multipart, MultipartStateMapper.instance);
    }

}

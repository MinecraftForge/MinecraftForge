package net.minecraftforge.debug;

import net.minecraft.client.gui.MapItemRenderer;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = MapLocatingVillage.MODID, name = "MapLocatingVillage", version = MapLocatingVillage.VERSION, acceptableRemoteVersions = "*")
@Mod.EventBusSubscriber
public class MapLocatingVillage
{
    public static final String MODID = "maplocatingvillage";
    public static final String VERSION = "1.0";

    public static final MapDecoration.Type VILLAGE_MAP_DECORATION = EnumHelper.addMapDecoration("VILLAGE", (byte) 0, true, -1);
    public static final ResourceLocation VILLAGE_MAP_GIVER_NAME = new ResourceLocation(MODID, "village_map_giver");

    @SidedProxy
    public static CommonProxy PROXY;

    @GameRegistry.ObjectHolder("village_map_giver")
    public static Item VILLAGE_MAP_GIVER = null;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        PROXY.preInit();
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().register(new VillageMapGiver().setRegistryName(VILLAGE_MAP_GIVER_NAME));
    }

    public static class CommonProxy
    {
        public void preInit() {}
    }

    public static class ServerProxy extends CommonProxy
    {
        @Override
        public void preInit() {}
    }

    @Mod.EventBusSubscriber(value = Side.CLIENT, modid = MODID)
    public static class ClientProxy extends CommonProxy
    {
        @Override
        public void preInit()
        {
            MapItemRenderer.registerCustomMapDecorationTexture(VILLAGE_MAP_DECORATION, new ResourceLocation(MODID, "textures/map/map_icons.png"));
        }

        @SubscribeEvent
        public static void registerModels(ModelRegistryEvent event)
        {
            ModelResourceLocation location = new ModelResourceLocation(VILLAGE_MAP_GIVER_NAME, "inventory");
            ModelBakery.registerItemVariants(VILLAGE_MAP_GIVER, location);
            ModelLoader.setCustomMeshDefinition(VILLAGE_MAP_GIVER, stack -> location);
        }
    }

    public static class VillageMapGiver extends Item
    {
        VillageMapGiver()
        {
            setCreativeTab(CreativeTabs.MISC);
        }

        @Override
        public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
        {
            if (!worldIn.isRemote)
            {
                BlockPos nearestVillage = worldIn.findNearestStructure("Village", playerIn.getPosition(), true);

                if (nearestVillage != null)
                {
                    ItemStack itemstack = ItemMap.setupNewMap(worldIn, (double) nearestVillage.getX(), (double) nearestVillage.getZ(), (byte) 2, true, true);
                    ItemMap.renderBiomePreviewMap(worldIn, itemstack);
                    MapData.addTargetDecoration(itemstack, nearestVillage, "+", VILLAGE_MAP_DECORATION);

                    playerIn.inventory.addItemStackToInventory(itemstack);
                }
                else
                {
                    playerIn.sendMessage(new TextComponentString("No Village found!"));
                }
            }

            return new ActionResult<>(EnumActionResult.PASS, playerIn.getHeldItem(handIn));
        }

        @Override
        public String getItemStackDisplayName(ItemStack stack)
        {
            return "Locate Village (Right click)";
        }
    }
}
package net.minecraftforge.debug;

import java.util.Collection;
import java.util.Map;

import static net.minecraftforge.oreregistry.OreRegistryConstants.*;

import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.oreregistry.BasicOre;
import net.minecraftforge.oreregistry.OreMaterial;
import net.minecraftforge.oreregistry.OreRegistry;
import net.minecraftforge.oreregistry.OreShape;
import net.minecraftforge.oreregistry.ShapedForgeRecipe;
import net.minecraftforge.oreregistry.ShapelessForgeRecipe;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.Logger;

@Mod(modid = OreRegistryTest.MOD_ID, name = "OreRegistryTest", version = "1.0", acceptableRemoteVersions = "*")
public class OreRegistryTest
{
    static final String MOD_ID = "ore_registry_test";
    static final boolean ENABLED = false;

    private static final ResourceLocation diamond2 = new ResourceLocation(MOD_ID, "diamond_2");
    @ObjectHolder("diamond_2")
    public static final Item DIAMOND_2 = null;
    public static Logger logger;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        if (ENABLED)
        {
            MinecraftForge.EVENT_BUS.register(this);
            logger = event.getModLog();
        }
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        if (ENABLED)
        {
            Collection<OreMaterial> materials = OreRegistry.getMaterials();
            for (OreMaterial material : materials)
            {
                Map<OreShape, BasicOre> shapesMapForMaterial = OreRegistry.getShapesMapForMaterial(material);
                logger.info("Found {} shapes for material: {}", shapesMapForMaterial.size(), material);
                for (BasicOre basicOre : shapesMapForMaterial.values())
                {
                    logger.info("    Shape: {}, Variant Count: {}, Chosen Variant: {}", basicOre.getShape(), basicOre.getVariants().size(), basicOre.get());
                }
            }

        }
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event)
    {
        Item item = new Diamond2();
        item.setCreativeTab(CreativeTabs.MATERIALS);
        item.setRegistryName(diamond2);
        event.getRegistry().register(item);
    }

    @SubscribeEvent
    public void registerModels(ModelRegistryEvent event)
    {
        final ModelResourceLocation name = new ModelResourceLocation(diamond2, "inventory");
        ModelBakery.registerItemVariants(DIAMOND_2, name);
        ModelLoader.setCustomMeshDefinition(DIAMOND_2, is -> name);
    }

    @SubscribeEvent
    public void registerRecipes(RegistryEvent.Register<IRecipe> event)
    {
        logger.info("Registering Crafting Test Recipes:");

        IForgeRegistry<IRecipe> registry = event.getRegistry();

        BasicOre diamond = OreRegistry.addOre(DIAMOND, GEM, new ItemStack(DIAMOND_2));
        // convert legacy diamond into unified diamond
        ShapelessForgeRecipe legacyConversion = new ShapelessForgeRecipe(null, diamond, diamond);
        legacyConversion.setRegistryName("any_diamond_to_unified_diamond");
        registry.register(legacyConversion);

        // make something else using BasicOres
        BasicOre redstoneDust = OreRegistry.getOre(REDSTONE, DUST);
        if (redstoneDust != null)
        {
            ShapedForgeRecipe recipe = new ShapedForgeRecipe(null, Items.ACACIA_BOAT,
                    "XYX", " Z ",
                    'X', diamond, 'Y', redstoneDust, 'Z', Items.APPLE);
            recipe.setRegistryName("pointless_new_boat_recipe");
            registry.register(recipe);
        }

        // more recipes are specified in JSON in the resources folder
    }

    public static class Diamond2 extends Item
    {
        @Override
        public String getItemStackDisplayName(ItemStack stack)
        {
            return "Diamond 2";
        }
    }
}

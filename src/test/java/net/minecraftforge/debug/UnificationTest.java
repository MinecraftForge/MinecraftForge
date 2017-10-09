package net.minecraftforge.debug;

import java.util.Collection;
import java.util.Map;

import static net.minecraftforge.unification.UnificationConstants.*;

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
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.minecraftforge.unification.UnificationManager;
import net.minecraftforge.unification.UnifiedForm;
import net.minecraftforge.unification.Unifier;
import net.minecraftforge.unification.Unified;
import net.minecraftforge.unification.UnifiedMaterial;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.Logger;

@Mod(modid = UnificationTest.MOD_ID, name = "UnificationTest", version = "1.0", acceptableRemoteVersions = "*")
public class UnificationTest
{
    static final String MOD_ID = "unification_test";
    static final boolean ENABLED = true;

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
    public void init(FMLInitializationEvent event)
    {
        if (ENABLED)
        {
            logUnified(UnificationManager.getItemStackUnifier());
            logUnified(UnificationManager.getBlockStateUnifier());
        }
    }

    private static <T> void logUnified(Unifier<T> unifier)
    {
        Collection<UnifiedMaterial> materials = UnificationManager.getMaterials();
        for (UnifiedMaterial material : materials)
        {
            Map<UnifiedForm, Unified<T>> formsMapForMaterial = unifier.getFormsMapForMaterial(material);
            logger.info("Found {} forms for material: {}", formsMapForMaterial.size(), material);
            for (Unified<T> unified : formsMapForMaterial.values())
            {
                logger.info("    Form: {}, Variant Count: {}, Chosen Variant: {}", unified.getForm(), unified.getVariants().size(), unified.get());
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

        // we create our own diamond for testing, but really you should never duplicate vanilla stuff because
        // it always exists and the vanilla recipes do not support unification.
        Unifier<ItemStack> itemUnifier = UnificationManager.getItemStackUnifier();
        itemUnifier.add(DIAMOND, GEM, new ItemStack(item));
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

        Unifier<ItemStack> itemUnifier = UnificationManager.getItemStackUnifier();
        Unified<ItemStack> diamond = itemUnifier.get(DIAMOND, GEM);
        if (diamond != null)
        {
            // convert legacy diamond into unified diamond
            ShapelessOreRecipe legacyConversion = new ShapelessOreRecipe(null, diamond.get(), diamond);
            legacyConversion.setRegistryName("any_diamond_to_unified_diamond");
            registry.register(legacyConversion);
        }

        Unified<ItemStack> redstoneDust = itemUnifier.get(REDSTONE, DUST);
        if (redstoneDust != null)
        {
            ShapedOreRecipe recipe = new ShapedOreRecipe(null, Items.ACACIA_BOAT,
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

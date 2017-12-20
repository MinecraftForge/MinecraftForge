package net.minecraftforge.debug;

import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.brewing.BrewingStandFuelEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = BrewingStandFuelEventTest.MOD_ID, name = "Test for BrewingStandFuelEvent", version = "1.0", acceptableRemoteVersions = "*")
@Mod.EventBusSubscriber
public class BrewingStandFuelEventTest
{
    public static final String MOD_ID = "brewing_stand_fuel_event_test";
    private static final ResourceLocation brewingGoldShovelName = new ResourceLocation(
            MOD_ID, "brewing_gold_shovel");

    @GameRegistry.ObjectHolder("brewing_gold_shovel")
    public static final Item BREWING_GOLD_SHOVEL = null;

    @SubscribeEvent
    public static void onBrewingStandFuelEvent(BrewingStandFuelEvent event)
    {
        ItemStack itemStack = event.getItemStack();
        Item item = itemStack.getItem();
        if (item == Items.SLIME_BALL)
        {
            event.setValue(1);// make slime balls power brewing stands
        }
        else if (item == Items.BLAZE_POWDER)
        {
            event.setValue(0); // do not allow the blaze powder to be fuel
        }
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().register(new BrewingGoldShovel()
                .setRegistryName(brewingGoldShovelName));
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event)
    {
        {
            final ModelResourceLocation location = new ModelResourceLocation(
                    brewingGoldShovelName, "inventory");
            ModelBakery.registerItemVariants(BREWING_GOLD_SHOVEL, location);
            ModelLoader.setCustomMeshDefinition(BREWING_GOLD_SHOVEL,
                    is -> location);
        }
    }

    public static class BrewingGoldShovel extends ItemSpade
    {
        public BrewingGoldShovel()
        {
            super(ToolMaterial.GOLD);
            setCreativeTab(CreativeTabs.TOOLS);
            setMaxStackSize(1);
        }

        @Override
        public int getBrewingStandFuel(ItemStack stack)
        {
            return 25;
        }

        @Override
        public String getItemStackDisplayName(ItemStack stack)
        {
            return "Brewing Golden Shovel";
        }
    }
}

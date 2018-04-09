package net.minecraftforge.debug;

import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.brewing.BrewingFuel;
import net.minecraftforge.common.brewing.BrewingFuelCapabilities;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Mod(modid = BrewingStandFuelTest.MOD_ID, name = "Test for BrewingStandFuelEvent", version = "1.0", acceptableRemoteVersions = "*")
@Mod.EventBusSubscriber
public final class BrewingStandFuelTest
{
    static final String MOD_ID = "brewing_stand_fuel_test";
    private static final ResourceLocation BREWING_GOLD_SHOVEL_NAME = new ResourceLocation(
            MOD_ID, "brewing_gold_shovel");
    private static final ResourceLocation INJECTED_BREWING_FUEL = new ResourceLocation(MOD_ID, "injected_brewing_fuel");

    @GameRegistry.ObjectHolder("brewing_gold_shovel")
    static Item BREWING_GOLD_SHOVEL;

    @SubscribeEvent
    public static void attachItemStackCapabilities(AttachCapabilitiesEvent<ItemStack> event)
    {
        ItemStack stack = event.getObject();
        Item item = stack.getItem();
        if (item == Items.SLIME_BALL)
        {
            event.getCapabilities().put(INJECTED_BREWING_FUEL, new CapabilityProvider(1));
        }
        if (item == Items.BLAZE_POWDER)
        {
            event.getCapabilities().put(INJECTED_BREWING_FUEL, new CapabilityProvider(0));
        }
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().register(new BrewingGoldShovel()
                .setRegistryName(BREWING_GOLD_SHOVEL_NAME));
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event)
    {
        {
            final ModelResourceLocation location = new ModelResourceLocation(
                    BREWING_GOLD_SHOVEL_NAME, "inventory");
            ModelBakery.registerItemVariants(BREWING_GOLD_SHOVEL, location);
            ModelLoader.setCustomMeshDefinition(BREWING_GOLD_SHOVEL,
                    is -> location);
        }
    }

    private static final class CapabilityProvider implements ICapabilityProvider
    {
        private final BrewingFuel instance;

        CapabilityProvider(int fuelValue)
        {
            this.instance = () -> fuelValue;
        }

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
        {
            return capability == BrewingFuelCapabilities.getBrewingFuelCapability();
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
        {
            if (capability == BrewingFuelCapabilities.getBrewingFuelCapability())
            {
                return BrewingFuelCapabilities.getBrewingFuelCapability().cast(instance);
            }
            return null;
        }
    }

    private static class BrewingGoldShovel extends ItemSpade
    {
        BrewingGoldShovel()
        {
            super(ToolMaterial.GOLD);
            setCreativeTab(CreativeTabs.TOOLS);
            setMaxStackSize(1);
        }

        @Nullable
        @Override
        public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt)
        {
            return new CapabilityProvider(20);
        }

        @Override
        public String getItemStackDisplayName(ItemStack stack)
        {
            return "Brewing Golden Shovel";
        }
    }
}

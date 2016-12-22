package net.minecraftforge.test;

import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = "customshieldstests", name = "CustomShieldsTest", version = "0.0.0")
public class CustomShieldsTest
{
    public static final boolean ENABLE = true;

    public static ItemCustomShield testShield = new ItemCustomShield();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        if (ENABLE)
        {
            GameRegistry.register(testShield.setRegistryName("test_shield").setUnlocalizedName("test_shield"));
        }
    }

    private static class ItemCustomShield extends ItemShield
    {

        public ItemCustomShield()
        {
            setMaxDamage(1424);
        }

        @Override public boolean isShield(ItemStack stack)
        {
            return true;
        }
    }
}

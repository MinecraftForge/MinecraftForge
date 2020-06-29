package net.minecraftforge.debug.item;

import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(EnderMaskTest.MODID)
public class EnderMaskTest
{
    public static final String MODID = "ender_mask_test";
    public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, MODID);

    public static RegistryObject<Item> ender_mask = ITEMS.register("ender_mask", () ->
            new ArmorItem(ArmorMaterial.LEATHER, EquipmentSlotType.HEAD, (new Item.Properties().group(ItemGroup.MISC)))
            {
                @Override
                public boolean isEnderMask(ItemStack stack, PlayerEntity player, EndermanEntity endermanEntity)
                {
                    return player.experienceLevel > 10;
                }
            }
    );

    public EnderMaskTest()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(modEventBus);
    }
}

package net.minecraftforge.debug.client.model;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(DynBucketModelTest.MODID)
public class DynBucketModelTest
{
    public static final String MODID = "dyn_bucket_model_test";
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    
    public static final RegistryObject<Item> DRIP_BUCKET = ITEMS.register("drip_bucket", () -> new Item(new Item.Properties().group(ItemGroup.MISC)));
    public static final RegistryObject<Item> LAVA_OBSIDIAN = ITEMS.register("lava_obsidian", () -> new Item(new Item.Properties().group(ItemGroup.MISC)));
    
    public DynBucketModelTest()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(modEventBus);
    }
}

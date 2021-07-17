package net.minecraftforge.debug.item.crafting;

import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(SingleItemRecipeNBTTest.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = SingleItemRecipeNBTTest.MOD_ID)
public class SingleItemRecipeNBTTest
{
    public static final String MOD_ID = "singleitemrecipe_nbt_test";
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
    
    private static final RegistryObject<Block> NBTBLOCK = BLOCKS.register("nbt", ()-> new NBTBlock(Properties.of(Material.STONE)));
    private static final RegistryObject<Item> NBTITEM = ITEMS.register("nbt", () -> new BlockItem(NBTBLOCK.get(), new net.minecraft.item.Item.Properties().tab(ItemGroup.TAB_BUILDING_BLOCKS)));
    
    public SingleItemRecipeNBTTest() {
        final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(modBus);
        BLOCKS.register(modBus);
	}
    
    public static class NBTBlock extends Block
    {

        public NBTBlock(Properties p_i48440_1_)
        {
            super(p_i48440_1_);
        }
        
        @Override
        public void fillItemCategory(ItemGroup p_149666_1_, NonNullList<ItemStack> p_149666_2_) {
            ItemStack stack1 = new ItemStack(this);
            stack1.getOrCreateTag().putString("testtag", "first");
            p_149666_2_.add(stack1);
            ItemStack stack2 = new ItemStack(this);
            stack2.getOrCreateTag().putString("testtag", "second");
            p_149666_2_.add(stack2);
        }
    	
    }
}

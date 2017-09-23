package net.minecraftforge.debug;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = ItemCanDestroyBlocksInCreativeTest.MODID, name = "Item.canDestroyBlockInCreative() Test", version = "1.0", acceptableRemoteVersions = "*")
public class ItemCanDestroyBlocksInCreativeTest
{
    public static final boolean ENABLE = true;
    public static final String MODID = "item_can_destroy_blocks_in_creative_test";

    @Mod.EventBusSubscriber(modid = MODID)
    public static class Registration
    {
        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event)
        {
            if (!ENABLE)
                return;

            Item test = new Item()
            {
                @Override
                public boolean canDestroyBlockInCreative(World world, BlockPos pos, ItemStack stack, EntityPlayer player)
                {
                    return false;
                }
            }.setRegistryName(MODID, "item_test")
             .setUnlocalizedName(MODID + ".item_test")
             .setCreativeTab(CreativeTabs.TOOLS);

            event.getRegistry().register(test);
        }
    }
}

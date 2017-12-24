package net.minecraftforge.debug;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = DebugSearchTabs.MODID, name = "Debug Search Tab", version = "1.0", acceptableRemoteVersions = "*")
public class DebugSearchTabs
{
    public static final String MODID = "debugsearchtab";
    static final boolean ENABLED = false;

    public static final CreativeTabs SEARCH_TAB = !ENABLED ? null : new CreativeTabs(1, "searchtab")
    {
        @SideOnly(Side.CLIENT)
        public ItemStack getTabIconItem()
        {
            return new ItemStack(Items.TOTEM_OF_UNDYING);
        }
        
        @Override
        public boolean hasSearchBar()
        {
        	return true;
        }
        
        @Override
        @SideOnly(Side.CLIENT)
        public void displayAllRelevantItems(NonNullList<ItemStack> items)
        {
        	super.displayAllRelevantItems(items);
        	items.add(new ItemStack(Blocks.BARRIER));
        	items.add(new ItemStack(Blocks.COMMAND_BLOCK));
        	items.add(new ItemStack(Blocks.CHAIN_COMMAND_BLOCK));
        	items.add(new ItemStack(Blocks.REPEATING_COMMAND_BLOCK));
        }
    };
}

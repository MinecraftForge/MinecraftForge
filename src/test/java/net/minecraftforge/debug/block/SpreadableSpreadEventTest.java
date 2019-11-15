package net.minecraftforge.debug.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.GrassBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BlockEvent.SpreadableSpreadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.ObjectHolder;

/**
 * This test mod blocks pistons from moving cobblestone at all except indirectly
 * This test mod adds a block that moves upwards when pushed by a piston
 * This test mod informs the user what will happen the piston and affected blocks when changes are made
 * This test mod makes black wool pushed by a piston drop after being pushed.
 */

@Mod(SpreadableSpreadEventTest.MODID)
@Mod.EventBusSubscriber(bus = Bus.MOD)
public class SpreadableSpreadEventTest {
	
    public static final String MODID = "spreadable_spread_event_test";
        
    @ObjectHolder(SpecialDirtBlock.blockName)
    public static Block SPECIAL_DIRT_BLOCK;
    
    @ObjectHolder(SpecialGrassBlock.blockName)
    public static Block SPECIAL_GRASS_BLOCK;
    
    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        event.getRegistry().register(new SpecialDirtBlock());
        event.getRegistry().register(new SpecialGrassBlock());
    }
    
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().register(new BlockItem(SPECIAL_DIRT_BLOCK, new Item.Properties().group(ItemGroup.BUILDING_BLOCKS)).setRegistryName(MODID, SpecialDirtBlock.blockName));
        event.getRegistry().register(new BlockItem(SPECIAL_GRASS_BLOCK, new Item.Properties().group(ItemGroup.BUILDING_BLOCKS)).setRegistryName(MODID, SpecialGrassBlock.blockName));
    }
    
    @Mod.EventBusSubscriber(modid = SpreadableSpreadEventTest.MODID)
    public static class SpreadableSpreadEventHandler {
        @SubscribeEvent
        public static void spreadableSpreadPre(SpreadableSpreadEvent.Pre event)
        {
        	BlockState spreadable = event.getSpreadable();
            PlayerEntity player = Minecraft.getInstance().player;
            
        	/* MYCELIUM spreading notifies the player */
        	if (spreadable.getBlock() == Blocks.MYCELIUM) 
        	{
                player.sendMessage(new StringTextComponent("Mycelium spreads gray wool instead of Mycelium."));
        	} 
        	else if (spreadable.getBlock() == SPECIAL_GRASS_BLOCK)
        	{
                player.sendMessage(new StringTextComponent("Special Grass Block spread successfully."));	
        	}
        }

        @SubscribeEvent
        public static void spreadableSpreadPost(SpreadableSpreadEvent.Post event)
        {
        	/* MYCELIUM spreads grey wool instead of MYCELIUM */
        	BlockState spreadable = event.getState();
        	if (spreadable.getBlock() == Blocks.MYCELIUM) 
        	{
        		World world = event.getWorld().getWorld();
        		BlockPos pos = event.getPos();
        		world.setBlockState(pos, Blocks.GRAY_WOOL.getDefaultState());
        	}
        }
    }

    public static class SpecialDirtBlock extends Block
    {
		public static final String blockName = "special_dirt_block";
        
        public SpecialDirtBlock()
        {
        	super(Block.Properties.create(Material.EARTH));
        	setRegistryName(MODID, blockName);
         }
        
        @Override
    	public boolean canSustainSpreadableOfType(Block spreadable)
    	{
    		return spreadable == SpreadableSpreadEventTest.SPECIAL_GRASS_BLOCK;
    	}
    }
    
    public static class SpecialGrassBlock extends GrassBlock
    {
		public static final String blockName = "special_grass_block";

        public SpecialGrassBlock()
        {
        	super(Block.Properties.create(Material.EARTH).tickRandomly());
        	setRegistryName(MODID, blockName);
        }
        
        @Override
    	public Block degeneratesInto()
    	{
    		return SpreadableSpreadEventTest.SPECIAL_DIRT_BLOCK;
    	}
    }
}
